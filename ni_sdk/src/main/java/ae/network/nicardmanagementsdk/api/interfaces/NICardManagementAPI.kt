package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NICancelledResponse
import ae.network.nicardmanagementsdk.api.models.output.NIErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse
import java.lang.Exception

interface NICardManagementAPI {

    suspend fun getCardDetails(
        input: NIInput
    ): DetailsErrorResponse

    suspend fun setPin(
        pin: String,
        input: NIInput
    ): SuccessErrorResponse

    suspend fun verifyPin(
        pin: String,
        input: NIInput
    ): SuccessErrorResponse

    suspend fun changePin(
        oldPin: String,
        newPin: String,
        input: NIInput
    ): SuccessErrorResponse

    suspend fun viewPin(
        input: NIInput,
    ) : ViewPinErrorResponse
}

data class SuccessErrorCancelResponse(
    val isSuccess: NISuccessResponse?,
    val isError: NIErrorResponse?,
    val isCancelled: NICancelledResponse?
)

data class SuccessErrorResponse(
    val isSuccess: NISuccessResponse?,
    val isError: NIErrorResponse?
) {
    companion object {
        fun fromException(e: Exception) : SuccessErrorResponse {
            return SuccessErrorResponse(
                null,
                NIErrorResponse.fromException(e)
            )
        }
    }
}

data class DetailsErrorResponse(
    val details: NICardDetailsResponse?,
    val isError: NIErrorResponse?
) {
    companion object {
        fun fromException(e: Exception) : DetailsErrorResponse {
            return DetailsErrorResponse(
                null,
                NIErrorResponse.fromException(e)
            )
        }
    }
}

fun DetailsErrorResponse.asSuccessErrorResponse(): SuccessErrorResponse {
    return SuccessErrorResponse(
        details?.let {
           NISuccessResponse()
        },
        isError
    )
}

data class ViewPinErrorResponse(
    val pin: ViewPinResponse?,
    val error: NIErrorResponse?
) {
    companion object {
        fun fromException(e: Exception): ViewPinErrorResponse {
            return ViewPinErrorResponse(
                null,
                NIErrorResponse.fromException(e)
            )
        }
    }
}

fun ViewPinErrorResponse.asSuccessErrorResponse(): SuccessErrorResponse {
    return SuccessErrorResponse(
        isSuccess = pin?.let {
            NISuccessResponse()
        },
        isError = error
    )
}

fun SuccessErrorResponse.asSuccessErrorCancelResponse(): SuccessErrorCancelResponse {
    return SuccessErrorCancelResponse(
        isSuccess,
        isError,
        null
    )
}