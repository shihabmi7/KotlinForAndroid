package com.shihab.kotlintoday.utility

import com.shihab.kotlintoday.model.Task

class DataSource {

    companion object {

        fun getTaskList(): ArrayList<Task>? {

            var taskList = ArrayList<Task>()
            taskList.add(Task("Pray", true, 1))
            taskList.add(Task("Sleep", true, 2))
            taskList.add(Task("Eat", true, 3))
            return taskList
        }
    }

}