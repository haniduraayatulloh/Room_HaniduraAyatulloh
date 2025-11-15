package com.example.praktikum7_225150207111005_haniduraayatulloh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TaskViewModel (private val repository: TaskRepository): ViewModel() {

    val allTasks = repository.allTasks


    fun addNewTask(taskTitle: String) {
        val newTask = Task(title = taskTitle)
        viewModelScope.launch {
            repository.insert(newTask)
        }
    }

    fun updateTaskTitle(task: Task, newTitle: String) {
        // Buat salinan tugas dengan judul yang baru. Status isCompleted tetap.
        val updatedTask = task.copy(title = newTitle)

        viewModelScope.launch {
            repository.update(updatedTask) // Panggil fungsi update di repository
        }
    }
    fun updateTaskStatus (task: Task, isCompleted: Boolean) {
        val updatedTask = task.copy(isCompleted = isCompleted)
        viewModelScope.launch {
            repository.update(updatedTask)
        }
    }


    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }
}


class TaskViewModelFactory(private val repository: TaskRepository):
    ViewModelProvider.Factory {
    override fun <T: ViewModel> create (modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}