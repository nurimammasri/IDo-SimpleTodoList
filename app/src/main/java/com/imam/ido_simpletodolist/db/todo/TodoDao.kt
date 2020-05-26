package com.imam.ido_simpletodolist.db.todo

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*


@Dao
interface TodoDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTodo(todo: Todo)

    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Query("UPDATE todo SET title = :title, content= :content, update_at=:update_at, due_date=:due_date WHERE id LIKE :id")
    suspend fun updateTodo(
        id: Int?,
        title: String,
        content: String,
        update_at: Date,
        due_date: Date
    )

    @Query("Select * from todo")
    fun getAllTodoList(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo ORDER BY created_at ASC")
    fun getOrderbyCreateTodoList(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo ORDER BY due_date ASC")
    fun getOrderbyDueDateTodoList(): LiveData<List<Todo>>
}