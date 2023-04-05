package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.models.PinCertificateModel
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetVerifyPinBodyDto

interface ISetVerifyPinRepository {

    suspend fun getCardsLookUp(
        token: String,
        bankCode: String,
        cardIdentifierBody: CardIdentifierBodyDto
    ): CardIdentifierModel

    suspend fun getPinCertificate(
        token: String,
        bankCode: String
    ): PinCertificateModel

    suspend fun setVerifyPin(
        token: String,
        bankCode: String,
        setVerifyPinBody: SetVerifyPinBodyDto
    )

}