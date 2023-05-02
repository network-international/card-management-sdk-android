package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierTypeEnum
import ae.network.nicardmanagementsdk.network.dto.set_pin.EncryptionMethodEnum
import ae.network.nicardmanagementsdk.network.retrofit_api.ChangePinApi
import ae.network.nicardmanagementsdk.repository.interfaces.IChangePinRepository

class ChangePinRepository(private val changePinApi: ChangePinApi) :
    PinRepository(changePinApi), IChangePinRepository {

    override suspend fun changePin(
        input: NIInput,
        encryptedOldPinBlock: String,
        encryptedNewPinBlock: String
    ) {
        changePinApi.changePin(
            headerRetrofit(input.connectionProperties.token, input.bankCode),
            ChangePinBodyDto(
                input.cardIdentifierId,
                encryptedOldPinBlock,
                encryptedNewPinBlock,
                EncryptionMethodEnum.ASYMMETRIC_ENC.name,
                CardIdentifierTypeEnum.EXID.name
            )
        )
    }

}