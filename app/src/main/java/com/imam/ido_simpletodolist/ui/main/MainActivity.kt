package com.imam.ido_simpletodolist.ui.main

import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.db.todo.Todo
import com.imam.ido_simpletodolist.notification.AlarmReceiver
import com.imam.ido_simpletodolist.ui.about.AboutTodoActivity
import com.imam.ido_simpletodolist.ui.create.CreateTodoActivity
import com.imam.ido_simpletodolist.utils.Constants
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.alert_dialog_longpress_view.*
import kotlinx.android.synthetic.main.alert_dialog_view.*
import kotlinx.android.synthetic.main.alert_dialog_view.btn_close
import kotlinx.android.synthetic.main.content_main.*
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), TodoAdapter.TodoEvents {

    private lateinit var alarmReceiver: AlarmReceiver

    private lateinit var todoViewModel: TodoViewModel
    private lateinit var todoAdapter: TodoAdapter

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        alarmReceiver = AlarmReceiver()

        //Setting Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarMain)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "IDo"
        toolbar.subtitle = "SimpleTodoList"


        drawer = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        //to set work change icon navigation, must be on below drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.mipmap.icon)

        //Setting up RecyclerView
        rv_todo_list.layoutManager = LinearLayoutManager(this)
        todoAdapter = TodoAdapter(this)
        rv_todo_list.adapter = todoAdapter


        //Setting up ViewModel and LiveData
        todoViewModel = ViewModelProvider(this).get(TodoViewModel::class.java)
        todoViewModel.getAllTodoList()?.observe(this, Observer {
            setVisibilityImageEmpty(it)
            todoAdapter.setAllTodoList(it)
        })

        //FAB click listener
        fab_new_todo.setOnClickListener {
            val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
            startActivityForResult(intent, Constants.INTENT_CREATE_TODO)
        }


    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    private fun setVisibilityImageEmpty(allTodoList: List<Todo>) {
        if (allTodoList.isEmpty()) {
            layout_recyclerview.visibility = View.GONE
            img_empty_item.visibility = View.VISIBLE
            info_empty.visibility = View.VISIBLE
            img_empty_item.animation =
                AnimationUtils.loadAnimation(applicationContext, R.anim.fade_transition_animation)
            info_empty.animation =
                AnimationUtils.loadAnimation(applicationContext, R.anim.fade_transition_animation)
        } else {
            layout_recyclerview.visibility = View.VISIBLE
            img_empty_item.visibility = View.GONE
            info_empty.visibility = View.GONE
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
        // Constant for date format
        val DATE_FORMAT = "EEE, dd/MM/yyyy HH:mm"

        // Date formatter
        val dateFormat = SimpleDateFormat(
            DATE_FORMAT,
            Locale.getDefault()
        )

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_view)
        dialog.tv_alert_title.text = todo.title
        dialog.tv_alert_desc.text = todo.content
        dialog.tv_alert_create.text = dateFormat.format(todo.createAt)
        dialog.tv_alert_update.text = dateFormat.format(todo.updateAt)
        dialog.tv_alert_due.text = dateFormat.format(todo.dueAt)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.btn_close.setOnClickListener {
            dialog.dismiss()
        }

        if (todo.finished) {
            dialog.tv_alert_check.text = getString(R.string.mark_as_finished)
        } else {
            dialog.tv_alert_check.text = getString(R.string.mark_as_notfinish)
        }

        dialog.btn_edit.setOnClickListener {
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_BEFORE_HOURS)
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_ONE_TIME)
            val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
            intent.putExtra(Constants.INTENT_OBJECT, todo)
            startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
            dialog.dismiss()
        }

        dialog.show()
    }

    //Callback when user clicks on view note
    override fun onViewLongClicked(todo: Todo) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog_longpress_view)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.btn_close.setOnClickListener {
            dialog.dismiss()
        }

        dialog.btn_delete_all.setOnClickListener {
            todoViewModel.deleteAll(todo)
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_ONE_TIME)
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_BEFORE_HOURS)
            dialog.dismiss()
        }

        dialog.btn_delete_allcomplete.setOnClickListener {
            todoViewModel.deleteFinished(todo)
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_ONE_TIME)
            alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_BEFORE_HOURS)
            dialog.dismiss()
        }

        dialog.show()
    }

    //Callback when user clicks on CheckFinished
    override fun onCheckFinishedClicked(
        todo: Todo,
        itemView: View
    ) {
        val mills = todo.dueAt.time
        if (!todo.finished) {
            alarmReceiver.finishAlarm(this, AlarmReceiver.TYPE_BEFORE_HOURS)
            alarmReceiver.finishAlarm(this, AlarmReceiver.TYPE_ONE_TIME)
        } else if (todo.finished && mills > 0 && System.currentTimeMillis() < mills) {
            alarmReceiver.setOneTimeAlarm(
                this,
                AlarmReceiver.TYPE_ONE_TIME,
                todo.dueAt,
                todo.title,
                "Your Item Todo Has Due Now, Please mark your completed items or update with edit"
            )
            if (todo.check_alarm_hour) {
                val calendar = Calendar.getInstance()
                calendar.time = todo.dueAt
                val hour = calendar[Calendar.HOUR_OF_DAY] - 1
                calendar.set(Calendar.HOUR_OF_DAY, hour)

                alarmReceiver.setBeforeHourAlarm(
                    this,
                    AlarmReceiver.TYPE_BEFORE_HOURS,
                    calendar.time,
                    todo.title + "    (Notification before 1 hours)",
                    "Soon Your Item Todo Has Due"
                )
            }
        } else {
            Snackbar.make(
                itemView,
                "Due Date has Expired, Delete Item or Edit",
                Snackbar.LENGTH_LONG
            )
                .setAction("Edit") {
                    val intent = Intent(this@MainActivity, CreateTodoActivity::class.java)
                    intent.putExtra(Constants.INTENT_OBJECT, todo)
                    startActivityForResult(intent, Constants.INTENT_UPDATE_TODO)
                }.show()
        }
        todoViewModel.setFinishedItemTodo(todo)
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
                alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_ONE_TIME)
                alarmReceiver.cancelAlarm(this, AlarmReceiver.TYPE_BEFORE_HOURS)
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
        val searchIcon = searchView.findViewById<ImageView>(R.id.search_button)
        searchIcon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
        val closeIcon = searchView.findViewById<ImageView>(R.id.search_close_btn)
        closeIcon.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.white))
        val editText = searchView.findViewById<EditText>(R.id.search_src_text)
        editText.setTextColor(resources.getColor(R.color.white))
        searchView.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
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
                    setVisibilityImageEmpty(it)
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.sort_bycreate -> {
                todoViewModel.getOrderbyCreateTodoList()?.observe(this, Observer {
                    setVisibilityImageEmpty(it)
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.sort_bydue -> {
                todoViewModel.getOrderbyDueDateTodoList()?.observe(this, Observer {
                    setVisibilityImageEmpty(it)
                    todoAdapter.setAllTodoList(it)
                })
            }
            R.id.about -> {
                val intent = Intent(this@MainActivity, AboutTodoActivity::class.java)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
