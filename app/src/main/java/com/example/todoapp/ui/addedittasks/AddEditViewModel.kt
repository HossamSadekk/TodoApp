package com.example.todoapp.ui.addedittasks

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    @Assisted private val state : SavedStateHandle,
    private val taskDao: TaskDao
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("name") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("name",value)
        }

    var important = state.get<Boolean>("important") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("important",value)
        }
}