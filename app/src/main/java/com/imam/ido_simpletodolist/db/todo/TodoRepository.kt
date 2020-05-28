package com.imam.ido_simpletodolist.db.todo

import android.app.Application
import androidx.lifecycle.LiveData
import com.imam.ido_simpletodolist.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodoRepository(application: Application) {
    private val todoDao: TodoDao?
    private var todoes: LiveData<List<Todo>>? = null
    private var todoesOrderedbyCreate: LiveData<List<Todo>>? = null
    private var todoesOrderedbyDueDate: LiveData<List<Todo>>? = null
    private var todoesFinished: LiveData<List<Todo>>? = null
    private var todoesNotFinished: LiveData<List<Todo>>? = null

    init {
        val db = AppDatabase.getInstance(application.applicationContext)
        todoDao = db?.todoDao()
        todoes = todoDao?.getAllTodoList()
        todoesOrderedbyCreate = todoDao?.getOrderbyCreateTodoList()
        todoesOrderedbyDueDate = todoDao?.getOrderbyDueDateTodoList()
        todoesFinished = todoDao?.getCheckFinishedTodoList(finished = true)
        todoesNotFinished = todoDao?.getCheckFinishedTodoList(finished = false)
    }

    fun getAllTodoList(): LiveData<List<Todo>>? {
        return todoes
    }

    fun getOrderbyCreateTodoList(): LiveData<List<Todo>>? {
        return todoesOrderedbyCreate
    }

    fun getOrderbyDueDateTodoList(): LiveData<List<Todo>>? {
        return todoesOrderedbyDueDate
    }

    fun getAllFinishedTodoList(): LiveData<List<Todo>>? {
        return todoesFinished
    }

    fun getAllNotFinishedTodoList(): LiveData<List<Todo>>? {
        return todoesNotFinished
    }

    fun insert(todo: Todo) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao?.insertTodo(todo)
        }
    }

    fun delete(todo: Todo) {
        runBlocking {
            this.launch(Dispatchers.IO) {
                todoDao?.deleteTodo(todo)
            }
        }
    }

    fun update(todo: Todo) = runBlocking {
        this.launch(Dispatchers.IO) {
            todoDao?.updateTodo(
                todo.id,
                todo.title,
                todo.content,
                todo.updateAt,
                todo.dueAt,
                todo.finished
            )
        }
    }
}