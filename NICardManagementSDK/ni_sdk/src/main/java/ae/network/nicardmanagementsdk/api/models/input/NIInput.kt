package ae.network.nicardmanagementsdk.api.models.input

import java.io.Serializable

data class NIInput(
    val bankCode: String,
    val cardIdentifierId: String,
    val cardIdentifierType: String,
    val connectionProperties: NIConnectionProperties,
    val displayAttributes: NIDisplayAttributes? = null
) : Serializable
