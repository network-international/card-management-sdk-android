package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.network.retrofit_api.SetPinApi

class SetPinRepository(private val setPinApi: SetPinApi) :
    SetVerifyPinRepository(setPinApi) {

    override suspend fun setVerifyPin(
        input: NIInput,
        encryptedPinBlock: String
    ) {
        setPinApi.setPin(
            headerRetrofit(input.connectionProperties.token, input.bankCode),
            getVerifyPinBodyDto(input, encryptedPinBlock)
        )
    }

}