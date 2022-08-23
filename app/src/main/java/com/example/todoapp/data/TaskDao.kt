package com.example.todoapp.data

import androidx.room.*
import com.example.todoapp.ui.tasks.Sort
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // this to define what will happen i insert a task with the same if of another task
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    fun getTask(query: String, sort: Sort, hide: Boolean): Flow<List<Task>> =
        when (sort) {
            Sort.BY_DATE -> getTasksSortedByDate(query, hide)
            Sort.BY_NAME -> getTasksSortedByName(query, hide)
        }

    @Query("SELECT * FROM task_table WHERE (completed != :hide OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,name")
    fun getTasksSortedByName(searchQuery: String, hide: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hide OR completed = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY important DESC,created")
    fun getTasksSortedByDate(searchQuery: String, hide: Boolean): Flow<List<Task>>
}