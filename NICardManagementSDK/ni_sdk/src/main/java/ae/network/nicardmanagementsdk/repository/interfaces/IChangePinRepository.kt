package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.change_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto

interface IChangePinRepository {

    suspend fun getCardsLookUp(
        authToken: String,
        cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierModel

    suspend fun getCertificateFromApiGateway(
        authToken: String
    ): PinCertificateModel

    suspend fun changePin(
        authToken: String,
        changePinBody: ChangePinBodyDto
    )

}