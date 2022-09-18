package com.shihab.kotlintoday.feature

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_class.*
import kotlinx.android.synthetic.main.content_class.*

/** Value is available in compile time*/
const val maxNumberOfPetAllowed = 5

class ClassActivity : AppCompatActivity() {

    companion object {
        // it works as static
        const val petNumber = 2
        fun getPetHelpLine(): String {
            return "999"
        }
    }

    private val petName: String by lazy { " My Pet Name is Tom" }
    private lateinit var classOwnerName: String
    lateinit var message: String
    lateinit var person: Person
    private val myFirstPetName: String
        get() {
            return "My first pet name is Bingo"
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_class)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            classOwnerName = "Shihab Uddin : this an example of lateinit var"
            message = classOwnerName + "\n" + petName;

        }

        textview.text = "Your name is "

        buttonLazy.setOnClickListener {
            Snackbar.make(it, petName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        buttonCompanion.setOnClickListener {
            message = "My total pet number is $petNumber & pet help line is " + getPetHelpLine()
            message += "\n Max number of pet allowed is $maxNumberOfPetAllowed"
            Snackbar.make(it, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        buttonsSetterGetterTest.setOnClickListener {
            Snackbar.make(it, myFirstPetName, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        buttonScope.setOnClickListener {
            checkScopeFunction()
        }

    }

    private fun checkScopeFunction() {
        person = Person()

        person.let {
            it.firstName = "Shihab"
            it.secondName = "Uddin"
            message = it.fullName()
            Log.d("Let: ", it.fullName())
        }

        person = person.apply {
            this.firstName = "Kotlin"
            this.secondName = "is Boss"
            this.nickname = "Just Raw"
            message += "\n" + this.fullName();
            Log.d("Apply: ", this.fullName())
        }

        person = person.also {
            it.firstName = "Android "
            it.secondName = "is Also Boss"
            it.nickname = "Just Raw"
            message += "\n" + it.fullName() + " " + it.nickname
            Log.d("Also: ", it.fullName() + it.nickname)
        }

        textview.text = message
    }
}
