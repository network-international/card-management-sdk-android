package ae.network.nicardmanagementsdk.domain.usecases.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse

interface ISetPinUseCases {
    suspend fun setPin(input: NIInput, pin: String) : NISuccessResponse
}