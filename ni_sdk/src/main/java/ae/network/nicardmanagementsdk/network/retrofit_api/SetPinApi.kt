package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.set_pin.*
import retrofit2.http.*

interface SetPinApi {

    @POST("cards/lookup")
    suspend fun getCardsLookUp(
        @HeaderMap headers: Map<String, String>,
        @Body cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierResponseDto

    @GET("security/pin_certificate")
    suspend fun getPinCertificate(
        @HeaderMap headers: Map<String, String>,
    ): PinCertificateResponseDto

    @POST("security/set_pin")
    suspend fun setPin(
        @HeaderMap headers: Map<String, String>,
        @Body setPinBody: SetPinBodyDto
    )
}