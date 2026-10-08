package ae.network.nicardmanagementsdk.domain.usecases.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse

interface IViewPinUseCases {
    suspend fun viewPin(input: NIInput): ViewPinResponse
}