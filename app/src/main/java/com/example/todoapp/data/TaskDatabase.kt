package com.example.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class] , version = 1)
abstract class TaskDatabase : RoomDatabase() {

    // this method access our DAO
    abstract fun taskDao() : TaskDao

}