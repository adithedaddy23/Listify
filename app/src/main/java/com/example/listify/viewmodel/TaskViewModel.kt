package com.example.listify.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.listify.database.TaskEntity
import com.example.listify.undo.UndoManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor (
    private val repository: TaskRepository
) : ViewModel() {

    val allTasks = repository.allTasks.asLiveData()

    val incompleteTasks = repository.getIncompleteTasks().asLiveData()

    val completeTasks = repository.getCompleteTasks().asLiveData()

    private val undoManager = UndoManager()
    private val _undoAvailable = MutableLiveData(false)
    val undoAvailable: LiveData<Boolean> get() = _undoAvailable

    fun addTask(title: String, dueDate: Long) {
        viewModelScope.launch {
            val task = TaskEntity(
                title = title,
                isCompleted = false,
                dueDate = dueDate
            )
            repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity, newTitle: String, newDueDate: Long) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                title = newTitle,
                dueDate = newDueDate
            )
            repository.updateTask(updatedTask)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            undoManager.push(task)
            _undoAvailable.postValue(true)
            repository.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            undoManager.push(task)
            _undoAvailable.postValue(true)
            repository.deleteTask(task)
        }
    }


    fun undoLastAction() {
        viewModelScope.launch {
            val lastTask = undoManager.pop()
            if (lastTask != null) {
                if (repository.taskExists(lastTask.id)) {
                    // If the task exists, handle different undo scenarios
                    when {
                        // If the task was originally deleted, restore it
                        !repository.taskExists(lastTask.id) -> {
                            repository.insertTask(lastTask)
                        }
                        // If the task was completed, toggle back to incomplete
                        lastTask.isCompleted -> {
                            repository.updateTask(lastTask.copy(isCompleted = false))
                        }
                        // If the task was updated, restore to previous state
                        else -> {
                            // This assumes you're tracking the original task state before changes
                            repository.updateTask(lastTask)
                        }
                    }
                } else {
                    // If the task was deleted, restore it with its original completion status
                    repository.insertTask(lastTask)
                }
            }
            _undoAvailable.postValue(undoManager.hasUndo())
        }
    }


}