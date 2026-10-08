package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto
import retrofit2.http.*

interface ChangePinApi : PinApi {

    @POST("security/change_pin")
    suspend fun changePin(
        @HeaderMap headers: Map<String, String>,
        @Body changePinBody: ChangePinBodyDto
    )
}