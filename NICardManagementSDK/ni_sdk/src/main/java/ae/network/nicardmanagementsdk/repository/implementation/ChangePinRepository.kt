package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.change_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto
import ae.network.nicardmanagementsdk.network.dto.change_pin.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.ChangePinApi
import ae.network.nicardmanagementsdk.repository.interfaces.IChangePinRepository

class ChangePinRepository(private val changePinApi: ChangePinApi) : IChangePinRepository {
    override suspend fun getCardsLookUp(
        authToken: String,
        cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return changePinApi.getCardsLookUp(
            mapOf(
                Pair("Authorization", "Bearer $authToken"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            cardIdentifierBody
        ).asDomainModel()
    }

    override suspend fun getCertificateFromApiGateway(authToken: String): PinCertificateModel {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return changePinApi.getPinCertificate(
            mapOf(
                Pair("Authorization", "Bearer $authToken"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            )
        ).asDomainModel()
    }

    override suspend fun changePin(authToken: String, changePinBody: ChangePinBodyDto) {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        changePinApi.changePin(
            mapOf(
                Pair("Authorization", "Bearer $authToken"),
                Pair("Content-Type", "application/json"),
                Pair("Accept", "application/json"),
                Pair("Unique-Reference-Code", uniqueReferenceCode),
                Pair("Financial-Id", FINANCIAL_ID),
                Pair("Channel-Id", CHANNEL_ID),
            ),
            changePinBody
        )
    }

}