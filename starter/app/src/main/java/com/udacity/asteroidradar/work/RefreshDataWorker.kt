package com.udacity.asteroidradar.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository

class RefreshAsteroidWorker(appContext: Context, params: WorkerParameters):
        CoroutineWorker(appContext, params){

    companion object {
        const val WORK_NAME = "RefreshAsteroidWorker"
    }

    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidsRepository(database)
        return try {
            repository.refreshAsteroids()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

}