package com.example.dermamedicalapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.example.dermamedicalapplication.databinding.ActivityWaterReminderBinding
import java.lang.Integer.parseInt
import kotlin.properties.Delegates

class WaterReminderActivity : AppCompatActivity() {
    lateinit var binding : ActivityWaterReminderBinding

    var currentProgress: Int = 0
    lateinit var progressbar : ProgressBar;
    var amount: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaterReminderBinding.inflate(layoutInflater)
        progressbar = binding.progressBar
        setContentView(binding.root)

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
            if(currentProgress < progressbar.max) {
                addProgress()
            }
        }

        binding.ReminderToggle.setOnClickListener {

            startActivity(Intent(this, ReminderSettingActivity::class.java))

        }

    }

    private fun addProgress()
    {
        if (binding.waterAmount.checkedRadioButtonId == -1)
        {
            android.app.AlertDialog.Builder(this)
                .setTitle("Water amount is empty")
                .setMessage("Please select water amount")
                .setPositiveButton("Okay"){_,_ ->}
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


        }
    }
}