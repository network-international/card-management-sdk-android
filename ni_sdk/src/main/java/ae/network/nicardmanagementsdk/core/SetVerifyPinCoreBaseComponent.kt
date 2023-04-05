package ae.network.nicardmanagementsdk.core

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ISetVerifyPinUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class SetVerifyPinCoreBaseComponent(
    private val setVerifyPinUseCase: ISetVerifyPinUseCases,
    private val input: NIInput
) : ISetVerifyPinCore {

    override suspend fun makeNetworkRequest(pin: String): SuccessErrorResponse {

        val response = withContext(Dispatchers.IO) {
            val e: Exception?
            try {
                val response = setVerifyPinUseCase.setVerifyPin(input, pin)
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
}