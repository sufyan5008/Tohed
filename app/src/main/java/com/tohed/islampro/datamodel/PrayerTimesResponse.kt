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
            val readable: String,
            val timestamp: String,
            val hijri: Hijri,
            val gregorian: Gregorian
        ) {
            data class Hijri(
                val date: String,
                val day: String,
                val month: Month,
                val year: String,
                val designation: Designation
            ) {
                data class Month(
                    val number: Int,
                    val en: String,
                    val ar: String
                )

                data class Designation(
                    val abbreviated: String,
                    val expanded: String
                )
            }

            data class Gregorian(
                val date: String,
                val day: String,
                val weekday: Weekday,
                val month: Month,
                val year: String,
                val designation: Designation
            ) {
                data class Month(
                    val number: Int,
                    val en: String
                )

                data class Weekday(
                    val en: String
                )

                data class Designation(
                    val abbreviated: String,
                    val expanded: String
                )
            }
        }
    }
}


/*
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
                    val en: String
                )
            }
        }
    }
}*/
