package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.SetVerifyPinApi
import ae.network.nicardmanagementsdk.repository.interfaces.ISetVerifyPinRepository

abstract class SetVerifyPinBaseRepository(private val setVerifyPinApi: SetVerifyPinApi) : ISetVerifyPinRepository {

    override suspend fun getCardsLookUp(
        token: String,
        bankCode: String,
        cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return setVerifyPinApi.getCardsLookUp(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", bankCode),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            cardIdentifierBody
        ).asDomainModel()
    }

    override suspend fun getPinCertificate(token: String, bankCode: String): PinCertificateModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return setVerifyPinApi.getPinCertificate(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", bankCode),
                Pair("Channel-Id", CHANNEL_ID),
            )
        ).asDomainModel()
    }
}