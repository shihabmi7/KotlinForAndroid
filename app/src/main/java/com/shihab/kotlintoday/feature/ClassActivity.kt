package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.databinding.ActivityClassBinding
import com.shihab.kotlintoday.databinding.ActivityViewBindingBinding


open class ClassActivity : AppCompatActivity() {


    lateinit  var binding: ActivityClassBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClassBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        /**  Persion class has interface implementation*/

        var person = Person()

        // it calles setter everytime
        person.nickname = " tube light"

        //textview.text = person._firstnName + person.secondName +person.nickname

        // textview.text = person. fullName()
        binding.contentClass.textview.text = person.giveFullName(person)


    }

}
