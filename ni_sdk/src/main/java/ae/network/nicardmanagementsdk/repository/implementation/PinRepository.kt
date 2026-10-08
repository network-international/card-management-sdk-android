package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.asDomainModel
import ae.network.nicardmanagementsdk.network.retrofit_api.PinApi
import ae.network.nicardmanagementsdk.repository.interfaces.IPinRepository

abstract class PinRepository(private val pinApi: PinApi) : BaseRepository(), IPinRepository {

    override suspend fun getCardsLookUp(
        input: NIInput,
        publicKey: String
    ): CardIdentifierModel {
        return pinApi.getCardsLookUp(
            headerRetrofit(input.connectionProperties.token, input.bankCode),
            CardIdentifierBodyDto(
                input.cardIdentifierType,
                input.cardIdentifierId,
                publicKey
            )
        ).asDomainModel()
    }

    override suspend fun getPinCertificate(
        input: NIInput
    ): PinCertificateModel {
        return pinApi.getPinCertificate(
            headerRetrofit(input.connectionProperties.token, input.bankCode)
        ).asDomainModel()
    }
}