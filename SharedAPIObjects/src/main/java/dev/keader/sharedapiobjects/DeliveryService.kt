package dev.keader.sharedapiobjects

interface DeliveryService {
    suspend fun getProduct(code: String): ItemWithTracks
    fun validateCode(code: String): Boolean
    fun codeHasMultipleParams(): Boolean
    fun getDeliveryCompany(): DeliveryCompany
}
