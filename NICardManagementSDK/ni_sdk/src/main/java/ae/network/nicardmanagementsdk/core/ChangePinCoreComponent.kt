package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.implementation.ChangePinUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.IChangePinUseCases
import ae.network.nicardmanagementsdk.network.Network
import ae.network.nicardmanagementsdk.repository.implementation.ChangePinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChangePinCoreComponent(
    private val changePinUseCase: IChangePinUseCases,
    private val input: NIInput
) : IChangePinCore {

    override suspend fun makeNetworkRequest(
        oldPin: String,
        newPin: String
    ): SuccessErrorResponse {

        val response = withContext(Dispatchers.IO) {
            val e: Exception?
            try {
                val response = changePinUseCase.changePin(input, oldPin, newPin)
                return@withContext SuccessErrorResponse(
                    response,
                    null
                )
            } catch (exception: Exception) {
                e = exception
            }

            // map the "e" exception to NISDKErrors
            return@withContext SuccessErrorResponse.fromException(e!!)
        }

        return response
    }

    companion object {
        fun fromFactory(input: NIInput): ChangePinCoreComponent {
            val network = Network(input.connectionProperties)
            val changePinRepository = ChangePinRepository(network.changePinApi)
            val changePinUseCases = ChangePinUseCases(changePinRepository)
            return ChangePinCoreComponent(changePinUseCases, input)
        }
    }
}