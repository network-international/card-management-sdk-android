package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.set_pin.*
import retrofit2.http.*

interface VerifyPinApi : PinApi {

    @POST("security/verify_pin")
    suspend fun verifyPin(
        @HeaderMap headers: Map<String, String>,
        @Body verifyPinBody: SetVerifyPinBodyDto
    )
}