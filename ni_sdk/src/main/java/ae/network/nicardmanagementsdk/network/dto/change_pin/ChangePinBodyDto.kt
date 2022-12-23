package ae.network.nicardmanagementsdk.network.dto.change_pin

import com.google.gson.annotations.SerializedName

data class ChangePinBodyDto(
    @SerializedName("card_identifier_id") val cardIdentifierId: String,
    @SerializedName("encrypted_old_pin") val encryptedOldPin: String,
    @SerializedName("encrypted_new_pin") val encryptedNewPin: String,
    @SerializedName("encryption_method") val encryptionMethod: String,
    @SerializedName("card_identifier_type") val cardIdentifierType: String,
)

enum class EncryptionMethodEnum {
    ASYMMETRIC_ENC,
    SYMMETRIC_ENC
}

enum class CardIdentifierTypeEnum {
    EXID, CONTRACT_NUMBER
}