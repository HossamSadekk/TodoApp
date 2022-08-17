package com.example.todoapp.ui.tasks

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class TaskViewModel constructor(taskRepository: TaskRepository) : ViewModel() {
    val taskRepository = taskRepository
    fun getTasks() =
        taskRepository.getTasks()

}