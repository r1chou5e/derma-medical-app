package com.example.dermamedicalapplication

import android.app.AlarmManager
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dermamedicalapplication.databinding.ActivityReminderSettingBinding
import java.util.Calendar

class ReminderSettingActivity : AppCompatActivity() {

    lateinit var binding: ActivityReminderSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReminderSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNavigationView.selectedItemId = R.id.ReminderSetting

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

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        createNotificationChannel()
        binding.confirmBtn.setOnClickListener {
            if(binding.notifSwitch.isChecked) {scheduleNotification()} }

    }

    private fun scheduleNotification() {
        val intent = Intent(applicationContext, Notification::class.java)
        val title = "Water Reminder"
        val message = "Time to drink water 💧"
        intent.putExtra(titleExtra, title)
        intent.putExtra(messageExtra, message)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val startTime = getStartTime()
        val endTime = getEndTime()
        if (System.currentTimeMillis() >= endTime)
        {
            alarmManager.cancel(pendingIntent)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            startTime,
            2 * 60 * 60 * 1000,
            pendingIntent
        )
        showAlert()
    }

    private fun showAlert() {


        AlertDialog.Builder(this)
            .setTitle("Cài đặt thông báo")
            .setMessage("Cài đặt đã được lưu")
            .setPositiveButton("Ok") { _, _ -> }
            .show()
    }

    private fun getStartTime(): Long {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker.hour)
            set(Calendar.MINUTE, binding.timePicker.minute)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getEndTime(): Long {
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, binding.timePicker2.hour)
            set(Calendar.MINUTE, binding.timePicker2.minute)
            set(Calendar.SECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun createNotificationChannel() {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}














