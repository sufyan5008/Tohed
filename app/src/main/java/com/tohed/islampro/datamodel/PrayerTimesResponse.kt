package com.tohed.islampro.datamodel

data class PrayerTimesResponse(
    val code: Int,
    val status: String,
    val data: Data
) {
    data class Data(
        val timings: Map<String, String>,
        val date: Date
    ) {
        data class Date(
            val gregorian: Gregorian,
            val hijri: Hijri
        ) {
            data class Gregorian(
                val date: String
            )

            data class Hijri(
                val date: String,
                val month: Month
            ) {
                data class Month(
                    val ar: String
                )
            }
        }
    }
}