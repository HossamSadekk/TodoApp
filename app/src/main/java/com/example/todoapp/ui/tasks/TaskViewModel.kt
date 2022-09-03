package com.example.todoapp.ui.tasks

import androidx.hilt.Assisted
import androidx.lifecycle.*
import com.example.todoapp.data.PreferencesManager
import com.example.todoapp.data.Sort
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskEventChannel = Channel<TaskEvent>()
    val taskEvent = taskEventChannel.receiveAsFlow()

    private val taskFlow =
        combine(searchQuery.asFlow(), preferencesFlow) { query, preferencesFlow ->
            Pair(query, preferencesFlow)
        }.flatMapLatest {
            taskDao.getTask(it.first, it.second.sort, it.second.hideCompleted)
        }

    val tasks = taskFlow.asLiveData()

    fun onSortOrderSelected(sort: Sort) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sort)
    }

    fun onHideCompletedSelected(hide: Boolean) = viewModelScope.launch {
        preferencesManager.hideCompleted(hide)
    }

    fun onTaskSelected(task: Task) {
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskStateChanged(task: Task, checkedState: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = checkedState))
        }
    }

    fun onTaskSwiped(task: Task) =
        viewModelScope.launch {
            taskDao.delete(task)
            taskEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
        }

    fun onUndoDeleteClicked(task: Task) =
        viewModelScope.launch {
            taskDao.insert(task)
        }

    fun onAddItemClick() =
        viewModelScope.launch {
            taskEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
        }

    sealed class TaskEvent {
        object NavigateToAddTaskScreen : TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
    }
}

