package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.NasaApi
import com.udacity.asteroidradar.domain.PictureOfDay
import com.udacity.asteroidradar.api.parseStringToAsteroidList
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

//    val today : String  = Calendar.getInstance().time.toString(API_QUERY_DATE_FORMAT)

    private val _apod = MutableLiveData<PictureOfDay>()
    val apod: LiveData<PictureOfDay>
        get() = _apod

//    private val _asteroidString = MutableLiveData<String>()
//    val asteroidString: LiveData<String>
//        get() = _asteroidString

//    val asteroids = Transformations.map(asteroidString) {
//        parseStringToAsteroidList(it)
//    }

    val asteroids = asteroidsRepository.asteroids

    private val _navigateToDetail = MutableLiveData<Asteroid>()
    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        getNasaApod()
//        getNasaAsteroid()
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {
                Log.i("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

    private fun getNasaApod() {
        viewModelScope.launch {
            try {
                _apod.value = NasaApi.apodService.getApod(BuildConfig.API_KEY)
            } catch (e: Exception) {
                _apod.value = PictureOfDay("","Failure: ${e.message}","R.drawable.placeholder_picture_of_day")
            }
        }
    }

//    private fun getNasaAsteroid() {
//        viewModelScope.launch {
//            try {
//                _asteroidString.value = NasaApi.asteroidService.getAstroidsNetwork(today, today, BuildConfig.API_KEY)
//            } catch (e: Exception) {
//                Log.i("MainViewModel", "Failure: ${e.message}")
//                Log.i("MainViewModel", "Calendar: $today")
//            }
//        }
//    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetail.value = null
    }

//    fun Date.toString(format: String, locale: Locale = Locale.getDefault()) : String {
//        val formatter = SimpleDateFormat(format, locale)
//        return formatter.format(this)
//    }

    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}