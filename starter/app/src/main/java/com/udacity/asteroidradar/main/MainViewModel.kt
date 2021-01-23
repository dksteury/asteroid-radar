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

enum class AsteroidFilter {TODAY, WEEK, ALL}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _apod = MutableLiveData<PictureOfDay>()
    val apod: LiveData<PictureOfDay>
        get() = _apod

    private val _filter = MutableLiveData<AsteroidFilter>()

    val asteroids = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidFilter.TODAY -> asteroidsRepository.asteroidsToday
            AsteroidFilter.WEEK -> asteroidsRepository.asteroidsWeek
            else -> asteroidsRepository.asteroidsAll
        }
    }

    private val _navigateToDetail = MutableLiveData<Asteroid>()
    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        _filter.value = AsteroidFilter.WEEK
        viewModelScope.launch {
            try {
                asteroidsRepository.refreshAsteroids()
            } catch (e: Exception) {
                Log.i("MainViewModel", "Failure: ${e.message}")
            }
        }
        getNasaApod()
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

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetail.value = null
    }

    fun updateFilter(filter: AsteroidFilter) {
        _filter.value = filter
    }

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