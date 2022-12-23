package ae.network.nicardmanagementsdk.api.models.output

import ae.network.nicardmanagementsdk.helpers.toSpacedPAN
import ae.network.nicardmanagementsdk.helpers.toStarMaskedString
import ae.network.nicardmanagementsdk.presentation.models.CardDetailsModel

data class NICardDetailsResponse(
    val clearPan: String?,
    val maskedPan: String?,
    val expiry: String?,      // TODO: - TBD if type Date or String
    val clearCVV2: String?,
    val clearCardholderName: String?
)

data class CardDetailsResponse(
    val encryptedPan: String,
    val maskedPan: String,
    val expiry: String,
    val encryptedCvv2: String,
    val clearCardholderName: String
)

fun NICardDetailsResponse.asClearViewModel(): CardDetailsModel {
    return CardDetailsModel(
        clearPan?.toSpacedPAN(),
        "${expiry?.substring(2..3)}/${expiry?.substring(0..1)}",
        clearCVV2,
        clearCardholderName?.trim()
    )
}

fun NICardDetailsResponse.asMaskedViewModel(): CardDetailsModel {
    return CardDetailsModel(
        maskedPan?.toSpacedPAN(),
        "**/**",
        "***",
        clearCardholderName?.toStarMaskedString(2)
    )
}

fun NICardDetailsResponse.asClearPanNonSpaced(): String {
    return clearPan.toString()
}
