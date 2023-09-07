package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType

interface NICardManagementFormsAPI {

    fun displayCardDetailsForm(
        input: NIInput
    )

    fun displayViewPinForm(
        input: NIInput,
        pinFormType: NIPinFormType
    )
}