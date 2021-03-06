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

    @Query("UPDATE todo SET title = :title, content= :content, update_at=:update_at, due_date=:due_date, finished = :finished, check_alarm_hour = :check_alarm_hour  WHERE id LIKE :id")
    suspend fun updateTodo(
        id: Int?,
        title: String,
        content: String,
        update_at: Date,
        due_date: Date,
        finished: Boolean,
        check_alarm_hour: Boolean
    )

    @Query("Select * from todo")
    fun getAllTodoList(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo ORDER BY created_at ASC")
    fun getOrderbyCreateTodoList(): LiveData<List<Todo>>

    @Query("SELECT * FROM todo ORDER BY due_date ASC")
    fun getOrderbyDueDateTodoList(): LiveData<List<Todo>>

    @Query("DELETE FROM todo WHERE finished LIKE :finished")
    fun deleteFinishedTodoList(finished: Boolean): Int

    @Query("DELETE FROM todo")
    fun deleteAll()
}