package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse

interface IViewPinRepository {
    suspend fun  viewPin(
        input: NIInput,
        publicKey: String
    ) : ViewPinResponse
}