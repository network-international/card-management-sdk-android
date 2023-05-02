package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierTypeEnum
import ae.network.nicardmanagementsdk.network.dto.set_pin.EncryptionMethodEnum
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetVerifyPinBodyDto
import ae.network.nicardmanagementsdk.network.retrofit_api.PinApi
import ae.network.nicardmanagementsdk.repository.interfaces.ISetVerifyPinRepository

abstract class SetVerifyPinRepository(pinApi: PinApi) : PinRepository(pinApi), ISetVerifyPinRepository {

    fun getVerifyPinBodyDto(input: NIInput, encryptedPinBlock: String): SetVerifyPinBodyDto {
        return SetVerifyPinBodyDto(
            input.cardIdentifierId,
            encryptedPinBlock,
            EncryptionMethodEnum.ASYMMETRIC_ENC.name,
            CardIdentifierTypeEnum.EXID.name
        )
    }
}