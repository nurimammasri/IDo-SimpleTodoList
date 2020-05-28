package com.imam.ido_simpletodolist.db.todo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.imam.ido_simpletodolist.utils.DateConverter
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "todo")
@Parcelize
data class Todo(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,

    @ColumnInfo(name = "created_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateConverter::class)
    val createAt: Date,

    @ColumnInfo(name = "update_at", defaultValue = "CURRENT_TIMESTAMP")
    @TypeConverters(DateConverter::class)
    val updateAt: Date,

    @ColumnInfo(name = "due_date")
    @TypeConverters(DateConverter::class)
    val dueAt: Date,

    @ColumnInfo(name = "finished")
    var finished: Boolean
) : Parcelable