package ae.network.nicardmanagementsdk.api.implementation

import ae.network.nicardmanagementsdk.api.interfaces.DetailsErrorResponse
import ae.network.nicardmanagementsdk.api.interfaces.NICardManagementAPI
import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.core.ChangePinCoreComponent
import ae.network.nicardmanagementsdk.core.GetCardDetailsCoreComponent
import ae.network.nicardmanagementsdk.core.SetPinCoreComponent

object NICardManagement : NICardManagementAPI {

    override suspend fun getCardDetails(
        input: NIInput
    ): DetailsErrorResponse {
        return GetCardDetailsCoreComponent.fromFactory(input).run {
            makeNetworkRequest()
        }
    }

    override suspend fun setPin(
        pin: String,
        input: NIInput
    ): SuccessErrorResponse {
        return SetPinCoreComponent.fromFactory(input).run {
            makeNetworkRequest(
                pin
            )
        }
    }

    override suspend fun changePin(
        oldPin: String,
        newPin: String,
        input: NIInput
    ): SuccessErrorResponse {
        return ChangePinCoreComponent.fromFactory(input).run {
            makeNetworkRequest(
                oldPin,
                newPin
            )
        }
    }

}