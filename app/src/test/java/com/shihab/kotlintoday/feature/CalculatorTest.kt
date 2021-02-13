package com.shihab.kotlintoday.feature

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test

class CalculatorTest {

    @Before
    fun setUp() {
    }

    @Test
    fun checkTwoSum() {
        val result = Calculator.add(7,7)
        assertThat(result).isEqualTo(14)
    }

    @Test
    fun checkSubstract() {
        val result = Calculator.substract(7,7)
        assertThat(result).isEqualTo(0)
    }

    @Test
    fun checkMultiply() {
        val result = Calculator.multiple(7,7)
        assertThat(result).isEqualTo(49)
    }
}