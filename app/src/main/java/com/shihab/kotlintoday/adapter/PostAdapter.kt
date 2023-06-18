package com.shihab.kotlintoday.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shihab.kotlintoday.databinding.ItemAdapterBinding
import com.shihab.kotlintoday.model.Post

class PostAdapter(internal var context: Context, internal var postList: List<Post>) :
    RecyclerView.Adapter<PostViewHolder>() {

    private val TAG: String = "PostAdapter"
    var DURATION: Long = 500
    private var on_attach = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        return PostViewHolder(
            ItemAdapterBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        var post = postList[position]

        holder.itemviewBinding.tvTitle.text = post.title
        holder.itemviewBinding.details.text = post.body
        // setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun setAnimation(itemView: View, i: Int) {
        var i = i
        if (!on_attach) {
            i = -1
        }
        val isNotFirstItem = i == -1
        i++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator: ObjectAnimator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay = if (isNotFirstItem) DURATION / 2 else i * DURATION / 3
        animator.duration = 500
        animator.start()
        animatorSet.play(animator)
    }

    /*override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                Log.d(TAG, "onScrollStateChanged: Called $newState")
                on_attach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }*/
}