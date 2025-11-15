package com.example.praktikum7_225150207111005_haniduraayatulloh

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.praktikum7_225150207111005_haniduraayatulloh.ui.theme.Praktikum7_225150207111005_HaniduraAyatullohTheme

class MainActivity: ComponentActivity() {

    private val database by lazy { TaskDatabase.getDatabase (application) }
    private val repository by lazy { TaskRepository (database.taskDao()) }
    private val viewModelFactory by lazy { TaskViewModelFactory (repository) }
    private val viewModel: TaskViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            Praktikum7_225150207111005_HaniduraAyatullohTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Ambil data Flow dan konversi menjadi Compose State
                    val tasks by viewModel.allTasks.collectAsState(initial = emptyList())

                    TaskScreen(
                        tasks = tasks,
                        onAddTask = { title -> viewModel.addNewTask(title) },
                        onUpdateTask = { task, completed ->
                            viewModel.updateTaskStatus (task, completed)
                        },
                        onDeleteTask = { task -> viewModel.deleteTask (task) },
                        onEditTaskTitle = { task, newTitle ->
                            viewModel.updateTaskTitle(task, newTitle)
                        }

                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen (
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onUpdateTask: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onEditTaskTitle: (Task, String) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Daftar Tugas (Room Compose)") }) }
    ) { paddingValues ->
        Column(modifier = Modifier.padding (paddingValues).fillMaxSize()) {
            TaskInput(onAddTask)
            Spacer (modifier = Modifier.height(8.dp))
            TaskList(tasks, onUpdateTask, onDeleteTask, onEditTaskTitle)
        }
    }
}

@Composable
fun TaskInput (onAddTask: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tugas Baru") },
            modifier = Modifier.weight(1f)
        )
        Spacer (modifier = Modifier.width(8.dp))
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onAddTask(text)
                    text = ""
                }
            },
            enabled = text.isNotBlank()
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Tambah Tugas")
        }
    }
}

@Composable
fun TaskList(
    tasks: List<Task>,
    onUpdateTask: (Task, Boolean) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onEditTaskTitle: (Task, String) -> Unit
) {

    LazyColumn (contentPadding = PaddingValues (horizontal = 16.dp, vertical = 8.dp)) {
        items (tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onCheckedChange = { isChecked -> onUpdateTask (task, isChecked) },
                onDelete = { onDeleteTask(task) },
                onEdit = { newTitle -> onEditTaskTitle(task, newTitle) }
            )
            Divider()
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onCheckedChange(!task.isCompleted) }, // Klik untuk centang/uncentang
                onLongClick = { showEditDialog = true } // Tekan lama untuk memunculkan dialog
            )
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCheckedChange
        )
        Spacer (Modifier.width(8.dp))
        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Hapus Tugas")
        }
        if (showEditDialog) {
            EditTaskDialog(
                task = task,
                onDismiss = { showEditDialog = false },
                onConfirmEdit = { newTitle ->
                    onEdit(newTitle)
                    showEditDialog = false
                }
            )
        }
    }
}
@Composable
fun EditTaskDialog(
    task: Task,
    onDismiss: () -> Unit,
    onConfirmEdit: (String) -> Unit
) {
    var newTitleText by remember { mutableStateOf(task.title) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Tugas") },
        text = {
            OutlinedTextField(
                value = newTitleText,
                onValueChange = { newTitleText = it },
                label = { Text("Judul Tugas Baru") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newTitleText.isNotBlank()) {
                        onConfirmEdit(newTitleText)
                    }
                },
                enabled = newTitleText.isNotBlank() && newTitleText != task.title
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}