package ae.network.nicardmanagementsdk.api.models.input

import java.io.Serializable

data class NIConnectionProperties(
    val rootUrl: String,
    val token: String
) : Serializable
