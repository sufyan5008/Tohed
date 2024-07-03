package com.tohed.islampro.api


import com.tohed.islampro.datamodel.PrayerTimesResponse
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface PrayerService {
    @GET("timings/{date}")
    suspend fun getPrayerTimings(
        @Path("date") date: String,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int
    ): PrayerTimesResponse
}

private fun createHttpClient(latitude: Double, longitude: Double): OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            val modifiedUrl = originalRequest.url.newBuilder()
                .addQueryParameter("latitude", latitude.toString())
                .addQueryParameter("longitude", longitude.toString())
                .build()
            val modifiedRequest = originalRequest.newBuilder()
                .url(modifiedUrl)
                .build()
            chain.proceed(modifiedRequest)
        }
        .build()
}

fun createPrayerService(latitude: Double, longitude: Double): PrayerService {
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.aladhan.com/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(createHttpClient(latitude, longitude))
        .build()

    return retrofit.create(PrayerService::class.java)
}

suspend fun fetchPrayerTimings(latitude: Double, longitude: Double): PrayerTimesResponse {
    val currentDate = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
    val service = createPrayerService(latitude, longitude)
    return service.getPrayerTimings(currentDate, latitude, longitude, 2)
}