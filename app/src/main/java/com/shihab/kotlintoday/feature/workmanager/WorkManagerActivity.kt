package com.shihab.kotlintoday.feature.workmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.*
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityDialogFragmentAcivityBinding
import com.shihab.kotlintoday.databinding.ActivityWorkManagerBinding
import com.shihab.kotlintoday.feature.workmanager.work.MyWorker
import com.shihab.kotlintoday.utility.LogMe

class WorkManagerActivity : AppCompatActivity() {

    lateinit var binding: ActivityWorkManagerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityWorkManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val data =
            Data.Builder().putString(MyWorker.KEY_INPUT_DATA, "This is run time data").build()
        val constraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val request: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<MyWorker>().setConstraints(constraints).setInputData(data)
                .build()

        binding.content.btPerformTask.setOnClickListener {
            WorkManager.getInstance(this).enqueue(request)
        }

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
            .observe(this, Observer { info ->

                if (info != null && info.state.isFinished) {
                    val myResult = info.outputData.getString(
                        MyWorker.KEY_OUTPUT_DATA
                    )
                    myResult?.let {
                        binding.content.tvStatus.append("\n" + it)
                    }
                }
                binding.content.tvStatus.append("\n" + info.state.name)
                LogMe.i("Work_Info", info.state.name)
            })
    }
}
