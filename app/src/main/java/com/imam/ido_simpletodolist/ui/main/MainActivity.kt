package com.imam.ido_simpletodolist.ui.main

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
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
            setVisibilityImageEmpty(it)
        })

        //FAB click listener
        fab_new_todo.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }


    }

    private fun setVisibilityImageEmpty(allTodoList: List<Todo>){
        if (allTodoList.isEmpty()) {
            layout_recyclerview.visibility = View.GONE
            img_empty_item.visibility = View.VISIBLE
            img_empty_item.animation = AnimationUtils.loadAnimation(applicationContext, R.anim.fade_transition_animation)
        }
        else {
            layout_recyclerview.visibility = View.VISIBLE
            img_empty_item.visibility = View.GONE
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
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.search)
        val searchView: SearchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(searchManager
            .getSearchableInfo(componentName))
        searchView.maxWidth = Integer.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                todoAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                todoAdapter.filter.filter(newText)
                return false
            }

        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_default -> {
                todoViewModel.getAllTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                    setVisibilityImageEmpty(it)
                })
            }
            R.id.sort_bycreate -> {
                todoViewModel.getOrderbyCreateTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                    setVisibilityImageEmpty(it)
                })
            }
            R.id.sort_bydue -> {
                todoViewModel.getOrderbyDueDateTodoList()?.observe(this, Observer {
                    todoAdapter.setAllTodoList(it)
                    setVisibilityImageEmpty(it)
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
