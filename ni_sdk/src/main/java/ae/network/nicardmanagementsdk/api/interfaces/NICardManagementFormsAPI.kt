package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput

interface NICardManagementFormsAPI {

    fun displayCardDetailsForm(
        input: NIInput
    )
}