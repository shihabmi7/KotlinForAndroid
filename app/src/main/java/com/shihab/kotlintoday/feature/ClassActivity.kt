package com.shihab.kotlintoday.feature

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import com.shihab.kotlintoday.feature.`interface`.IPersonInfo

import kotlinx.android.synthetic.main.activity_class.*
import kotlinx.android.synthetic.main.content_class.*

class ClassActivity : AppCompatActivity(), IPersonInfo {
    override fun giveFullName(person: Person) :String{
        print("full info called")

        return  person.fullName()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }


        var person = Person()

         // it calles setter everytime
         person.nickname = " tube light"

        //textview.text = person._firstnName + person.secondName +person.nickname

//        textview.text = person. fullName()
        textview.text = giveFullName(Person())


    }

}
