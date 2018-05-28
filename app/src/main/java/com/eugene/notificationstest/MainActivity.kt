package com.eugene.notificationstest

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOAD_SOMETHING_JOB_ID = 707
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        switch1.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked){
                scheduleMyJob()
            } else {
                cancelMyJob()
            }
        }
    }

    private fun scheduleMyJob() {

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler

        val jobInfo = JobInfo.Builder(LOAD_SOMETHING_JOB_ID,
                ComponentName(this, MyJobService::class.java))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setRequiresCharging(true)
                .setPersisted(true)

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            jobInfo.setPeriodic(TimeUnit.HOURS.toMillis(1), TimeUnit.MINUTES.toMillis(16))
        } else {
            jobInfo.setPeriodic(TimeUnit.SECONDS.toMillis(10))
        }

        jobScheduler.schedule(jobInfo.build())
    }

    private fun cancelMyJob() {

        val jobScheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(LOAD_SOMETHING_JOB_ID)
    }
}
