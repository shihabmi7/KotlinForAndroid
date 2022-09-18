package com.shihab.kotlintoday.feature

import android.os.Bundle
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
    lateinit var adam: Person
    lateinit var ibrahim: Person
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
        message = ""

        adam = person.apply {
            firstName = "Adam Apply on"
            secondName = "Hawa"
            message += "\n" + this.fullName();
        }

        ibrahim = person.also {
            it.firstName = "Ibrahim Also"
            it.secondName = "Ismail"
            message += "\n" + it.fullName()
        }

        person.let {
            it.firstName = "let Shihab is"
            it.secondName = "human"
            message += "\n" + it.fullName()
        }

        person.run {
            firstName = "Android "
            secondName = "is run so Fast"
            message += "\n" + fullName() + " " + nickname
        }

        with(person) {
            firstName = "All is an Example "
            secondName = "with scope!"
            message += "\n" + fullName()
        }

        textview.text = message
    }
}
