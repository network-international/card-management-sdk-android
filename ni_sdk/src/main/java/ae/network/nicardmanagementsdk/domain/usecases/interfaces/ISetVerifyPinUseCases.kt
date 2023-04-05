package ae.network.nicardmanagementsdk.domain.usecases.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse

interface ISetVerifyPinUseCases {
    suspend fun setVerifyPin(input: NIInput, pin: String) : NISuccessResponse
}