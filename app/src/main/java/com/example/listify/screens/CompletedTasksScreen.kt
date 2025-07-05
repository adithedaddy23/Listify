package com.example.listify.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.listify.database.TaskEntity
import com.example.listify.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CompletedTasksScreen(
    viewModel: TaskViewModel = hiltViewModel(),
    navController: NavController
) {
    val tasks by viewModel.completeTasks.observeAsState(emptyList())
    val currentRoute = Screen.HomeScreen.route
    val snackbarHostState = remember { SnackbarHostState() }
    SharedBottomNavigationBar(
        navController = navController,
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "Completed Tasks") }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            innerPadding ->
            tasks.let {
                CompletedTaskList(
                    modifier = Modifier.padding(innerPadding),
                    tasks = it,
                    snackbarHostState = snackbarHostState,
                )
            }
        }
    }
}


@Composable
fun CompletedTaskList(
    viewModel: TaskViewModel = hiltViewModel(),
    modifier: Modifier,
    tasks: List<TaskEntity>,
    snackbarHostState: SnackbarHostState,
    ){
    val coroutineScope = rememberCoroutineScope()
    LazyColumn (
        modifier = modifier
    ) {
        items(tasks) { task ->
            CompletedTasksCard(
                task = task,
                onTaskDelete = { taskToDelete ->
                    viewModel.deleteTask(taskToDelete)
                    coroutineScope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Task Deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            viewModel.undoLastAction()
                        } }
                },
            )
        }
    }
}


@SuppressLint("NewApi")
@Composable
fun CompletedTasksCard(
    task: TaskEntity,
    onTaskDelete: (TaskEntity) -> Unit,

){
    val formattedDate = remember(task.dueDate) {
        val instant = Instant.ofEpochMilli(task.dueDate)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        localDate.format(formatter)
    }
    val currentDate = remember { LocalDate.now(ZoneId.systemDefault()) }
    val taskDate = remember(task.dueDate) {
        Instant.ofEpochMilli(task.dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(30.dp),
    ) {
        Column (
        ) {
            ListItem(
                headlineContent = { Text(
                    text = task.title,
                    fontSize = 18.sp,
                    textDecoration = TextDecoration.LineThrough) },
                supportingContent = {
                    when(taskDate) {
                        currentDate -> Text(text = "Due: Today",textDecoration = TextDecoration.LineThrough)
                        currentDate.plusDays(1) -> Text(text = "Due: Tomorrow",textDecoration = TextDecoration.LineThrough)
                        else -> Text(text = ("Due $formattedDate"),textDecoration = TextDecoration.LineThrough)
                    }
                },
                trailingContent = {
                    IconButton(
                        onClick = {onTaskDelete(task)}
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
            HorizontalDivider()
//
        }
    }
}





