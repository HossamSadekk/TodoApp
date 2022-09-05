package com.example.todoapp.ui.addedittasks

import androidx.hilt.Assisted
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.ADD_TASK_RESULT_OK
import com.example.todoapp.EDIT_TASK_RESULT_OK
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    @Assisted private val state: SavedStateHandle,
    private val taskDao: TaskDao
) : ViewModel() {

    val task = state.get<Task>("task")

    var taskName = state.get<String>("name") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("name", value)
        }

    var important = state.get<Boolean>("important") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("important", value)
        }

    private val addEditChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidMessage("Task title cannot be empty")
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = important)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = important)
            createTask(newTask)
        }
    }

    private fun showInvalidMessage(msg: String) =
        viewModelScope.launch {
            addEditChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(msg))
        }

    private fun createTask(newTask: Task) =
        viewModelScope.launch {
            taskDao.insert(newTask)
            addEditChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
        }

    private fun updateTask(updatedTask: Task) =
        viewModelScope.launch {
            taskDao.update(updatedTask)
            addEditChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }


    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }
}