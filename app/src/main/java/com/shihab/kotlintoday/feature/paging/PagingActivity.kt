package com.shihab.kotlintoday.feature.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityPagingBinding
import kotlinx.coroutines.flow.collectLatest

class PagingActivity : AppCompatActivity() {

    lateinit var activityPagingBinding: ActivityPagingBinding
    lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityPagingBinding = DataBindingUtil.setContentView(this, R.layout.activity_paging)

        initRecyclerView()
        initViewModel()
    }

    private fun initRecyclerView() {
        activityPagingBinding.recyclerPaging.apply {
            val decoration  = DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            recyclerViewAdapter = RecyclerViewAdapter()
            adapter = recyclerViewAdapter
        }
    }

    private fun initViewModel() {
        val viewModel  = ViewModelProvider(this).get(PagingActivityVM::class.java)
        lifecycleScope.launchWhenCreated {
            viewModel.getListData().collectLatest {
                recyclerViewAdapter.submitData(it)
            }
        }
    }

}