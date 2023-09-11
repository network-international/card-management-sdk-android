package ae.network.nicardmanagementsdk.api.models.input

import java.io.Serializable

data class NIDisplayAttributes(
    // this parameter is optional
    // if set, these fonts will be used in the UI forms; if not set will use default fonts
    val fonts: List<NIFontLabelPair>? = null,

    // this parameter is optional
    // if set, the card details will take into account the attributes passed into this variable
    // if not set, will take the default values
    val cardAttributes: NICardAttributes? = null,

    // the next three parameter are optional
    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val setPinMessageAttributes: PinMessageAttributes? = null,
    val verifyPinMessageAttributes: PinMessageAttributes? = null,
    val changePinMessageAttributes: PinMessageAttributes? = null,

    // This parameter is optional.
    // If not set the SDK will follow your parent app day/night mode based on OS settings or as requested by your app.
    // The recommended way for using this parameter is to leave it unset, unless you have some special requirements.
    // If a value is set, the SDK will emulate (force) day/night mode, regardless of the system settings.
    val theme: NITheme? = null,

    // This parameter is optional.
    // If not set, the SDK will automatically choose English as default language.
    // If a value is set, the SDK will force English/Arabic language.
    val language: NILanguage? = null
): Serializable

enum class NITheme: Serializable {
    LIGHT, DARK_APP_COMPAT, DARK_MATERIAL
}

enum class NILanguage : Serializable {
    ENGLISH, ARABIC
}