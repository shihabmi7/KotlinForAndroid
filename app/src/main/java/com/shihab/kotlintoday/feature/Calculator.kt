package com.shihab.kotlintoday.feature

object Calculator {
    fun add(a: Int, b: Int): Int {
        return a + b
    }

    fun substract(a: Int, b: Int): Int {
        return a - b
    }

    fun multiple(a: Int, b: Int): Int {
        return a * b
    }

    fun divide(a: Float, b: Float): Float {
        return when {
            a != 0f -> {
                a / b
            }
            else -> 0f
        }
    }
}