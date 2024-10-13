package com.example.lab08

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {

    // Obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    // Obtener tareas completadas
    @Query("SELECT * FROM tasks WHERE is_completed = 1")
    suspend fun getCompletedTasks(): List<Task>

    // Obtener tareas pendientes
    @Query("SELECT * FROM tasks WHERE is_completed = 0")
    suspend fun getPendingTasks(): List<Task>

    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)

    // Actualizar una tarea
    @Update
    suspend fun updateTask(task: Task)

    // Eliminar una tarea individual
    @Delete
    suspend fun deleteTask(task: Task)

    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
