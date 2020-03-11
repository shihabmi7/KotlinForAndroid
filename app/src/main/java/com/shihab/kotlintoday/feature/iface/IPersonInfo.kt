package com.shihab.kotlintoday.feature.iface

import com.shihab.kotlintoday.feature.Person


interface IPersonInfo {

    val providerInfo: String

    fun giveFullName(person: Person):String {

        // in kotlin, interface can give default implementation...
        println(providerInfo)
        return person.fullName()
    }
}
