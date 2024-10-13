package com.example.lab08

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Schedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(viewModel: TaskViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var taskBeingEdited by remember { mutableStateOf<Task?>(null) }
    var updatedTaskDescription by remember { mutableStateOf("") }

    var currentFilter by remember { mutableStateOf(TaskFilter.ALL) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Task Manager",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // New Task Input
        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            Modifier.fillMaxWidth(),
            label = { Text("Nueva tarea") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color(0xFFE0E0E0),  // Suave gris claro
                focusedIndicatorColor = Color(0xFF90CAF9),  // Azul suave
                unfocusedIndicatorColor = Color(0xFFB0BEC5)  // Gris suave
            )
        )

        Button(
            onClick = {
                if (newTaskDescription.isNotEmpty()) {
                    viewModel.addTask(newTaskDescription)
                    newTaskDescription = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF90CAF9))  // Azul suave
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar tarea", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Button(
                onClick = {
                    currentFilter = TaskFilter.ALL
                    viewModel.setFilter(TaskFilter.ALL)
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (currentFilter == TaskFilter.ALL) Color(0xFF64B5F6) else Color(0xFFB0BEC5))  // Diferentes tonalidades de azul y gris
            ) {
                Icon(Icons.Default.List, contentDescription = "Todas", tint = Color.White)
            }

            Button(
                onClick = {
                    currentFilter = TaskFilter.COMPLETED
                    viewModel.setFilter(TaskFilter.COMPLETED)
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (currentFilter == TaskFilter.COMPLETED) Color(0xFF64B5F6) else Color(0xFFB0BEC5))
            ) {
                Icon(Icons.Default.Check, contentDescription = "Completadas", tint = Color.White)
            }

            Button(
                onClick = {
                    currentFilter = TaskFilter.PENDING
                    viewModel.setFilter(TaskFilter.PENDING)
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (currentFilter == TaskFilter.PENDING) Color(0xFF64B5F6) else Color(0xFFB0BEC5))
            ) {
                Icon(Icons.Default.Schedule, contentDescription = "Pendientes", tint = Color.White)
            }
        }

        // Task List
        tasks.forEach { task ->
            Card(
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .background(Color(0xFFF5F5F5)),  // Fondo de la tarjeta gris claro
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (taskBeingEdited?.id == task.id) {
                        TextField(
                            value = updatedTaskDescription,
                            onValueChange = { updatedTaskDescription = it },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFE0E0E0)  // Gris claro para el input
                            )
                        )
                        Button(
                            onClick = {
                                if (updatedTaskDescription.isNotEmpty()) {
                                    viewModel.updateTaskDescription(task, updatedTaskDescription)
                                    taskBeingEdited = null
                                }
                            },
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF64B5F6))  // Azul suave
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Guardar", tint = Color.White)
                        }
                    } else {
                        Text(
                            text = task.description,
                            color = if (task.isCompleted) Color(0xFF66BB6A) else Color(0xFF37474F),  // Verde para completadas, gris oscuro para pendientes
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Move buttons below the task description
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(onClick = { viewModel.toggleTaskCompletion(task) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0BEC5))) {
                                Icon(
                                    imageVector = if (task.isCompleted) Icons.Default.Check else Icons.Default.Schedule,
                                    contentDescription = "Estado",
                                    tint = Color.White
                                )
                            }
                            Button(onClick = { viewModel.deleteTask(task) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0BEC5))) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                            }
                            Button(onClick = {
                                taskBeingEdited = task
                                updatedTaskDescription = task.description
                            },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB0BEC5))) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Delete All Button
        Button(
            onClick = { coroutineScope.launch { viewModel.deleteAllTasks() } },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF5350))  // Rojo claro para eliminar todas
        ) {
            Text("Eliminar todas las tareas", color = Color.White)
        }
    }
}
