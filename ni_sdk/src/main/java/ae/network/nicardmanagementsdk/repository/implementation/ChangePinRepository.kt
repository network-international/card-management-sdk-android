package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto
import ae.network.nicardmanagementsdk.network.retrofit_api.ChangePinApi
import ae.network.nicardmanagementsdk.repository.interfaces.IChangePinRepository

class ChangePinRepository(private val changePinApi: ChangePinApi) :
    PinRepository(changePinApi), IChangePinRepository {

    override suspend fun changePin(
        token: String,
        bankCode: String,
        changePinBody: ChangePinBodyDto
    ) {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        changePinApi.changePin(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", bankCode),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            changePinBody
        )
    }

}