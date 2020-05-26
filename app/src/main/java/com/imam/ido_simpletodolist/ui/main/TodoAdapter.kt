package com.imam.ido_simpletodolist.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.db.todo.Todo
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*


class TodoAdapter(todoEvents: TodoEvents) : RecyclerView.Adapter<TodoAdapter.ViewHolder>(), Filterable {

    private var todoList: List<Todo> = arrayListOf()
    private var filteredTodoList: List<Todo> = arrayListOf()
    private val listener: TodoEvents = todoEvents

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = filteredTodoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(filteredTodoList[position], listener)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // Constant for date format
        private val DATE_FORMAT = "EEE, dd/MM/yyyy HH:mm"

        // Date formatter
        private val dateFormat = SimpleDateFormat(
            DATE_FORMAT,
            Locale.getDefault()
        )

        fun bind(todo: Todo, listener: TodoEvents) {
            itemView.tv_title.text = todo.title
            itemView.tv_content.text = todo.content
            itemView.tv_createdate.text = dateFormat.format(todo.createAt)
            itemView.tv_updatedate.text = dateFormat.format(todo.updateAt)
            itemView.tv_duedate.text = dateFormat.format(todo.dueAt)

            itemView.ic_delete.setOnClickListener {
                listener.onDeleteClicked(todo)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(todo)
            }
        }
    }


    /**
     * Activity uses this method to update todoList with the help of LiveData
     * */
    fun setAllTodoList(todoItems: List<Todo>) {
        this.todoList = todoItems
        this.filteredTodoList = todoItems
        notifyDataSetChanged()
    }

    /**
     * Search Filter implementation
     * */
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0.toString()
                filteredTodoList = if (charString.isEmpty()) {
                    todoList
                } else {
                    val filteredList = arrayListOf<Todo>()
                    for (row in todoList) {
                        if (row.title.toLowerCase(Locale.ROOT).contains(charString.toLowerCase(Locale.ROOT)) || row.content.contains(charString.toLowerCase(Locale.ROOT))) {
                            filteredList.add(row)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = filteredTodoList
                return filterResults
            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                filteredTodoList = p1?.values as List<Todo>
                notifyDataSetChanged()
            }

        }
    }


    /**
     * RecycleView touch event callbacks
     * */
    interface TodoEvents {
        fun onDeleteClicked(todo: Todo)
        fun onViewClicked(todo: Todo)
    }
}
