package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.adapter.PostAdapter
import com.shihab.kotlintoday.model.Post
import com.shihab.kotlintoday.rest.IMyAPI
import com.shihab.kotlintoday.rest.RetrofitClient
import com.shihab.kotlintoday.utility.showErrorMessage
import com.shihab.kotlintoday.utility.showSuccessMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_recycler_view_with_retrofit_rx.*
import kotlinx.android.synthetic.main.content_recycler_view_with_retrofit_rx.*

class RecyclerViewWithRetrofitRx : AppCompatActivity() {

    internal lateinit var myApi: IMyAPI
    internal lateinit var compositeDisposable: CompositeDisposable

    var TAG: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_with_retrofit_rx)
        setSupportActionBar(toolbar)

        recycler_retro_data.layoutManager = LinearLayoutManager(this)
        recycler_retro_data.setHasFixedSize(true)

        val retrofit = RetrofitClient.instancevalue
        myApi = retrofit.create(IMyAPI::class.java)

        compositeDisposable = CompositeDisposable()
        fetchData()

        //rxTest()
    }

    private fun fetchData() {

        compositeDisposable.add(myApi.post.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe {

                post ->

            displayPost(post)

        })
    }

    private fun displayPost(post: List<Post>?) {

        recycler_retro_data.adapter = PostAdapter(this, post!!)
        showSuccessMessage(this, "Success")

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
