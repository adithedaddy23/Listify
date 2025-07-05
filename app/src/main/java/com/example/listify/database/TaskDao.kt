package com.example.listify.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted")
    fun getTasksByCompletion(isCompleted: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dueDate = :dueDate")
    fun getTasksByDueDate(dueDate: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: kotlin.Int): TaskEntity?
}