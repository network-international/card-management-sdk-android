package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.ViewPinErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.implementation.ViewPinUseCases
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.IViewPinUseCases
import ae.network.nicardmanagementsdk.network.Network
import ae.network.nicardmanagementsdk.repository.implementation.ViewPinRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ViewPinCoreComponent(
    private val input: NIInput,
    private val viewPinUseCases: IViewPinUseCases
) : IViewPinCore {
    override suspend fun makeNetworkRequest(): ViewPinErrorResponse {
        val response = withContext(Dispatchers.IO) {
            val e: Exception?
            try {
                val pinResponse = viewPinUseCases.viewPin(input)
                return@withContext ViewPinErrorResponse(
                    pin = pinResponse,
                    error = null
                )
            } catch (exception: Exception) {
                e = exception
            }

            return@withContext ViewPinErrorResponse.fromException(e!!)
        }
        return response
    }

    companion object {
        fun fromFactory(input: NIInput): ViewPinCoreComponent {
            val network = Network(input.connectionProperties)
            val viewPinRepository = ViewPinRepository(network.viewPinApi)
            val viewPinUseCases = ViewPinUseCases(viewPinRepository)
            return ViewPinCoreComponent(input, viewPinUseCases)
        }
    }
}