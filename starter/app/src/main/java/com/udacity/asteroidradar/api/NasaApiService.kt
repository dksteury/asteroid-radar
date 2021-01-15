package com.udacity.asteroidradar

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.reflect.Type

private val retrofit = Retrofit.Builder()
        .addConverterFactory(StringOrJsonConverterFactory.create())
        .baseUrl(Constants.BASE_URL)
        .build()

interface NasaApodService {
    @GET("planetary/apod") @JsonAno
    suspend fun getApod(@Query("api_key") type : String ) : PictureOfDay
}

interface NasaAsteroidService {
    @GET("neo/rest/v1/feed") @StringAno
    suspend fun getAstroids(
        @Query("start_date") startDate : String,
        @Query("end_date") endDate : String,
        @Query("api_key") apiKey : String) : String
}

object NasaApi {
    val apodService : NasaApodService by lazy { retrofit.create(NasaApodService::class.java) }
    val asteroidService : NasaAsteroidService by lazy {retrofit.create(NasaAsteroidService::class.java)}
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention()
internal annotation class StringAno

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention()
internal annotation class JsonAno

class StringOrJsonConverterFactory : Converter.Factory() {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        annotations.forEach { annotation ->
            when (annotation) {
                is StringAno -> return ScalarsConverterFactory.create()
                    .responseBodyConverter(type, annotations, retrofit)
                is JsonAno -> return MoshiConverterFactory.create(moshi)
                    .responseBodyConverter(type, annotations, retrofit)
            }
        }
        return null
    }

    companion object {
        fun create() = StringOrJsonConverterFactory()
    }
}