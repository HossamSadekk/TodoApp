package com.example.todoapp.ui.deleteAllCompleted

import androidx.lifecycle.ViewModel
import com.example.todoapp.data.TaskDao
import com.example.todoapp.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() =
        applicationScope.launch {
            taskDao.deleteAllCompleted()
        }

}