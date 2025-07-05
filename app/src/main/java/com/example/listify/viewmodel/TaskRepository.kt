package com.example.listify.viewmodel

import com.example.listify.database.TaskDao
import com.example.listify.database.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    val allTasks: Flow<List<TaskEntity>> = taskDao.getAllTasks()

    // Get incomplete tasks
    fun getIncompleteTasks(): Flow<List<TaskEntity>> =
        taskDao.getTasksByCompletion(isCompleted = false)

    fun getCompleteTasks(): Flow<List<TaskEntity>> =
        taskDao.getTasksByCompletion(isCompleted = true)

    // Get tasks by due date
    fun getTasksByDueDate(dueDate: Long): Flow<List<TaskEntity>> =
        taskDao.getTasksByDueDate(dueDate)

    // Insert task
    suspend fun insertTask(task: TaskEntity) {
        taskDao.insert(task)
    }

    // Update task
    suspend fun updateTask(task: TaskEntity) {
        taskDao.update(task)
}

    // Delete task
    suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }
    suspend fun taskExists(taskId: Int): Boolean {
        return taskDao.getTaskById(taskId) != null
    }
}