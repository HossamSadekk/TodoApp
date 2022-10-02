package com.example.todoapp.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // Annotate with @RunWith
@SmallTest
class TaskDatabaseTest {
    @get:Rule
    var instatExecutorRule = InstantTaskExecutorRule()
    private lateinit var database: TaskDatabase
    private lateinit var dao: TaskDao


    // just to ensure a new database before every test case because it should be independent.
    //When to use Room.inMemoryDatabaseBuilder() instead of Room.databaseBuilder() ?
    // If you want data to be persisted after you killing the app then you should go with Room.databaseBuilder()
    // and if you want this data to
    // be not persisted then you have to choose Room.inMemoryDatabaseBuilder() in case of testing scenarios.
    @Before
    fun setup() {

        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.taskDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert() = runBlockingTest {
        val task = Task("do exercise", id = 1)
        dao.insert(task)
        dao.getTasksSortedByName("", false).test {
            assertThat(awaitItem()).contains(task)
        }
    }

    @Test
    fun delete() = runBlockingTest {

        val task = Task("do exercise", id = 1)
        dao.insert(task)
        dao.delete(task)
        val tasks = dao.getTasksSortedByName("", false).test {
            assertThat(awaitItem()).doesNotContain(task)

        }

    }

    @Test
    fun update() = runBlockingTest {
        val task = Task("do exercise", id = 1, created = 1664735788385)
        dao.insert(task)
        dao.update(task.copy("cook something"))
        dao.getTasksSortedByName("", false).test {
            assertThat(awaitItem()).containsExactly(
                Task(
                    "cook something",
                    id = 1,
                    created = 1664735788385
                )
            )
        }
    }

    @Test
    fun getTasksSortedByName() = runBlockingTest {
        // Arrange
        dao.insert(Task("a", created = 1664736190844))
        dao.insert(Task("b", created = 1664736190844))
        dao.insert(Task("c", created = 1664736190844))
        // Act
        val tasks = listOf<Task>(
            Task("a", id = 1, created = 1664736190844),
            Task("b", id = 2, created = 1664736190844),
            Task("c", id = 3, created = 1664736190844)
        )
        dao.getTasksSortedByName("", false).test {
            assertThat(awaitItem()).isEqualTo(tasks)
        }
    }
    @Test
    fun deleteAllCompleted()= runBlockingTest {
        // Arrange
        dao.insert(Task("a", created = 1664736190844, completed = true))
        dao.insert(Task("b", created = 1664736190844, completed = false))
        dao.insert(Task("c", created = 1664736190844, completed = true))
        // Act
        val tasks = listOf<Task>(
            Task("b", id = 2, created = 1664736190844)
        )
        dao.deleteAllCompleted()
        dao.getTasksSortedByName("", false).test {
            assertThat(awaitItem()).isEqualTo(tasks)
        }
    }

}