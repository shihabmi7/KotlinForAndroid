package com.shihab.kotlintoday.feature.flow

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.shihab.kotlintoday.databinding.ActivityKotlinFlowBinding
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

class KotlinFlowActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKotlinFlowBinding
    val viewModel: FlowViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKotlinFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.main.buttonLiveData.setOnClickListener {
            viewModel.liveDataClicked()
        }

        binding.main.buttonStateFlow.setOnClickListener {
            viewModel.stateFlowClicked()
        }

        binding.main.buttonSharedFlow.setOnClickListener {
            viewModel.sharedFlowClicked()
        }

        binding.main.buttonNormalFlow.setOnClickListener {
            lifecycleScope.launchWhenCreated {
                viewModel.triggerNormalFlow().collectLatest {
                    binding.main.txtNormalFlow.text = it.toString();
                }
            }
        }

        viewModel.liveData.observe(this) {
            binding.main.txtLiveData.text = it
        }


        lifecycleScope.launchWhenCreated {
            /**   State Flow
            - we can map, filter in flow operator
            - testing capability is good as it's under coroutine
            -  It's a Hot flows that mean it emits though no collector is there
            - triggers again when configuration is changes ex. rotation, language change
            - should used launchWhen created as it's state holder
             */
            viewModel.stateFlow.collectLatest {
                binding.main.txtState.text = it
            }
        }

        lifecycleScope.launchWhenStarted {
            /** Shared Flow
             * One time emition
             * Not hold the state
             * better / example navigation from fragment to fragment or A to A
             * */
            viewModel.sharedFlow.collectLatest {
                binding.main.txtShared.text = it
            }
        }
    }
}