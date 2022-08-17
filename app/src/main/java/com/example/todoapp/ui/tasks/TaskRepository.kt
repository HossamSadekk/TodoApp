package com.example.todoapp.ui.tasks

import androidx.lifecycle.asLiveData
import com.example.todoapp.data.TaskDao
import javax.inject.Inject

class TaskRepository @Inject constructor(taskDao: TaskDao) {
    val taskDao = taskDao

    fun getTasks() =
        taskDao.getTasks().asLiveData()
}