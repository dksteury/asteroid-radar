package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.BuildConfig
import com.udacity.asteroidradar.NasaApi
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _asteroidString = MutableLiveData<String>()

    val asteroidString: LiveData<String>
        get() = _asteroidString

    private val _apod = MutableLiveData<PictureOfDay>()

    val apod: LiveData<PictureOfDay>
        get() = _apod

    init {
        getNasaApod()
        getNasaAsteroid()
    }

    private fun getNasaApod() {
        viewModelScope.launch {
            try {
                _apod.value = NasaApi.apodService.getApod(BuildConfig.API_KEY)
            } catch (e: Exception) {
                _apod.value = PictureOfDay("","Failure: ${e.message}","")
            }
        }
    }

    private fun getNasaAsteroid() {
        viewModelScope.launch {
            try {
                _asteroidString.value = NasaApi.asteroidService.getAstroids("2021-01-05", "2021-01-06", BuildConfig.API_KEY)
            } catch (e: Exception) {
                _asteroidString.value = "Failure: ${e.message}"
            }
        }
    }
}