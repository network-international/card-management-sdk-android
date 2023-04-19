package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierResponseDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.PinCertificateResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface PinApi {

    @POST("cards/lookup")
    suspend fun getCardsLookUp(
        @HeaderMap headers: Map<String, String>,
        @Body cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierResponseDto

    @GET("security/pin_certificate")
    suspend fun getPinCertificate(
        @HeaderMap headers: Map<String, String>,
    ): PinCertificateResponseDto
}