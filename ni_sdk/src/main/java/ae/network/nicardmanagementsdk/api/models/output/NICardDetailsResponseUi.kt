package ae.network.nicardmanagementsdk.api.models.output

import ae.network.nicardmanagementsdk.presentation.models.CardDetailsModel

fun NICardDetailsResponse.asClearViewModel(): CardDetailsModel {
    return CardDetailsModel(
        clearPan,
        expiry,
        clearCVV2,
        clearCardholderName?.trim()
    )
}

fun NICardDetailsResponse.asMaskedViewModel(): CardDetailsModel {
    return CardDetailsModel(
        maskedPan,
        "**/**",
        "***",
        clearCardholderName?.toStarMaskedName(2)
    )
}

fun NICardDetailsResponse.asClearPanNonSpaced(): String {
    return clearPan.toString()
}

private fun String.toStarMaskedName(range: Int): String {
    val names = this.replace("\\s+".toRegex(), " ").trim().split(" ")
    return names.joinToString(" ") { name ->
        when (name.length) {
            in 1..range -> name
            else -> name.take(range).padEnd(name.length, '*')
        }
    }
}
