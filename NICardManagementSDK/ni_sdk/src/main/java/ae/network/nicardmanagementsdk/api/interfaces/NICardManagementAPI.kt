package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.output.NICardDetailsResponse
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NIErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse
import java.lang.Exception

interface NICardManagementAPI {

    suspend fun getCardDetails(
        input: NIInput
    ): DetailsErrorResponse

    suspend fun setPin(
        pin: String,
        input: NIInput
    ): SuccessErrorResponse

    suspend fun changePin(
        oldPin: String,
        newPin: String,
        input: NIInput
    ): SuccessErrorResponse
}

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