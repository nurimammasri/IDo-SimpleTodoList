package com.imam.ido_simpletodolist.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imam.ido_simpletodolist.db.todo.Todo
import com.imam.ido_simpletodolist.db.todo.TodoDao
import com.imam.ido_simpletodolist.utils.DateConverter

@Database(entities = [Todo::class], exportSchema = false, version = 3)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun todoDao(): TodoDao

    companion object {

        private const val DB_NAME = "TODO_DB"
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room
                        .databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            DB_NAME
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return instance
        }

    }

}