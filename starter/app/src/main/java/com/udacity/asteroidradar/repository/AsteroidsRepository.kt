package com.udacity.asteroidradar.repository

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

    val today : String  = Calendar.getInstance().time.toString(Constants.API_QUERY_DATE_FORMAT)

    val asteroids : LiveData<List<Asteroid>> =
            Transformations.map(database.asteroidDao.getAsteroids()) {
                it.asDomainModel()
            }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val asteroidString = NasaApi.asteroidService.getAstroidsNetwork(today, today, BuildConfig.API_KEY)
            val asteroidContainer = parseStringToNetworkAsteroidContainer(asteroidString)
            database.asteroidDao.insertAll(*asteroidContainer.asDatabaseModel())
        }
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()) : String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}