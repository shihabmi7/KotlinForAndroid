package com.shihab.kotlintoday.feature

class Person(val _firstnName: String = "shihab", val secondName: String = "uddin") {


    /**  we can define value here
    //
    //    val firstName:String = _firstnName
    //    val secondName:String = secondName
    //
     */
    var nickname: String? = null
        set(value) {
            field = " My nick name is $value"
        }
        get() {
            return field
        }

    init {
        /** Acts as constructor */
        println("init 1 $_firstnName $secondName")
    }

    constructor() : this("Shihab", "Uddin") {

        /** if constructor is not defined this secondary constructor called.*/
        println("secondary constructor $_firstnName $secondName")

    }

    init {
        /** as many init / constructor you may called*/
        println("init 2 $_firstnName $secondName")
    }


    internal fun fullName(): String {

        val nicknames = nickname ?: " no nickname"

        return _firstnName + secondName + nicknames
    }


    // Visibility : by default class function and field is public.
    // private field is not accessble to any other class
    // internal works inside a module / package
    //
}