package ae.network.nicardmanagementsdk.api.models.input

import java.io.Serializable

data class NIDisplayAttributes(
    // if set, these fonts will be used in the UI forms; if not set will use default fonts
    val fonts: List<NIFontLabelPair>? = null,
    // if set, the card details will take into account the attributes passed into this variable; if not set, will take the default values
    val cardAttributes: NICardAttributes? = null
): Serializable