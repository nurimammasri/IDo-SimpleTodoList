package com.imam.ido_simpletodolist.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.imam.ido_simpletodolist.db.todo.Todo
import com.imam.ido_simpletodolist.db.todo.TodoRepository

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TodoRepository = TodoRepository(application)
    private val allTodoList: LiveData<List<Todo>>? = repository.getAllTodoList()
    private val allOrderbyCreateTodoList: LiveData<List<Todo>>? =
        repository.getOrderbyCreateTodoList()
    private val allOrderbyDueDateTodoList: LiveData<List<Todo>>? =
        repository.getOrderbyDueDateTodoList()

    fun insertTodo(todo: Todo) {
        repository.insert(todo)
    }

    fun updateTodo(todo: Todo) {
        repository.update(todo)
    }

    fun deleteTodo(todo: Todo) {
        repository.delete(todo)
    }

    fun deleteAll(todo: Todo) {
        repository.deleteAll(todo)
    }

    fun deleteFinished(todo: Todo) {
        repository.deleteFinishedTodoList(todo)
    }

    fun getAllTodoList(): LiveData<List<Todo>>? {
        return allTodoList
    }

    fun getOrderbyCreateTodoList(): LiveData<List<Todo>>? {
        return allOrderbyCreateTodoList
    }

    fun getOrderbyDueDateTodoList(): LiveData<List<Todo>>? {
        return allOrderbyDueDateTodoList
    }

    fun setFinishedItemTodo(todo: Todo) {
        todo.finished = !todo.finished
        repository.update(todo)
    }

}
