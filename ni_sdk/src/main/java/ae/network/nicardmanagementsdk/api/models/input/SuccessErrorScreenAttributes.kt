package ae.network.nicardmanagementsdk.api.models.input

import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import java.io.Serializable

data class PinMessageAttributes(
    val successAttributes: SuccessErrorScreenAttributes,
    val errorAttributes: SuccessErrorScreenAttributes
): Serializable

data class SuccessErrorScreenAttributes(
    @LayoutRes val layoutId: Int,
    @IdRes val buttonResId: Int
): Serializable