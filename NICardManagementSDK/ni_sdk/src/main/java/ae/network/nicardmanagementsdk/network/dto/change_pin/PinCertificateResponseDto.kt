package ae.network.nicardmanagementsdk.network.dto.change_pin

import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import com.google.gson.annotations.SerializedName

data class PinCertificateResponseDto(
    @SerializedName("certificate") val certificate : String
)

fun PinCertificateResponseDto.asDomainModel(): PinCertificateModel {
    return PinCertificateModel(
        certificate
    )
}