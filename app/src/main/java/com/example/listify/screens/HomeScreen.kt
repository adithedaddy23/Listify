package com.example.listify.screens

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.listify.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Dialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.example.listify.database.TaskEntity
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TaskHomeScreen(
    navController: NavController
) {
    val snackbarHostState = remember{ SnackbarHostState() }
    val currentRoute = Screen.HomeScreen.route
    SharedBottomNavigationBar(
        navController = navController,

    ) {
        Scaffold (
            topBar = {
                TopAppBar(

                    title = { Text(text = "Tasks todo") }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) {
            innerPadding ->
            FloatingButton(
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding()),
                snackbarHostState = snackbarHostState,
            )
        }
    }
}

@SuppressLint("NewApi")
@Composable
fun FloatingButton(
    viewModel: TaskViewModel = hiltViewModel(),
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,

) {
    val tasks by viewModel.allTasks.observeAsState(initial = emptyList())
    val listState = rememberLazyListState()
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    var showAddDialog by remember { mutableStateOf(false) }
    val currentDate = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val coroutineScope = rememberCoroutineScope()

    val incompleteTasks = tasks.filter { task ->
        !task.isCompleted &&
                Instant.ofEpochMilli(task.dueDate)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() >= currentDate
    }
        .sortedBy { task ->
            Instant.ofEpochMilli(task.dueDate).atZone(ZoneId.systemDefault()).toLocalDate()
        }

    Box(modifier = modifier.fillMaxSize()) {  // Wrap in Box instead of Scaffold
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(incompleteTasks) { task ->
                TaskListCard(
                    task = task,
                    onTaskCheckedChange = {
                        viewModel.toggleTaskCompletion(task)
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Task moved to Completed Tasks",
                                actionLabel = "Undo",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoLastAction()
                            }
                        }
                    },
                    onTaskDelete = { taskToDelete ->
                        viewModel.deleteTask(taskToDelete)
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                            message = "Task Deleted",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.undoLastAction() // Restore deleted task
                            } }
                    },
                    onTaskUpdate = { taskToUpdate, newTitle, newDueDate ->
                        val oldTask = taskToUpdate.copy()
                        viewModel.updateTask(taskToUpdate, newTitle, newDueDate)
                        coroutineScope.launch {
                            val result = snackbarHostState.showSnackbar(
                            message = "Task Updated",
                            actionLabel = "Undo",
                            duration = SnackbarDuration.Short
                        )
                            if (result == SnackbarResult.ActionPerformed) {
                                viewModel.updateTask(oldTask, oldTask.title, oldTask.dueDate) // Revert to old task details
                            } }
                    }

                )
            }
        }

        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            expanded = expandedFab,
            icon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Task"
                )
            },
            text = { Text("Add Task") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }

    if (showAddDialog) {
        AddTaskDialog(
            onDismiss = { showAddDialog = false },
            onTaskAdded = { title, date ->
                viewModel.addTask(title, date)
                showAddDialog = false
            }
        )
    }
}
@RequiresApi(Build.VERSION_CODES.O)
fun formatDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    return date.format(formatter)
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddTaskDialog(
    onDismiss: (Boolean) -> Unit,
    onTaskAdded: (String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val currentDate = System.currentTimeMillis()
    val currentDateWithoutTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(currentDate), ZoneId.systemDefault())
        .toLocalDate()
    val selectedDateWithoutTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(dueDate), ZoneId.systemDefault())
        .toLocalDate()

    Dialog(
        onDismissRequest = { onDismiss(false) }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(375.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Task",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(16.dp)
                )
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Add title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions(KeyboardCapitalization.Words)
                )
                OutlinedTextField(
                    value = formatDate(dueDate),
                    onValueChange = {  },
                    label = { Text("Add a Due Date") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    trailingIcon = {
                        IconButton(
                            onClick = {showDatePicker = true}
                        ){
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = null
                            )
                        }
                    }

                )
                if (selectedDateWithoutTime.isBefore(currentDateWithoutTime)) {
                    Text(
                        text = "Please select a future date.",
                        color = Color.Red,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {onDismiss(false)}
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onTaskAdded(title, dueDate)
                            }
                        }
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dueDate)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDate = datePickerState.selectedDateMillis
                        if (selectedDate != null) {
                            dueDate = selectedDate // Update dueDate
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}



@SuppressLint("NewApi")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TaskListCard(
    task: TaskEntity,
    onTaskCheckedChange: (TaskEntity) -> Unit,
    onTaskDelete: (TaskEntity) -> Unit,
    onTaskUpdate: (TaskEntity,String,Long) -> Unit
){
        val formattedDate = remember(task.dueDate) {
        val instant = Instant.ofEpochMilli(task.dueDate)
        val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy")
        localDate.format(formatter)
    }
    var showEditDialog by remember { mutableStateOf(false) }
    var editedTitle by remember(task.title) { mutableStateOf(task.title) }
    var editedDate by remember(task.dueDate) { mutableStateOf(task.dueDate) }

    val currentDate = remember { LocalDate.now(ZoneId.systemDefault()) }
    val taskDate = remember(task.dueDate) {
        Instant.ofEpochMilli(task.dueDate)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    if(showEditDialog) {
        Dialog(
            onDismissRequest = { showEditDialog = false },
            content = {
                Card (
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Edit Task",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            value = editedTitle,
                            onValueChange = { editedTitle = it },
                            label = { Text(text = "Task Title") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = editedDate
                        )
                        var showDatePicker by remember { mutableStateOf(false) }

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                        ) {
                            Text("Select Due Date")
                        }
                        if (showDatePicker) {
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        datePickerState.selectedDateMillis?.let {
                                            editedDate = it
                                        }
                                        showDatePicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDatePicker = false }) {
                                        Text("Cancel")
                                    }
                                }
                            ) {
                                DatePicker(state = datePickerState)
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(
                                onClick = {
                                    onTaskUpdate(task, editedTitle, editedDate)
                                    showEditDialog = false
                                }
                            ) {
                                Text(text = "Save")
                            }
                            Button(
                                onClick = { showEditDialog = false }
                            ) {
                                Text(text = "Cancel")
                            }
                        }
                    }
                }
            },

        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()


    ) {
        Column(

        ) {
            ListItem(
                headlineContent = { Text(
                    text = task.title,
                    fontSize = 18.sp) },
                supportingContent = {
                    when(taskDate) {
                        currentDate -> Text(text = "Due: Today")
                        currentDate.plusDays(1) -> Text(text = "Due: Tomorrow")
                        else -> Text(text =("Due: $formattedDate") )
                    }
                },
                leadingContent = {
                    Button(
                        onClick = {
                            onTaskCheckedChange(task)},
                        modifier = Modifier.size(30.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Complete Task",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                trailingContent = {
                    Row {
                        // Edit button
                        IconButton(onClick = { showEditDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Task",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        // Delete button
                        IconButton(onClick = { onTaskDelete(task) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Task",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
            )
            HorizontalDivider()
        }
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun AddTaskDialogPreview(){
    AddTaskDialog(
        onDismiss = { /* Do nothing for preview */ },
        onTaskAdded = { taskName, taskTime ->
            println("Task Added: Name = $taskName, Time = $taskTime") })
}

//@Preview(showBackground = true)
//@Composable
//fun TaskListCardPreview() {
//    MaterialTheme {
//        TaskListCard(
//            task = TaskEntity(
//                id = 1,
//                title = "Complete Mathematics Assignment",
//                isCompleted = false,
//                dueDate = 20122024 // Using the format you had in your original code
//            ),
//            onTaskCheckedChange = {},
//            onTaskDelete = {}
//        )
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun TaskHomeScreenPreview(){
//    val drawerState = rememberDrawerState(DrawerValue.Closed) // Force drawer to open
//    val scope = rememberCoroutineScope()
//    TaskHomeScreen(scope, drawerState, modifier = Modifier, navigateToCompletedTasks = {}, navController = NavController(context = CompletedTasksScreen()))
//}