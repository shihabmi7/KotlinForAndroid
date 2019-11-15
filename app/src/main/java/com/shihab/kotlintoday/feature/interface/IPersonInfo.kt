package com.shihab.kotlintoday.feature.`interface`

import com.shihab.kotlintoday.feature.Person


interface IPersonInfo {

    fun giveFullName(person: Person):String {

        // in kotlin interface can give default implementation...
        return ""
    }
}
