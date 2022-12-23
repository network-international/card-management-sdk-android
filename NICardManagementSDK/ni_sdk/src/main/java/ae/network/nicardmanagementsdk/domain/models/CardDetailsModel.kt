package ae.network.nicardmanagementsdk.domain.models

data class CardDetailsModel(
    val clearPan: String?,
    val maskedPan: String?,
    val clearExpiry: String?,
    val maskedExpiry: String?,
    val clearCVV2: String?,
    val maskedCVV2: String?,
    val clearCardholderName: String?,
    val maskedCardholderName: String?
)
