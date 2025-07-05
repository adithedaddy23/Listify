package com.example.listify.undo

import com.example.listify.database.TaskEntity

class UndoManager {
    private val stack: ArrayDeque<TaskEntity> = ArrayDeque()

    fun push(taskEntity: TaskEntity) {
        if(stack.size == 12) {
            stack.removeFirst()
        }
        stack.addLast(taskEntity)
    }

    fun pop() : TaskEntity? = if(stack.isNotEmpty()) stack.removeLast() else null

    fun hasUndo(): Boolean = stack.isNotEmpty()
}

