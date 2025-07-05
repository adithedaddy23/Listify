package com.example.listify.screens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.listify.database.TaskEntity

@Composable
fun Navigation() {
    val navController = rememberNavController()
    DisposableEffect(navController) {
        onDispose { }
    }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        composable(
            route = Screen.HomeScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            TaskHomeScreen(
                navController = navController,

            )
        }

        composable(
            route = Screen.CompletedTasksScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            CompletedTasksScreen(
                navController = navController,
            )
        }

        composable(
            route = Screen.PendingTasksScreen.route,
            enterTransition = { fadeIn(animationSpec = tween(300)) },
            exitTransition = { fadeOut(animationSpec = tween(300)) }
        ) {
            PendingTasksScreen(
                scope = rememberCoroutineScope(),
                drawerState = rememberDrawerState(DrawerValue.Closed),
                navController = navController,
                modifier = Modifier,
            )
        }
    }
}