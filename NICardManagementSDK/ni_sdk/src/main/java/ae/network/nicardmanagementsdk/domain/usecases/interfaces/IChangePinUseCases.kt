package ae.network.nicardmanagementsdk.domain.usecases.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse

interface IChangePinUseCases {
    suspend fun changePin(input: NIInput, oldPin: String, newPin: String): NISuccessResponse
}