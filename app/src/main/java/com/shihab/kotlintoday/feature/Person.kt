package com.shihab.kotlintoday.feature

import com.shihab.kotlintoday.feature.iface.IPersonInfo
import com.shihab.kotlintoday.feature.iface.ISessionProvider

// use open keyword when other class want to inherit this class
open class Person(var firstName: String?, var secondName: String?) :
    IPersonInfo, ISessionProvider {

    private lateinit var age: Integer
    private lateinit var petName: String

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

    init {
        /** Acts as constructor */
        println("init 1 $firstName $secondName")
    }

    constructor() : this(null, null) {

        /** if constructor is not defined this secondary constructor called.*/
        println("secondary constructor $firstName $secondName")

    }

    init {
        /** as many init / constructor you may called*/
        println("init 2 $firstName $secondName")
    }


    internal fun fullName(): String {

        return "$firstName $secondName"
    }

    override val providerInfo: String
        get() = "Provider Info : Implemented"

    override fun sessionProvider() {
        println("sessionProvider")
    }


    // Visibility : by default class function and field is public.
    // private field is not accessble to any other class
    // internal works inside a module / package
    //
}