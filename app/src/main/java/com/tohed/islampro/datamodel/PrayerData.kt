package com.tohed.islampro.datamodel

data class PrayerData(
    val timings: Timings,
    val date: DateData,
    val meta: MetaData
)

data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String,
    val Midnight: String,
    val Firstthird: String,
    val Lastthird: String
)

data class DateData(
    val readable: String,
    val timestamp: String,
    val gregorian: GregorianDate,
    val hijri: HijriDate
)

data class GregorianDate(
    val date: String,
    val format: String,
    val day: String,
    val month: MonthData,
    val year: String,
    val designation: Designation
)

data class MonthData(
    val number: Int,
    val en: String
)

data class HijriDate(
    val date: String,
    val format: String,
    val day: String,
    val month: MonthData,
    val year: String,
    val designation: Designation
)

data class Designation(
    val abbreviated: String,
    val expanded: String
)

data class MetaData(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: MethodData
)

data class MethodData(
    val id: Int,
    val name: String,
    val params: ParamsData
)

data class ParamsData(
    val Fajr: Double,
    val Isha: Double
)
