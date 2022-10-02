package dev.keader.sharedapiobjects

import androidx.room.TypeConverter

enum class DeliveryCompany {
    CORREIOS
}

class DeliveryCompanyConverter {
    @TypeConverter
    fun toDeliveryCompany(value: String) = DeliveryCompany.valueOf(value)

    @TypeConverter
    fun fromDeliveryCompany(value: DeliveryCompany) = value.name
}
