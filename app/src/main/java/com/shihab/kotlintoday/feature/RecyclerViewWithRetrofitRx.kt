package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.widget.LinearLayout
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.adapter.PostAdapter
import com.shihab.kotlintoday.model.Post
import com.shihab.kotlintoday.rest.IMyAPI
import com.shihab.kotlintoday.rest.RetrofitClient
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

import kotlinx.android.synthetic.main.activity_recycler_view_with_retrofit_rx.*
import kotlinx.android.synthetic.main.content_recycler_view_with_retrofit_rx.*

class RecyclerViewWithRetrofitRx : AppCompatActivity() {

    internal lateinit var myApi :IMyAPI
    internal lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view_with_retrofit_rx)
        setSupportActionBar(toolbar)

        recycler_retro_data.layoutManager = LinearLayoutManager(this)

        val retrofit = RetrofitClient.instancevalue
        myApi = retrofit.create(IMyAPI::class.java)
        recycler_retro_data.setHasFixedSize(true)
        fetchData()
    }

    private fun fetchData() {

        compositeDisposable.add(myApi.post.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe{

            post -> displayPost(post)

        })
    }

    private fun displayPost(post: List<Post>?) {

        recycler_retro_data.adapter = PostAdapter(this,post!! )

    }

}
