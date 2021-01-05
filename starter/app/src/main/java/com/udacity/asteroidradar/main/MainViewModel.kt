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

    private val _apod = MutableLiveData<PictureOfDay>()

    val apod: LiveData<PictureOfDay>
        get() = _apod

    init {
        getNasaApod()
    }

    private fun getNasaApod() {
        viewModelScope.launch {
            try {
                _apod.value = NasaApi.retrofitService.getApod(BuildConfig.API_KEY)
            } catch (e: Exception) {
                _apod.value = PictureOfDay("","Failure: ${e.message}","")
            }
        }
    }
//        NasaApi.retrofitService.getApod(BuildConfig.API_KEY).enqueue(object : Callback<PictureOfDay> {
//            override fun onResponse(call: Call<PictureOfDay>, response: Response<PictureOfDay>) {
//                _response.value = "${response.body()?.title}"
//            }
//
//            override fun onFailure(call: Call<PictureOfDay>, t: Throwable) {
//                _response.value = "Failure: " + t.message
//            }
//        })
//    }
}