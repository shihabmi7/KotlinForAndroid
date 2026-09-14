package com.shihab.kotlintoday.utility

fun main() {
    val arr = listOf(5, 6, 7, 8, 9, 10, 1, 2, 3)
    val key = 10
    val result = searchInRotatedSortedArray(arr, key)
    println("Index is "+result)
}

fun searchInRotatedSortedArray(arr: List<Int>, key: Int): Int {
    var low = 0
    var high = arr.size - 1

    while (low <= high) {
        val mid = (low + high) / 2
        if (arr[mid] == key) return mid

        if (arr[low] <= arr[mid]) {
            // left half [low..mid] is the sorted half
            if (key >= arr[low] && key < arr[mid]) {
                high = mid - 1   // key is inside the sorted left half
            } else {
                low = mid + 1    // key must be in the right half
            }
        } else {
            // right half [mid..high] is the sorted half
            if (key > arr[mid] && key <= arr[high]) {
                low = mid + 1    // key is inside the sorted right half
            } else {
                high = mid - 1   // key must be in the left half
            }
        }
    }
    return -1
}