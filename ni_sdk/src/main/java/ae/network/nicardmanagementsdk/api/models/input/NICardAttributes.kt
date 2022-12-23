package ae.network.nicardmanagementsdk.api.models.input

import androidx.annotation.DrawableRes
import java.io.Serializable

data class NICardAttributes(
    // if true, the card details will be hidden/masked by default; if false, the card details will be visible by default
    val shouldHide: Boolean = true,
    // if set, this image will be used as background for the card details view; if not set, it will use default image from sdk
    @DrawableRes
    val backgroundImage: Int? = null
): Serializable
