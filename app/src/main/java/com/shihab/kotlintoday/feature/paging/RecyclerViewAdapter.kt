package com.shihab.kotlintoday.feature.paging

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.model.loadImage

class RecyclerViewAdapter() :
    PagingDataAdapter<CharacterData, RecyclerViewAdapter.MyViewHolder>(DiffUtilCallBack()) {

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.bind(getItem(position)!!, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater =
            LayoutInflater.from(parent.context).inflate(R.layout.item_paging, parent, false)

        return MyViewHolder(inflater)
    }

    class MyViewHolder(var view: View) : RecyclerView.ViewHolder(view) {

        val imageView: ImageView = view.findViewById(R.id.imageView)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)

        fun bind(data: CharacterData, position: Int) {
            tvName.text = data.name
            tvDesc.text = data.species

            imageView.loadImage(data.image!!)

            tvName.setOnClickListener {

                Toast.makeText(it.context, "Name: " + data.name, Toast.LENGTH_LONG).show();
            }

            imageView.setOnClickListener {

                Toast.makeText(it.context, "Image clicked : position $position", Toast.LENGTH_LONG)
                    .show();
            }



            view.setOnClickListener {
                Toast.makeText(it.context, "Row clicked : Position is $position", Toast.LENGTH_LONG)
                    .show();

            }

        }
    }

    class DiffUtilCallBack : DiffUtil.ItemCallback<CharacterData>() {
        override fun areItemsTheSame(oldItem: CharacterData, newItem: CharacterData): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: CharacterData, newItem: CharacterData): Boolean {
            return oldItem.name == newItem.name
                    && oldItem.species == newItem.species
        }
    }

}