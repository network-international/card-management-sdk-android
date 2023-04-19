package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto

interface IChangePinRepository : IPinRepository {

    suspend fun changePin(
        token: String,
        bankCode: String,
        changePinBody: ChangePinBodyDto
    )

}