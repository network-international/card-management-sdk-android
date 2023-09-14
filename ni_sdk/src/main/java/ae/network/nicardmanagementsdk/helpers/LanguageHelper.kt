package ae.network.nicardmanagementsdk.helpers

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NILanguage

class LanguageHelper {

    fun getLanguage(niInput: NIInput): String =
        when (niInput.displayAttributes?.language) {
            NILanguage.ARABIC -> "ar"
            NILanguage.ENGLISH -> "en"
            null -> "en"
        }
}