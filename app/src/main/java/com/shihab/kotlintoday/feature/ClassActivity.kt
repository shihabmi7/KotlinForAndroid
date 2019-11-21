package com.shihab.kotlintoday.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_class.*
import kotlinx.android.synthetic.main.content_class.*

open class ClassActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        /**  Persion class has interface implementation*/

        var person = Person()

        // it calles setter everytime
        person.nickname = " tube light"

        //textview.text = person._firstnName + person.secondName +person.nickname

        // textview.text = person. fullName()
        textview.text = person.giveFullName(person)


    }

}
