package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.network.retrofit_api.VerifyPinApi

class VerifyPinRepository(private val verifyPinApi: VerifyPinApi) :
    SetVerifyPinRepository(verifyPinApi) {

    override suspend fun setVerifyPin(
        input: NIInput,
        encryptedPinBlock: String
    ) {
        verifyPinApi.verifyPin(
            headerRetrofit(input.connectionProperties.token, input.bankCode),
            getVerifyPinBodyDto(input, encryptedPinBlock)
        )
    }
}