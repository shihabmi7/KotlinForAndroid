package com.shihab.kotlintoday.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.model.Post

class PostAdapter(internal var context: Context, internal var postList: List<Post>) :
    RecyclerView.Adapter<PostViewHolder>() {

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {

        var post = postList[position]

        holder.title.text = post.title
        holder.details.text = post.body

    }

    override fun getItemCount(): Int {

        return postList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {

        val itemview =
            LayoutInflater.from(parent.context).inflate(R.layout.item_adapter, parent, false)

        return PostViewHolder(itemview)

    }
}