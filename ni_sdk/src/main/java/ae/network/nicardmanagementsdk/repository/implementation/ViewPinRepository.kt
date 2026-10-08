package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse
import ae.network.nicardmanagementsdk.network.dto.view_pin.PINViewBodyDto
import ae.network.nicardmanagementsdk.network.dto.view_pin.toModel
import ae.network.nicardmanagementsdk.network.retrofit_api.ViewPinApi
import ae.network.nicardmanagementsdk.repository.interfaces.IViewPinRepository

class ViewPinRepository(
    private val viewPinApi: ViewPinApi,
) : BaseRepository(), IViewPinRepository {

    override suspend fun viewPin(
        input: NIInput,
        publicKey: String
    ): ViewPinResponse =
        viewPinApi.viewPin(
            headers = headerRetrofit(input.connectionProperties.token, input.bankCode),
            pinViewBodyDto = PINViewBodyDto(
                cardIdentifierId = input.cardIdentifierId,
                cardIdentifierType = input.cardIdentifierType,
                publicKey = publicKey
            )
        ).toModel()
}