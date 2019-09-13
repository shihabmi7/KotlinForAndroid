package com.shihab.kotlintoday.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_adapter.view.*
import kotlinx.android.synthetic.main.item_notifications.view.*

class PostViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview) {

    var title = itemview.tv_title
    var details = itemview.details
}