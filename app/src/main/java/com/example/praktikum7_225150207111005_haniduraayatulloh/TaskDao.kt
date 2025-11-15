package com.example.praktikum7_225150207111005_haniduraayatulloh

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTask(task: Task)


    @Query("SELECT * FROM task_table ORDER BY id DESC")
    fun getAllTasks(): Flow<List<Task>>


    @Update
    suspend fun updateTask(task: Task)


    @Delete
    suspend fun deleteTask(task: Task)
}