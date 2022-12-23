package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetPinBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.SetPinApi
import ae.network.nicardmanagementsdk.repository.interfaces.ISetPinRepository

class SetPinRepository(private val setPinApi: SetPinApi) : ISetPinRepository {

    override suspend fun getCardsLookUp(
        token: String,
        cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return setPinApi.getCardsLookUp(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            cardIdentifierBody
        ).asDomainModel()
    }

    override suspend fun getPinCertificate(token: String): PinCertificateModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return setPinApi.getPinCertificate(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            )
        ).asDomainModel()
    }

    override suspend fun setPin(token: String, setPinBody: SetPinBodyDto) {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        setPinApi.setPin(
            mapOf(
                Pair("Authorization", "Bearer $token"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            setPinBody
        )
    }

}