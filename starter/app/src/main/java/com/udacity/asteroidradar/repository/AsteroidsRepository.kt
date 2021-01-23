package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.NasaApi
import com.udacity.asteroidradar.api.asDatabaseModel
import com.udacity.asteroidradar.api.parseStringToAsteroidList
import com.udacity.asteroidradar.api.parseStringToNetworkAsteroidContainer
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.domain.Asteroid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(private val database: AsteroidsDatabase) {

    val today  = Calendar.getInstance().time.toString(Constants.API_QUERY_DATE_FORMAT)

    val weekFromToday = getNextWeek()

    val asteroidsToday : LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids(today, today)) {
                it.asDomainModel()
            }

    val asteroidsWeek : LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids(today, weekFromToday)) {
                it.asDomainModel()
            }

    val asteroidsAll : LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids()) {
                it.asDomainModel()
            }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            Log.i("refreshAsteroids", "Executed!")
            Log.i("MainViewModel", "Calendar: $today")
            Log.i("MainViewModel", "Calendar: $weekFromToday")
            val asteroidString = NasaApi.asteroidService.getAstroidsNetwork(today, weekFromToday, BuildConfig.API_KEY)
            val asteroidContainer = parseStringToNetworkAsteroidContainer(asteroidString)
            database.asteroidDao.insertAll(*asteroidContainer.asDatabaseModel())
        }
    }

    suspend fun deleteAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.clear()
        }
    }

    private fun getNextWeek(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
        return calendar.time.toString(Constants.API_QUERY_DATE_FORMAT)
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()) : String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}