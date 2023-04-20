package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput

interface ISetVerifyPinRepository : IPinRepository {

    suspend fun setVerifyPin(
        input: NIInput,
        encryptedPinBlock: String
    )

}