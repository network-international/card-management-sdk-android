package ae.network.nicardmanagementsdk.network

import ae.network.nicardmanagementsdk.api.models.input.NIConnectionProperties
import ae.network.nicardmanagementsdk.helpers.UrlHelper
import ae.network.nicardmanagementsdk.network.retrofit_api.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network(connectionProperties: NIConnectionProperties) {

    private var safeBaseUrl: String

    init {
        safeBaseUrl = UrlHelper().baseUrlCheck(connectionProperties)
    }

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(safeBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val cardDetailsApi: CardDetailsApi by lazy { retrofit.create(CardDetailsApi::class.java) }
    val setPinApi: SetPinApi by lazy { retrofit.create(SetPinApi::class.java) }
    val verifyPinApi: VerifyPinApi by lazy { retrofit.create(VerifyPinApi::class.java) }
    val changePinApi: ChangePinApi by lazy { retrofit.create(ChangePinApi::class.java) }
    val viewPinApi: ViewPinApi by lazy { retrofit.create(ViewPinApi::class.java) }

    companion object {
        private const val TAG = "NetworkRepository"
    }
}