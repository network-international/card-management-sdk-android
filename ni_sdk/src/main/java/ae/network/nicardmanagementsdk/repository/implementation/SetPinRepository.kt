package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetVerifyPinBodyDto
import ae.network.nicardmanagementsdk.network.retrofit_api.SetPinApi
import ae.network.nicardmanagementsdk.repository.interfaces.ISetVerifyPinRepository

class SetPinRepository(private val setPinApi: SetPinApi) :
    PinRepository(setPinApi), ISetVerifyPinRepository {

    override suspend fun setVerifyPin(
        token: String,
        bankCode: String,
        setVerifyPinBody: SetVerifyPinBodyDto
    ) {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        setPinApi.setPin(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", bankCode),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            setVerifyPinBody
        )
    }

}