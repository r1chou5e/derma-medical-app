package com.example.dermamedicalapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.dermamedicalapplication.databinding.ActivityQuestionBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import java.text.SimpleDateFormat
import java.util.*

class QuestionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBinding
    private lateinit var calendar: Calendar

    companion object {
        lateinit var imageUri: Uri
        lateinit var sex: String
        lateinit var pruritus_Pain: String
        lateinit var onset: String
        lateinit var birth: String
    }

    private val startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val resultCode = result.resultCode
        val data = result.data
        if (resultCode == Activity.RESULT_OK) {
            getValueForm()
            // Image Uri will not be null for RESULT_OK
            imageUri = data?.data!!
            startActivity(Intent(this, TakePictureActivity::class.java))
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getValueForm() {
        sex = when (checkRadioButton(binding.genderAnswerRg)) {
            "Nam" -> "m"
            "Nữ" -> "f"
            else -> ""
        }

        pruritus_Pain = when (checkRadioButton(binding.symptomAnswerRg)) {
            "Trong vòng 1 tuần" -> "1"
            "Trong vòng 1 tháng" -> "2"
            "Trong vòng 1 năm" -> "3"
            "Trên 1 năm" -> "4"
            else -> ""
        }

        onset = when (checkRadioButton(binding.levelAnswerRg)) {
            "Không có" -> "1"
            "Nhẹ - Trung bình" -> "2"
            "Nặng" -> "3"
            else -> ""
        }
        birth = calendar[Calendar.YEAR].toString()
    }

    private fun checkRadioButton(radioGroup: RadioGroup): String {
        val checked = radioGroup.checkedRadioButtonId
        return if (checked != -1) {
            val radioButton = findViewById<RadioButton>(checked)
            radioButton.text.toString()
        } else {
            ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        binding.dobEt.setText(dateFormatter.format(calendar.time))

        binding.bottomNavigationView.selectedItemId = R.id.diagnose

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    overridePendingTransition(0, 0)
                }
                R.id.profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    overridePendingTransition(0, 0)
                }
            }
            true
        }

        binding.scanBtn.setOnClickListener {
            ImagePicker.Companion.with(this)
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    fun showDatePickerDialog(view: View) {
        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                calendar.set(year, month, dayOfMonth)
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.dobEt.setText(dateFormatter.format(calendar.time))
            },
            calendar[Calendar.YEAR],
            calendar[Calendar.MONTH],
            calendar[Calendar.DAY_OF_MONTH]
        )

        datePickerDialog.show()
    }

}