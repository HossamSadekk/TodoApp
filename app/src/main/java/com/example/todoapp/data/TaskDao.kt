package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // this to define what will happen i insert a task with the same if of another task
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :searchQuery || '%' ")
    fun getTasks(searchQuery: String): Flow<List<Task>>
}