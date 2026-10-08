package ae.network.nicardmanagementsdk.network.retrofit_api

import ae.network.nicardmanagementsdk.network.dto.view_pin.PINViewBodyDto
import ae.network.nicardmanagementsdk.network.dto.view_pin.PINViewResponseDto
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface ViewPinApi : PinApi {

    @POST("security/view_pin")
    suspend fun viewPin(
        @HeaderMap headers: Map<String, String>,
        @Body pinViewBodyDto: PINViewBodyDto
    ): PINViewResponseDto
}