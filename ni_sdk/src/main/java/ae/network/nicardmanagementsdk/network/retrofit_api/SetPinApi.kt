package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.set_pin.*
import retrofit2.http.*

interface SetPinApi : PinApi {

    @POST("security/set_pin")
    suspend fun setPin(
        @HeaderMap headers: Map<String, String>,
        @Body setPinBody: SetVerifyPinBodyDto
    )
}