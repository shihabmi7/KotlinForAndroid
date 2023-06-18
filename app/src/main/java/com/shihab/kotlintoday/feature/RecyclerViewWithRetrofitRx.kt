package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.adapter.PostAdapter
import com.shihab.kotlintoday.databinding.ActivityRecyclerViewWithRetrofitRxBinding
import com.shihab.kotlintoday.model.Post
import com.shihab.kotlintoday.rest.ApiService
import com.shihab.kotlintoday.rest.RetrofitClient
import com.shihab.kotlintoday.utility.setDivider
import com.shihab.kotlintoday.utility.showSuccessMessage

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class RecyclerViewWithRetromfitRx : AppCompatActivity() {

    internal lateinit var binding: ActivityRecyclerViewWithRetrofitRxBinding
    private lateinit var compositeDisposable: CompositeDisposable

    var TAG: String = "RecyclerWithRetrofitRx"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerViewWithRetrofitRxBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

       binding.content. recyclerRetroData.layoutManager = LinearLayoutManager(this)
        binding.content.  recyclerRetroData.setHasFixedSize(true)

        compositeDisposable = CompositeDisposable()
        fetchData()

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun displayPost(post: List<Post>?) {
        binding.content. recyclerRetroData.setDivider(R.drawable.recycler_view_divider)
        binding.content. recyclerRetroData.adapter = PostAdapter(this, post!!)
        showSuccessMessage(this, "Success")

    }

    private fun fetchData() {

        compositeDisposable.add(RetrofitClient.getAPIInterface().getPost.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe { post ->

                displayPost(post)

            })
    }

    /*private fun rxTest() {

        var taskObservable =
            Observable.fromIterable(DataSource.getTaskList()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).filter {

                }

        taskObservable.subscribe(object : Observer<Task> {
            override fun onComplete() {
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(task: Task) {
                Log.d(TAG, "onNext: : " + task.description)
            }

            override fun onError(e: Throwable) {
            }

        })
    }*/
}
