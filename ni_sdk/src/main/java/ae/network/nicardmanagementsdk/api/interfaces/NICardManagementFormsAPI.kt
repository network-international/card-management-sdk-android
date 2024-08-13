package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.input.CardElementsConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

interface NICardManagementFormsAPI {

    fun displayCardDetailsForm(
        input: NIInput, @DrawableRes backgroundImage: Int?, @StringRes title: Int?, config: CardElementsConfig
    )

    fun displayViewPinForm(
        input: NIInput,
        pinFormType: NIPinFormType
    )
}