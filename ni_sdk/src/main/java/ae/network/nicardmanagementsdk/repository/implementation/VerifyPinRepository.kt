package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetVerifyPinBodyDto
import ae.network.nicardmanagementsdk.network.retrofit_api.VerifyPinApi

class VerifyPinRepository(private val verifyPinApi: VerifyPinApi) : SetVerifyPinBaseRepository(verifyPinApi) {

    override suspend fun setVerifyPin(token: String, bankCode: String, setVerifyPinBody: SetVerifyPinBodyDto) {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        verifyPinApi.verifyPin(
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