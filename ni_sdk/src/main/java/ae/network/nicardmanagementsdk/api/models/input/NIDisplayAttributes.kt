package ae.network.nicardmanagementsdk.api.models.input

import androidx.annotation.StyleRes
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

    // this parameter is optional
    // if set the status bar will follow your current app theme visual aspect
    @StyleRes val theme: Int? = null
): Serializable