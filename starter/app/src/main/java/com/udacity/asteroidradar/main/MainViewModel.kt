package com.udacity.asteroidradar.main

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.Constants.API_QUERY_DATE_FORMAT
import com.udacity.asteroidradar.NasaApi
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.api.parseStringToAsteroidList
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {

    val today : String  = Calendar.getInstance().time.toString(API_QUERY_DATE_FORMAT)

    private val _apod = MutableLiveData<PictureOfDay>()
    val apod: LiveData<PictureOfDay>
        get() = _apod

    private val _asteroidString = MutableLiveData<String>()
    val asteroidString: LiveData<String>
        get() = _asteroidString

    val asteroids = Transformations.map(asteroidString) {
        parseStringToAsteroidList(it)
    }

    private val _navigateToDetail = MutableLiveData<Asteroid>()
    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    init {
        getNasaApod()
        getNasaAsteroid()
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

    private fun getNasaAsteroid() {
        viewModelScope.launch {
            try {
                _asteroidString.value = NasaApi.asteroidService.getAstroids(today, today, BuildConfig.API_KEY)
            } catch (e: Exception) {
                Log.i("MainViewModel", "Failure: ${e.message}")
                Log.i("MainViewModel", "Calendar: $today")
            }
        }
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigateToDetail.value = null
    }

    fun Date.toString(format: String, locale: Locale = Locale.getDefault()) : String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }
}