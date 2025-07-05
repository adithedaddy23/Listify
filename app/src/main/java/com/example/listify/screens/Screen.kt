package com.example.listify.screens

sealed class Screen(val route: String) {
    object HomeScreen: Screen("home_screen")
    object PendingTasksScreen: Screen("pending_tasks_screen")
    object CompletedTasksScreen: Screen("completed_tasks_screen")
}
