package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.BuildConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.core.security.SelfSignedCertificate
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.ISetPinUseCases
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.set_pin.CardIdentifierTypeEnum
import ae.network.nicardmanagementsdk.network.dto.set_pin.EncryptionMethodEnum
import ae.network.nicardmanagementsdk.network.dto.set_pin.SetPinBodyDto
import ae.network.nicardmanagementsdk.repository.interfaces.ISetPinRepository
import java.security.KeyPair
import java.security.PrivateKey

class SetPinUseCases(private val setPinRepository: ISetPinRepository) : ISetPinUseCases {

    override suspend fun setPin(input: NIInput, pin: String): NISuccessResponse {

        //generate the KeyPair
        val keyPair = CryptoManager.generateRsaKeyPair(CryptoManager.KEY_LENGTH_BITS_4K)

        //get the object that holds the encrypted PAN
        val cardIdentifierModel = setPinRepository.getCardsLookUp(
            input.connectionProperties.token,
            CardIdentifierBodyDto(
                input.cardIdentifierType,
                input.cardIdentifierId,
                getCertificateBase64String(keyPair)
            )
        )

        // get their generated x.509.Certificate
        val certificateModel = setPinRepository.getPinCertificate(input.connectionProperties.token)

        // decrypt encrypted PAN to clear PAN
        val clearPan = decryptToClearPan(cardIdentifierModel, keyPair.private)

        //create pinBlock
        val pinBlock = createPinBlock(pin, clearPan)

        //encrypt the pinBlock
        val encryptedPinBlock = encryptPinBlock(pinBlock, certificateModel.certificate)

        //call API to Set Pin (send encrypted pin block) ("encrypted_pin":"encrypted pin block")
        setPinRepository.setPin(
            input.connectionProperties.token,
            SetPinBodyDto(
                input.cardIdentifierId,
                encryptedPinBlock,
                EncryptionMethodEnum.ASYMMETRIC_ENC.name,
                CardIdentifierTypeEnum.EXID.name
            )
        )

        return NISuccessResponse()
    }

    private fun decryptToClearPan(
        cardIdentifierModel: CardIdentifierModel,
        privateKey: PrivateKey
    ): String {
        return CryptoManager.rsaDecryptData(
            cardIdentifierModel.cardIdentifierId,
            privateKey,
            CryptoManager.HEX_CHUNK_BLOCK_LENGTH_4
        )
    }

    private fun encryptPinBlock(pinBlock: String, certificate: String): String {
        val certificateObject = CryptoManager.generateCertificateObject(certificate)
        val publicKey = certificateObject?.publicKey
        return CryptoManager.rsaEncryptData(pinBlock, publicKey)
    }

    private fun createPinBlock(pin: String, clearPan: String): String {
        return CryptoManager.format0Encode(pin, clearPan)
    }

    private fun getCertificateBase64String(keyPair: KeyPair): String {
        val certificate = SelfSignedCertificate(
            fqdn = BuildConfig.LIBRARY_PACKAGE_NAME,
            keyPair = keyPair
        )
        return certificate.certificateBase64String
    }

}
