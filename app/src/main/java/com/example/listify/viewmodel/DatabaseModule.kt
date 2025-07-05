package com.example.listify.viewmodel

import android.content.Context
import com.example.listify.database.TaskDao
import com.example.listify.database.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TaskDatabase {
        return TaskDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: TaskDatabase) : TaskDao {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideRepository(taskDao: TaskDao) : TaskRepository {
        return TaskRepository(taskDao)
    }
}