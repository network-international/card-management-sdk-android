package ae.network.nicardmanagementsdk.helpers

import ae.network.nicardmanagementsdk.api.models.input.NIConnectionProperties
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.util.*

class UrlHelper {
    fun baseUrlCheck(connectionProperties: NIConnectionProperties): String {
        val rootUrl = connectionProperties.rootUrl
        val httpUrl = rootUrl.toHttpUrl()
        Objects.requireNonNull(httpUrl, "NIInput > connectionProperties > rootUrl is not a valid URL string")
        val pathSegments: List<String> = httpUrl.pathSegments
        return if ("" == pathSegments[pathSegments.lastIndex]) rootUrl else "$rootUrl/"
    }
}