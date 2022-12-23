package ae.network.nicardmanagementsdk.api.models.output

import retrofit2.HttpException
import java.io.IOException
import java.io.Serializable

data class NIErrorResponse(
    val error: NISDKErrors,
    val errorMessage: String = error.value
): Serializable {
    companion object {
        fun fromException(e: Exception): NIErrorResponse {
            val niSDKError =  when (e) {

                is HttpException -> {
                    NISDKErrors.NETWORK_ERROR.also {
                        val localizedMessage = e.localizedMessage as String
                        val errorBodyMessage = e.response()?.errorBody()?.string()?.let { s ->
                            "${e.localizedMessage} : $s"
                        } ?: ""
                        it.value = "$localizedMessage : $errorBodyMessage"
                    }
                }

                is IOException -> NISDKErrors.NETWORK_ERROR.also {
                    it.value = e.localizedMessage as String
                }

                else -> NISDKErrors.GENERAL_ERROR.also {
                    it.value = e.localizedMessage as String
                }
            }
            return NIErrorResponse(
                niSDKError
            )
        }
    }
}

enum class NISDKErrors(var value: String): Serializable {
    GENERAL_ERROR("SDK General Error"),
    NAV_ERROR("Form not allowed pushing on navigation controller"),

    NETWORK_ERROR("Network Error"),
    PARSING_ERROR("SDK Parsing Error"),

    RSAKEY_ERROR("Couldn't  get or generate Public Key"),
    PINBLOCK_ERROR("PIN Block Error"),
    PINBLOCK_ENCRYPTION_ERROR("PIN Block Encryption Error")
}