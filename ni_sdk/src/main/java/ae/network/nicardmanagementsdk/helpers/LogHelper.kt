package ae.network.nicardmanagementsdk.helpers

import android.util.Log

object LogHelper {

    fun logError(tag: String?, e: java.lang.Exception) {
        Log.d(tag, "Exception : ${e.javaClass.simpleName}, message: ${e.message}")
    }
}