package com.example.listify.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.listify.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SharedBottomNavigationBar(
    navController: NavController,
    content: @Composable () -> Unit
) {
    val currentRoute = navController
        .currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Tasks",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Tasks",
                            fontSize = 12.sp
                        )
                    },
                    selected = currentRoute == Screen.HomeScreen.route,
                    onClick = {
                        navController.navigate(Screen.HomeScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed Tasks",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Completed",
                            fontSize = 12.sp
                        )
                    },
                    selected = currentRoute == Screen.CompletedTasksScreen.route,
                    onClick = {
                        navController.navigate(Screen.CompletedTasksScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.hourglass),
                            contentDescription = "Pending",
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Pending",
                            fontSize = 12.sp
                        )
                    },
                    selected = currentRoute == Screen.PendingTasksScreen.route,
                    onClick = {
                        navController.navigate(Screen.PendingTasksScreen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            content()
        }
    }
}