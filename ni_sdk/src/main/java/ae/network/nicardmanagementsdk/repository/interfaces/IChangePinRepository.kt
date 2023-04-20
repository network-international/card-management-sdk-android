package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.api.models.input.NIInput

interface IChangePinRepository : IPinRepository {

    suspend fun changePin(
        input: NIInput,
        encryptedOldPinBlock: String,
        encryptedNewPinBlock: String
    )

}