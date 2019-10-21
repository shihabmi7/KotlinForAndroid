package com.shihab.kotlintoday.feature.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.shihab.kotlintoday.R
import kotlinx.android.synthetic.main.activity_coroutine.*
import kotlinx.android.synthetic.main.content_coroutine.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoroutineActivity : AppCompatActivity() {

    // co-routine is not thread. Its a task.
    // it can operates under a thread
    //

    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coroutine)
        setSupportActionBar(toolbar)


        button.setOnClickListener {

            // IO = local db , network ;   Main= interacting with UI ;  default = heavy operation
            CoroutineScope(IO).launch {

                fakeAPIRequest()
            }

        }
    }

    private suspend fun fakeAPIRequest() {

        val result = getResult1FromAPI()
        println("debug:" + result)
        // textView.setText(result) /** It will bring crash on device*/
        setTextOnMainThread(result)

        val result2 = getResult2FromAPI()
        setTextOnMainThread(result2)

    }


    fun setNewText(message: String) {

        val newText = textView.text.toString() + "\n$message"
        textView.text = newText
    }

    suspend fun setTextOnMainThread(message: String) {
        CoroutineScope(Main).launch {
            setNewText(message)
        }


        // or you can use

        /* withContext(Main) {
            setNewText(message)
        }*/
    }

    suspend fun getResult1FromAPI(): String {

        logThread("getResult1FromAPI")

        delay(1000) // only pause to this co routine

        return RESULT_1
        //Thread.sleep(1000) // this will pause thread and all co routines in side the thread

    }

    suspend fun getResult2FromAPI(): String {

        logThread("getResult2FromAPI")

        delay(2000) // only pause to this co routine

        return RESULT_2
        //Thread.sleep(1000) // this will pause thread and all co routines in side the thread

    }

    private fun logThread(method: String) {

        println("debug : ${method}: ${Thread.currentThread().name}")
    }
}
