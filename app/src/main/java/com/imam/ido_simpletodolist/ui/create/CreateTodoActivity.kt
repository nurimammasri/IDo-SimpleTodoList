package com.imam.ido_simpletodolist.ui.create

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.db.todo.Todo
import com.imam.ido_simpletodolist.utils.Constants
import kotlinx.android.synthetic.main.activity_create_todo.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class CreateTodoActivity : AppCompatActivity() {

    private var todo: Todo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_todo)

        result_duedate.visibility = View.INVISIBLE
        //Prepopulate existing title and content from intent
        val intent = intent
        if (intent != null && intent.hasExtra(Constants.INTENT_OBJECT)) {
            val todo: Todo? = intent.getParcelableExtra(Constants.INTENT_OBJECT)
            this.todo = todo
            prePopulateData(todo)
        }

        val titleBar =
            if (todo != null) getString(R.string.viewOrEditTodo) else getString(R.string.createTodo)

        btnDate.setOnClickListener {
            handleDateButton()
        }

        btnTime.setOnClickListener {
            handleTimeButton()
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = titleBar
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)


        edt_date.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                result_duedate.visibility = View.VISIBLE
                result_date.text = edt_date.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        edt_time.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                result_duedate.visibility = View.VISIBLE
                result_time.text = edt_time.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun afterTextChanged(s: Editable) {
            }
        })

    }

    private fun prePopulateData(todo: Todo?) {
        tv_title.setText(todo?.title)
        tv_content.setText(todo?.content)
        edt_date.setText(todo?.dueAt?.let { toDate(it) })
        edt_time.setText(todo?.dueAt?.let { toTime(it) })
        if (todo?.dueAt != null) {
            result_duedate.visibility = View.VISIBLE
            result_date.text = toDate(todo.dueAt)
            result_time.text = toTime(todo.dueAt)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflate = menuInflater
        menuInflate.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.save_todo -> {
                saveTodo()
            }
            android.R.id.home -> {
                showAlertDialog()
            }
        }
        return true
    }

    override fun onBackPressed() {
        showAlertDialog()
    }

    private fun showAlertDialog() {
        val dialogTitle = "Batal"
        val dialogMessage = "Apakah anda ingin membatalkan perubahan pada form?"

        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(dialogTitle)
        alertDialogBuilder
            .setMessage(dialogMessage)
            .setCancelable(false)
            .setPositiveButton("Ya") { dialog, id ->
                finish()
            }
            .setNegativeButton("Tidak") { dialog, id -> dialog.cancel() }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    /**
     * Sends the updated information back to calling Activity
     * */
    private fun saveTodo() {
        if (validateFields()) {
            val date = Date()
            val format = SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale.getDefault())
            val dueDate: Date? =
                format.parse(edt_date.text.toString() + " " + edt_time.text.toString())

            val id = if (todo != null) todo?.id else null
            val todo = dueDate?.let {
                Todo(
                    id = id,
                    title = tv_title.text.toString(),
                    content = tv_content.text.toString(),
                    createAt = date,
                    updateAt = date,
                    dueAt = it
                )
            }
            Toast.makeText(this, "Berhasil disimpan", Toast.LENGTH_SHORT).show()
            val intent = Intent()
            intent.putExtra(Constants.INTENT_OBJECT, todo)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    /**
     * Validation of EditText
     * */
    private fun validateFields(): Boolean {
        if (tv_title.text.isEmpty()) {
            til_todo_title.error = getString(R.string.pleaseEnterTitle)
            tv_title.requestFocus()
            return false
        }
        if (tv_content.text.isEmpty()) {
            til_todo_content.error = getString(R.string.pleaseEnterContent)
            tv_content.requestFocus()
            return false
        }
        if (edt_date.text.isEmpty()) {
            til_todo_duedate.error = getString(R.string.pleaseEnterDueDate)
            edt_date.requestFocus()
            return false
        }
        if (edt_time.text.isEmpty()) {
            til_todo_duetime.error = getString(R.string.pleaseEnterDueTime)
            edt_time.requestFocus()
            return false
        }

        return true
    }

    /**
     * Due Date
     * */
    private fun handleDateButton() {
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH) + 1
        val date: Int = calendar.get(Calendar.DATE)

        val datePickerDialog = DatePickerDialog(
            this,
            OnDateSetListener { _, Year, Month, Date ->
                calendar.set(Calendar.YEAR, Year)
                calendar.set(Calendar.MONTH, Month)
                calendar.set(Calendar.DATE, Date)
                val dateString = toDate(calendar.time)
                edt_date.setText(dateString)
            }, year, month, date
        )
        datePickerDialog.show()

    }

    private fun handleTimeButton() {
        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR)
        val minute: Int = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            OnTimeSetListener { _, Hour, Minute ->
                calendar.set(Calendar.HOUR, Hour)
                calendar.set(Calendar.MINUTE, Minute)
                val timeString = toTime(calendar.time)
                edt_time.setText(timeString)
            }, hour, minute, true
        )
        timePickerDialog.show()

    }

    /**
     * Date Format
     * */
    private fun toDate(date: Date): String {
        val outputFormatter: DateFormat = SimpleDateFormat("EEE, dd/MM/yyyy", Locale.getDefault())
        return outputFormatter.format(date)
    }

    private fun toTime(date: Date): String {
        val outputFormatter: DateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return outputFormatter.format(date)
    }

}
