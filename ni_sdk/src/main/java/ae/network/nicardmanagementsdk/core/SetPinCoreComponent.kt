package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.implementation.SetPinUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ISetPinUseCases
import ae.network.nicardmanagementsdk.network.Network
import ae.network.nicardmanagementsdk.repository.implementation.SetPinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SetPinCoreComponent(
    private val setPinUseCase: ISetPinUseCases,
    private val input: NIInput
) : ISetPinCore {

    override suspend fun makeNetworkRequest(pin: String): SuccessErrorResponse {

        val response = withContext(Dispatchers.IO) {
            val e: Exception?
            try {
                val response = setPinUseCase.setPin(input, pin)
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
        fun fromFactory(input: NIInput): SetPinCoreComponent {
            val network = Network(input.connectionProperties)
            val setPinRepository = SetPinRepository(network.setPinApi)
            val setPinUseCases = SetPinUseCases(setPinRepository)
            return SetPinCoreComponent(setPinUseCases, input)
        }
    }
}