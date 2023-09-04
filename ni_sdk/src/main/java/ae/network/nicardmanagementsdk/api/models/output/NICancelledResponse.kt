package ae.network.nicardmanagementsdk.api.models.output

import java.io.Serializable

data class NICancelledResponse(
    val cancelMessage: String = "The action was cancelled by the user"
): Serializable
