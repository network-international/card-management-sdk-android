package ae.network.nicardmanagementsdk.network

import ae.network.nicardmanagementsdk.api.models.input.NIConnectionProperties
import ae.network.nicardmanagementsdk.network.retrofit_api.CardDetailsApi
import ae.network.nicardmanagementsdk.network.retrofit_api.ChangePinApi
import ae.network.nicardmanagementsdk.network.retrofit_api.SetPinApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Network(
    connectionProperties: NIConnectionProperties
) {

    companion object {
        private const val TAG = "NetworkRepository"
    }

    private val okHttpClient = OkHttpClient.Builder().build()

    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(connectionProperties.rootUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val cardDetailsApi: CardDetailsApi by lazy { retrofit.create(CardDetailsApi::class.java) }
    val setPinApi: SetPinApi by lazy { retrofit.create(SetPinApi::class.java) }
    val changePinApi: ChangePinApi by lazy { retrofit.create(ChangePinApi::class.java) }

}