package com.shihab.kotlintoday.feature.workmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.feature.workmanager.work.MyWorker
import com.shihab.kotlintoday.utility.LogMe
import kotlinx.android.synthetic.main.activity_work_manager.*
import kotlinx.android.synthetic.main.content_work_manager.*

class WorkManagerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_manager)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data =
            Data.Builder().putString(MyWorker.KEY_INPUT_DATA, "This is run time data").build()
        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<MyWorker>().setConstraints(constraints).setInputData(data)
                .build()

        bt_performTask.setOnClickListener {
            WorkManager.getInstance(this).enqueue(request)
        }

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this, Observer { info ->

                if (info != null && info.state.isFinished) {
                    val myResult = info.outputData.getString(
                        MyWorker.KEY_OUTPUT_DATA
                    )
                    myResult?.let {
                        tvStatus.append("\n" + it)
                    }
                }
                tvStatus.append("\n" + info.state.name)
                LogMe.i("Work_Info", info.state.name)
            })
    }
}
