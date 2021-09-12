package dev.keader.sharedapiobjects

interface DeliveryService {
    val deliveryCompany: DeliveryCompany
    suspend fun getProduct(code: String): ItemWithTracks
    fun validateCode(code: String): Boolean
    fun codeHasMultipleParams(): Boolean
}
