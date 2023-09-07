package ae.network.nicardmanagementsdk.network.dto.view_pin

import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse
import com.google.gson.annotations.SerializedName

data class PINViewResponseDto(
    @SerializedName("encrypted_pin") val encryptedPin: String
)

fun PINViewResponseDto.toModel(): ViewPinResponse {
    return ViewPinResponse(encryptedPin)
}