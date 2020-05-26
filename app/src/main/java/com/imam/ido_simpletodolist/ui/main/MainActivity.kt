package com.imam.ido_simpletodolist.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.db.todo.Todo
import com.imam.ido_simpletodolist.ui.create.CreateTodoActivity
import com.imam.ido_simpletodolist.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), TodoAdapter.TodoEvents {

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Setting up RecyclerView
        rv_todo_list.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(this)
        rv_todo_list.adapter = todoAdapter


        //Setting up ViewModel and LiveData
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList()?.observe(this, Observer {
            todoAdapter.setAllTodoList(it)
        })

        //FAB click listener
        fab_new_todo.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }
    }


    /**
     * RecyclerView Item callbacks
     * */
    //Callback when user clicks on Delete note
    override fun onDeleteClicked(todo: Todo) {
        showAlertDialog(todo)
    }

    //Callback when user clicks on view note
    override fun onViewClicked(todo: Todo) {
        val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
        intent.putExtra(Constants.INTENT_OBJECT, todo)
        startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
    }


    /**
     * Activity result callback
     * Triggers when Save button clicked from @CreateTodoActivity
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val todo = data?.getParcelableExtra<Todo>(Constants.INTENT_OBJECT)!!
            when (requestCode) {
                Constants.INTENT_CREATE_TODO -> {
                    todoViewModel.insertTodo(todo)
                }
                Constants.INTENT_UPDATE_TODO -> {
                    todoViewModel.updateTodo(todo)
                }
            }
        }
    }


    private fun showAlertDialog(todo: Todo) {
        val dialogMessage = "Apakah anda yakin ingin menghapus item ini?"
        val dialogTitle = "Hapus Item"


        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                todoViewModel.deleteTodo(todo)
                Toast.makeText(this, "Satu item berhasil dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    /**
     * Initialize Menu on Main
     **/
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_default -> {
                todoViewModel.getAllTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.sort_bycreate -> {
                todoViewModel.getOrderbyCreateTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.sort_bydue -> {
                todoViewModel.getOrderbyDueDateTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.search -> {

            }
            R.id.about -> {

            }
        }

        return super.onOptionsItemSelected(item)
    }


}
