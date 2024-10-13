package com.example.lab08

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



enum class TaskFilter { ALL, COMPLETED, PENDING }

class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private var currentFilter: TaskFilter = TaskFilter.ALL

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = when (currentFilter) {
                TaskFilter.ALL -> dao.getAllTasks()
                TaskFilter.COMPLETED -> dao.getCompletedTasks()
                TaskFilter.PENDING -> dao.getPendingTasks()
            }
        }
    }

    fun setFilter(filter: TaskFilter) {
        currentFilter = filter
        loadTasks()
    }

    // Function to add a new task
    fun addTask(description: String) {
        val newTask = Task(description = description)
        viewModelScope.launch {
            dao.insertTask(newTask)
            loadTasks() // Reload the task list after adding a new task
        }
    }

    // Function to toggle task completion
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            loadTasks() // Reload the task list after updating
        }
    }

    // Function to delete a specific task
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
            loadTasks() // Reload the task list after deletion
        }
    }

    // Function to update task description
    fun updateTaskDescription(task: Task, newDescription: String) {
        viewModelScope.launch {
            val updatedTask = task.copy(description = newDescription)
            dao.updateTask(updatedTask)
            loadTasks() // Reload task list
        }
    }

    // Function to delete all tasks
    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList() // Empty the task list in the state
        }
    }
}

