package ae.network.nicardmanagementsdk.api.interfaces

import ae.network.nicardmanagementsdk.api.models.output.NICancelledResponse
import ae.network.nicardmanagementsdk.api.models.output.NIErrorResponse
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse

data class SuccessErrorCancelResponse(
    val isSuccess: NISuccessResponse?,
    val isError: NIErrorResponse?,
    val isCancelled: NICancelledResponse?,
)

fun SuccessErrorResponse.asSuccessErrorCancelResponse(): SuccessErrorCancelResponse {
    return SuccessErrorCancelResponse(
        isSuccess,
        isError,
        null,
    )
}
