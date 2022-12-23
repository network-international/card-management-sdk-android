package ae.network.nicardmanagementsdk.domain.usecases.implementation

import ae.network.nicardmanagementsdk.BuildConfig
import ae.network.nicardmanagementsdk.api.models.input.NIInput
import ae.network.nicardmanagementsdk.api.models.output.NISuccessResponse
import ae.network.nicardmanagementsdk.core.security.CryptoManager
import ae.network.nicardmanagementsdk.core.security.SelfSignedCertificate
import ae.network.nicardmanagementsdk.domain.models.CardIdentifierModel
import ae.network.nicardmanagementsdk.domain.usecases.interfaces.IChangePinUseCases
import ae.network.nicardmanagementsdk.network.dto.change_pin.CardIdentifierBodyDto
import ae.network.nicardmanagementsdk.network.dto.change_pin.CardIdentifierTypeEnum
import ae.network.nicardmanagementsdk.network.dto.change_pin.ChangePinBodyDto
import ae.network.nicardmanagementsdk.network.dto.change_pin.EncryptionMethodEnum
import ae.network.nicardmanagementsdk.repository.interfaces.IChangePinRepository
import java.security.KeyPair
import java.security.PrivateKey

class ChangePinUseCases(private val changePinRepository: IChangePinRepository) :
    IChangePinUseCases {

    override suspend fun changePin(input: NIInput, oldPin: String, newPin: String): NISuccessResponse {

        // generate RSA KeyPair
        val keyPair = CryptoManager.generateRsaKeyPair(CryptoManager.KEY_LENGTH_BITS_4K)

        // get the object that holds the encrypted PAN
        val cardIdentifierModel = changePinRepository.getCardsLookUp(
            input.connectionProperties.token,
            CardIdentifierBodyDto(
                input.cardIdentifierType,
                input.cardIdentifierId,
                getCertificateBase64String(keyPair)
            )
        )

        // get generated x.509.Certificate from NI API Gateway
        val certificateModel = changePinRepository.getCertificateFromApiGateway(input.connectionProperties.token)

        // decrypt encrypted PAN to clear PAN
        val clearPan = decryptToClearPan(cardIdentifierModel, keyPair.private)

        // create pinBlock of the old pin
        val oldPinBlock = createPinBlock(oldPin, clearPan)

        // encrypt the pinBlock of the old pin
        val encryptedOldPinBlock = encryptPinBlock(oldPinBlock, certificateModel.certificate)

        // create pinBlock of the new pin
        val newPinBlock = createPinBlock(newPin, clearPan)

        // encrypt the pinBlock of the new pin
        val encryptedNewPinBlock = encryptPinBlock(newPinBlock, certificateModel.certificate)

        // call API to Change Pin (send encrypted pin blocks) ("encrypted_old_pin":"encrypted old pin block", "encrypted_new_pin":"encrypted new pin block")
        changePinRepository.changePin(
            input.connectionProperties.token,
            ChangePinBodyDto(
                input.cardIdentifierId,
                encryptedOldPinBlock,
                encryptedNewPinBlock,
                EncryptionMethodEnum.ASYMMETRIC_ENC.name,
                CardIdentifierTypeEnum.EXID.name
            )
        )

        return NISuccessResponse()
    }

    private fun decryptToClearPan(cardIdentifierModel: CardIdentifierModel, privateKey: PrivateKey): String {
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
