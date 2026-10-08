package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto

interface IPinRepository {

    suspend fun getCardsLookUp(
        input: NIInput,
        publicKey: String
    ): CardIdentifierModel

    suspend fun getPinCertificate(
        input: NIInput
    ): PinCertificateModel
}