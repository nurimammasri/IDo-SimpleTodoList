package com.imam.ido_simpletodolist.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.imam.ido_simpletodolist.R
import com.imam.ido_simpletodolist.db.todo.Todo
import kotlinx.android.synthetic.main.item_todo.view.*
import java.text.SimpleDateFormat
import java.util.*


class TodoAdapter(todoEvents: TodoEvents) : RecyclerView.Adapter<TodoAdapter.ViewHolder>(),
    Filterable {

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

            //Apply Animations
            itemView.layout_item.animation =
                AnimationUtils.loadAnimation(itemView.context, R.anim.fall_down_animation)

            itemView.tv_title.text = todo.title
            itemView.tv_content.text = todo.content
            itemView.tv_createdate.text = dateFormat.format(todo.createAt)
            itemView.tv_updatedate.text = dateFormat.format(todo.updateAt)
            itemView.tv_duedate.text = dateFormat.format(todo.dueAt)

            itemView.check_finished.isChecked = todo.finished

            if (todo.finished) {
                itemView.tv_check_finished.text =
                    itemView.resources.getString(R.string.mark_as_finished)

                itemView.layout_item.setCardBackgroundColor(itemView.resources.getColor(R.color.black))
                itemView.label_color.setColorFilter(itemView.resources.getColor(R.color.grey))

                itemView.tv_title.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.tv_content.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.title_lastupdate.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.title_duedate.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.ic_delete.setColorFilter(itemView.resources.getColor(R.color.grey))

                itemView.tv_createdate.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.tv_updatedate.setTextColor(itemView.resources.getColor(R.color.grey))
                itemView.tv_duedate.setTextColor(itemView.resources.getColor(R.color.grey))
            } else {
                itemView.tv_check_finished.text =
                    itemView.resources.getString(R.string.mark_as_notfinish)

                itemView.layout_item.setCardBackgroundColor(itemView.resources.getColor(R.color.white))
                itemView.label_color.setColorFilter(itemView.resources.getColor(R.color.colorPrimary))

                itemView.tv_title.setTextColor(itemView.resources.getColor(R.color.black))
                itemView.tv_content.setTextColor(itemView.resources.getColor(R.color.black))
                itemView.title_lastupdate.setTextColor(itemView.resources.getColor(R.color.black))
                itemView.title_duedate.setTextColor(itemView.resources.getColor(R.color.black))
                itemView.ic_delete.setColorFilter(itemView.resources.getColor(R.color.black_delete))


                itemView.tv_createdate.setTextColor(itemView.resources.getColor(R.color.blue))
                itemView.tv_updatedate.setTextColor(itemView.resources.getColor(R.color.orange))
                itemView.tv_duedate.setTextColor(itemView.resources.getColor(R.color.red))
            }

            itemView.check_finished.setOnClickListener {
                listener.onCheckFinishedClicked(todo, itemView)
            }

            itemView.ic_delete.setOnClickListener {
                listener.onDeleteClicked(todo)
            }

            itemView.setOnClickListener {
                listener.onViewClicked(todo)
            }

            itemView.setOnLongClickListener {
                listener.onViewLongClicked(todo)
                true
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
                        if (row.title.toLowerCase(Locale.ROOT).contains(
                                charString.toLowerCase(
                                    Locale.ROOT
                                )
                            ) || row.content.contains(charString.toLowerCase(Locale.ROOT))
                        ) {
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
        fun onViewLongClicked(todo: Todo)
        fun onCheckFinishedClicked(
            todo: Todo,
            itemView: View
        )
    }
}
