package ae.network.nicardmanagementsdk.network.dto.card_details

import com.google.gson.annotations.SerializedName

data class X509CertificateBodyDto(
    @SerializedName("publicKey") val publicKey : String
)
