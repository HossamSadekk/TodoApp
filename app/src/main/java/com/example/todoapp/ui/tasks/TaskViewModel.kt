package com.example.todoapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.todoapp.data.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val taskDao: TaskDao) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(Sort.BY_DATE)
    val hideComplete = MutableStateFlow(false)

    private val taskFlow =
        combine(searchQuery, sortOrder, hideComplete) { query, sortOrder, hideComplete ->
            Triple(query, sortOrder, hideComplete)
        }.flatMapLatest {
            taskDao.getTask(it.first, it.second, it.third)
        }

    val tasks = taskFlow.asLiveData()


}

enum class Sort { BY_NAME, BY_DATE }