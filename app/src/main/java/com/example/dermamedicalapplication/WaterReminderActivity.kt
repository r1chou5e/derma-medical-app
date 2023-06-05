package com.example.dermamedicalapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dermamedicalapplication.databinding.ActivityWaterReminderBinding
import java.lang.Integer.parseInt
import java.util.Calendar


class WaterReminderActivity : AppCompatActivity() {
    lateinit var binding : ActivityWaterReminderBinding
    lateinit var progressbar : ProgressBar;

    companion object {
        var currentProgress:Int = 0
    }
    var amount: Int = 0
    // Create object of SharedPreferences.
    lateinit var sharedPref: SharedPreferences
    //now get Editor
    lateinit var  editor: SharedPreferences.Editor




    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, HomeActivity::class.java)
        binding = ActivityWaterReminderBinding.inflate(layoutInflater)
        progressbar = binding.progressBar
        setContentView(binding.root)
        sharedPref  = getSharedPreferences("Water", MODE_PRIVATE);
        editor = sharedPref.edit();
        if(sharedPref.getInt("waterAmount", 0) == null) {
            currentProgress = 0
            editor.putInt("waterAmount", currentProgress)
            editor.apply()
            editor.commit()
        }else
        {
            currentProgress = sharedPref.getInt("waterAmount", 0)
        }
        binding.bottomNavigationView.selectedItemId = R.id.WaterReminder

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.home ->
                {
                    startActivity(Intent(this, HomeActivity::class.java))
                }
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.diagnose -> {
                    startActivity(Intent(this, QuestionActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }
        binding.otherChoice.setOnClickListener {
            if(binding.otherChoice.isChecked)
            {
                binding.other.visibility = View.VISIBLE
            }else
            {
                binding.other.visibility = View.INVISIBLE
            }
        }

        if (progressbar.progress != 0)
        {
            currentProgress= binding.progressBar.progress
        }

        binding.addBtn.setOnClickListener{
            if((currentProgress as Int) < progressbar.max) {
                addProgress()
            }
        }

        binding.ReminderToggle.setOnClickListener {

            startActivity(Intent(this, ReminderSettingActivity::class.java))

        }
        progressbar.max = 2000
        progressbar.progress = currentProgress
        binding.textView2.text = "${currentProgress}/2000 ml"






    }

    private fun addProgress()
    {
        if (binding.waterAmount.checkedRadioButtonId == -1)
        {
            android.app.AlertDialog.Builder(this)
                .setTitle("Chưa có mức nước nào")
                .setMessage("Vui lòng chọn lượng nước !")
                .setPositiveButton("Đồng ý"){_,_ ->}
                .show()
        }else {
            if (binding.ml300.isChecked) {
                amount = 300
            } else if (binding.ml400.isChecked) {
                amount = 400
            } else if (binding.ml600.isChecked) {
                amount = 600
            } else if (binding.otherChoice.isChecked) {
                amount = parseInt(binding.otherField.text.toString())
            }

            progressbar.max = 2000
            currentProgress += amount
            progressbar.progress = currentProgress
            binding.textView2.text = "${currentProgress}/2000 ml"
            editor.putInt("waterAmount", currentProgress);
            //commits your edits
            editor.commit();


            if(currentProgress >= 2000)
            {
                val builder = AlertDialog.Builder(this)
                getSystemService(LAYOUT_INFLATER_SERVICE)
                val height = LinearLayout.LayoutParams.WRAP_CONTENT
                val width = LinearLayout.LayoutParams.WRAP_CONTENT
                val view = layoutInflater.inflate(R.layout.drinking_goal_reach_popup, null)
                builder.setView(view)
                builder.create()
                builder.show()




            }


        }
    }
}