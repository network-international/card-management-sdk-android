package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.ViewPinResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.IViewPinUseCases
import ae.network.nicardmanagementsdk.repository.interfaces.IViewPinRepository
import java.security.KeyPair

class ViewPinUseCases(
    private val viewPinRepository: IViewPinRepository
) : BaseUseCases(), IViewPinUseCases {

    override suspend fun viewPin(input: NIInput): ViewPinResponse {
        val keyPair = CryptoManager.generateRsaKeyPair(CryptoManager.KEY_LENGTH_BITS_4K)
        val publicKey = getCertificateBase64String(keyPair)
        val viewPinResponse = viewPinRepository.viewPin(input, publicKey)

        return viewPinResponse(viewPinResponse, keyPair)
    }

    private fun viewPinResponse(response: ViewPinResponse, keyPair: KeyPair): ViewPinResponse {
        val decryptedPin = CryptoManager.rsaDecryptData(
            data = response.encrypted_pin,
            privateKey = keyPair.private,
            blockLength = CryptoManager.HEX_CHUNK_BLOCK_LENGTH_2
        )
        return ViewPinResponse(decryptedPin)
    }

}