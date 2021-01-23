package com.udacity.asteroidradar.detail

import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.domain.Asteroid

class DetailViewModel(selectedAsteroid: Asteroid) : ViewModel() {
    private val _asteroid = MutableLiveData<Asteroid>()
    val asteroid: LiveData<Asteroid>
        get() = _asteroid

    private val _hazardousText = MutableLiveData<String>()
    val hazardousText = Transformations.map(asteroid) {
        when (it.isPotentiallyHazardous) {
            true -> "potentially hazardous"
            else -> "not hazardous"
        }
    }

    init {
        _asteroid.value = selectedAsteroid
        _hazardousText.value = "hazardous"
        Log.i("DetailViewModel", "Init was run ${asteroid.value}")
    }
}