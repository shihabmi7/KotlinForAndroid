package com.shihab.kotlintoday.feature.custom_spinner

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ItemSpinnerProfessionBinding


class MoodAdapter(context: Context, professionList: List<ReqDocsItem>) : ArrayAdapter<ReqDocsItem>(context, 0, professionList) {

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val currentItem = getItem(position)
//        val view = recycledView
//                ?: LayoutInflater.from(context).inflate(R.layout.item_spinner_profession, parent, false)
//
//
        val view = ItemSpinnerProfessionBinding.inflate(LayoutInflater.from(parent.context))

        view.tvMood.text = currentItem!!.name
        return view.root
    }
}