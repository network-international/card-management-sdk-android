package ae.network.nicardmanagementsdk.network

import ae.network.nicardmanagementsdk.api.models.input.NIConnectionProperties
import ae.network.nicardmanagementsdk.helpers.UrlHelper
import ae.network.nicardmanagementsdk.network.retrofit_api.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class Network(connectionProperties: NIConnectionProperties) {

    private var safeBaseUrl: String
    private var logInterceptor = HttpLoggingInterceptor()
    private val okHttpClient: OkHttpClient
    init {
        safeBaseUrl = UrlHelper().baseUrlCheck(connectionProperties)
        // change logging level for debugging
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()
    }


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