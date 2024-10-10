package ae.network.nicardmanagementsdk.api.models.input

import ae.network.nicardmanagementsdk.R
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import java.io.Serializable

data class NIInput(
    val bankCode: String,
    val cardIdentifierId: String,
    val cardIdentifierType: String,
    val connectionProperties: NIConnectionProperties,
    val displayAttributes: NIDisplayAttributes? = null
) : Serializable

data class PinResultAttributes(
    val successScreen: PinResultScreenAttributes,
    val errorScreen: PinResultScreenAttributes
): Serializable

data class PinResultScreenAttributes(
    @LayoutRes val layoutId: Int,
    @IdRes val buttonResId: Int
): Serializable
data class PinManagementSetPinResources (
    var navTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_title_en)
    var screenTitleText: UIElementText, // = UIElementText.Int(R.string.set_pin_description_enter_pin_en)
    var secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    var notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en

    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val resultAttributes: PinResultAttributes? = null,
) : Serializable

data class PinManagementVerifyPinResources (
    var navTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_title_en)
    var screenTitleText: UIElementText, // = UIElementText.Int(R.string.verify_pin_description_en)
    var secondStepTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    var notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en

    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val resultAttributes: PinResultAttributes? = null,
) : Serializable

data class PinManagementChangePinResources (
    var navTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_title_en)
    var screenTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_current_pin_en)
    var newPinTitleText: UIElementText, // = UIElementText.Int(R.string.change_pin_description_enter_new_pin_en))
    var approvePinTitleText: UIElementText, // R.string.set_pin_description_re_enter_pin_en
    var notMatchTitleText: UIElementText, // R.string.set_pin_description_pin_not_match_en

    // if set an custom layout will be displayed on completion, and the component will navigate
    // back on "doneButton" specified as an @IdRes val buttonResId: Int
    val resultAttributes: PinResultAttributes? = null,
) : Serializable

data class PinManagementViewPinResources (
    var timerTemplate: UIElementText, // = UIElementText.Int(R.string.get_pin_countdown_timer_text_en)
) : Serializable
data class PinManagementResources (
    var setPin: PinManagementSetPinResources,
    var verifyPin: PinManagementVerifyPinResources,
    var changePin: PinManagementChangePinResources,
    var viewPin: PinManagementViewPinResources,
): Serializable {
    companion object {
        // ResultAttributes: if set an custom layout will be displayed on completion, and the component will navigate
        // back on "doneButton" specified as an @IdRes val buttonResId: Int
        fun default(
            setPinResultAttributes: PinResultAttributes? = null,
            verifyPinMessageAttributes: PinResultAttributes? = null,
            changePinResultAttributes: PinResultAttributes? = null
        ) = PinManagementResources(
            setPin = PinManagementSetPinResources(
                navTitleText = UIElementText.Int(R.string.set_pin_title_en),
                screenTitleText = UIElementText.Int(R.string.set_pin_description_enter_pin_en),
                secondStepTitleText = UIElementText.Int(R.string.set_pin_description_re_enter_pin_en),
                notMatchTitleText = UIElementText.Int(R.string.set_pin_description_pin_not_match_en),
                resultAttributes = setPinResultAttributes,
            ),
            verifyPin = PinManagementVerifyPinResources(
                navTitleText = UIElementText.Int(R.string.verify_pin_title_en),
                screenTitleText = UIElementText.Int(R.string.verify_pin_description_en),
                secondStepTitleText = UIElementText.Int(R.string.set_pin_description_re_enter_pin_en),
                notMatchTitleText = UIElementText.Int(R.string.set_pin_description_pin_not_match_en),
                resultAttributes = verifyPinMessageAttributes,
            ),
            changePin = PinManagementChangePinResources(
                navTitleText = UIElementText.Int(R.string.change_pin_title_en),
                screenTitleText = UIElementText.Int(R.string.change_pin_description_enter_current_pin_en),
                newPinTitleText = UIElementText.Int(R.string.change_pin_description_enter_new_pin_en),
                approvePinTitleText = UIElementText.Int(R.string.set_pin_description_re_enter_pin_en),
                notMatchTitleText = UIElementText.Int(R.string.set_pin_description_pin_not_match_en),
                resultAttributes = changePinResultAttributes,
            ),
            viewPin = PinManagementViewPinResources(
                timerTemplate = UIElementText.Int(R.string.get_pin_countdown_timer_text_en)
            )
        )
    }
}