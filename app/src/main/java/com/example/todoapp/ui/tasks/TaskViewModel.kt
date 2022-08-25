package com.example.todoapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.PreferencesManager
import com.example.todoapp.data.Sort
import com.example.todoapp.data.Task
import com.example.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskFlow =
        combine(searchQuery, preferencesFlow) { query, preferencesFlow ->
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

    }

    fun onTaskStateChanged(task: Task, checkedState: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = checkedState))
        }
    }


}

