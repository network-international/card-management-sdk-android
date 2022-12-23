package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType

interface NICardManagementFormsAPI {

    fun displayCardDetailsForm(
        input: NIInput
    )

    fun setPinForm(
        input: NIInput,
        type: NIPinFormType = NIPinFormType.DYNAMIC
    )

    fun changePinForm(
        input: NIInput,
        type: NIPinFormType = NIPinFormType.DYNAMIC
    )
}