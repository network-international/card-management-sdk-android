package ae.network.nicardmanagementsdk.repository.interfaces

import ae.network.nicardmanagementsdk.network.dto.set_pin.SetVerifyPinBodyDto

interface ISetVerifyPinRepository : IPinRepository {

    suspend fun setVerifyPin(
        token: String,
        bankCode: String,
        setVerifyPinBody: SetVerifyPinBodyDto
    )

}