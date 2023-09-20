package com.shino72.location.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.shino72.location.MainActivity
import com.shino72.location.R
import com.shino72.location.db.Entity.Plan
import com.shino72.location.db.dao.PlanDao
import com.shino72.location.repository.RoomRepository
import com.shino72.location.ui.DetailActivity
import com.shino72.location.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var roomRepository: RoomRepository

    @Inject
    lateinit var planDao: PlanDao

    lateinit var notificationManager: NotificationManager

    override fun onReceive(context: Context, intent: Intent) {
        // 재부팅 시 알람 재등록
        if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)){
            val alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager

            notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

            // 알람 데이터 가져오기
            runBlocking {
                val alarmData = planDao.getPlansAfterTimestamp(System.currentTimeMillis())
                alarmData?.let {plans ->
                    plans.forEach {plan ->
                        Log.d("progress","${plan.place}")
                        val pendingIntent = PendingIntent.getBroadcast(
                            context, plan.timestamp.toInt(), intent, PendingIntent.FLAG_IMMUTABLE
                        )
                        val calendar = Calendar.getInstance()
                        calendar.timeInMillis = plan.timestamp
                        alarmManager.setExact(
                            AlarmManager.RTC_WAKEUP,
                            plan.timestamp,
                            pendingIntent
                        )
                        deliverNotification(context, plan, plan.id)
                        Log.d("progress","${plan.contents} 알람 설정")
                    }
                }
            }

        }
        try {
            val data = intent.getSerializableExtra("detail") as Plan
            notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE) as NotificationManager

            // 알림 체널 설정
            createNotificationChannel()
            deliverNotification(context, data, data.id)
        }
        catch (_: Exception) {}
    }
    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel("alarm", "알림", NotificationManager.IMPORTANCE_LOW)
        notificationChannel.enableLights(false) // 불빛
        notificationChannel.enableVibration(false) // 진동
        notificationChannel.description = "알림"
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun deliverNotification(context : Context, plan: Plan, id : Int) {

        // 알람을 눌렀을 때 실행 - Intent
        val contentIntent = Intent(context, DetailActivity::class.java)
        contentIntent.putExtra("detail",plan)
        contentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(contentIntent)

        val contentPendingIntent = PendingIntent.getActivity(
            context,
            plan.timestamp.toInt(),
            contentIntent,  PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, id.toString())
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle("계획명 : ${plan.contents}")
            .setContentText("${plan.place}에 있나요? 완료해주세요!")
            .setContentIntent(contentPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setChannelId("alarm")
            .setDefaults(NotificationCompat.DEFAULT_ALL)
        notificationManager.notify(id, builder.build())
    }

}