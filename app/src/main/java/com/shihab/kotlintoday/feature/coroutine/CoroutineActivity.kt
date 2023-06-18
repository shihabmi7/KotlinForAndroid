package com.shihab.kotlintoday.feature.coroutine

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.shihab.kotlintoday.databinding.ActivityCoroutineBinding

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis

class CoroutineActivity : AppCompatActivity() {

    // co-routine is not thread. Its a task.
    // it can operates under a thread
    //

    private val RESULT_1 = "RESULT #1"
    private val RESULT_2 = "RESULT #2"

    val TIMEOUT = 1900L

    lateinit var binding: ActivityCoroutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityCoroutineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)


        binding.content.button.setOnClickListener {

            // IO = local db , network ;   Main= interacting with UI ;  default = heavy operation
            CoroutineScope(IO).launch {

                fakeAPIRequest()

            }

        }

        binding.content.buttonAsync.setOnClickListener {

            fakeAPIRequestWithAsync()
        }


        binding.content.buttonTimeOut.setOnClickListener {

            CoroutineScope(IO).launch {

                apiwithTimeOut()
            }
        }
    }
    private suspend fun apiwithTimeOut(){

        withContext(IO){

            val job =  withTimeoutOrNull(TIMEOUT) {

                val resultOne = getResult1FromAPI()
                println("debug: result 1 : ${resultOne}")

                val resultTwo = getResult2FromAPI()
                println("debug: result 2: ${resultTwo}")


            }

            if (job == null){

                val cancelMessage = " Cancelling job... Job took longer than $TIMEOUT ms"
                println("debug: ${cancelMessage}")
                setTimeoutTextview(cancelMessage)
            }
        }

    }

    fun setTimeoutTextview(message: String){

        CoroutineScope(Main).launch{
            binding.content.textViewTimeout.text = message
        }
    }
    private fun fakeAPIRequestWithAsync() {

        CoroutineScope(IO).launch {

            var executionTime = measureTimeMillis {

                var result_one: Deferred<String> = async {
                    println("debug: launching job1: ${Thread.currentThread().name}")
                    getResult1FromAPI()
                }

                var result_two: Deferred<String> = async {
                    println("debug: launching job2: ${Thread.currentThread().name}")
                    getResult1FromAPI()
                }

                setAsyncTextOnMainThread("Got ${result_one.await()} + ${result_two.await()}")

            }
            println("debug: total time elapsed: ${executionTime}")
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

        val newText = binding.content.textView.text.toString() + "\n$message"
        binding.content.textView.text = newText
    }

    fun setAsyncText(message: String) {

        val newText = binding.content.textViewAsysnc.text.toString() + "\n$message"
        binding.content.textViewAsysnc.text = newText
    }

    fun setTextOnMainThread(message: String) {
        CoroutineScope(Main).launch {
            setNewText(message)
        }


        // or you can use

        /* withContext(Main) {
            setNewText(message)
        }*/
    }


    fun setAsyncTextOnMainThread(message: String) {
        CoroutineScope(Main).launch {
            setAsyncText(message)
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
