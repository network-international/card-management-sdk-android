package ae.network.nicardmanagementsdk.core.security

import android.util.Base64
import java.io.ByteArrayInputStream
import java.security.*
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.crypto.Cipher
import kotlin.experimental.xor

object CryptoManager {
    /**
     * FIPS 140-2 encryption requires the key length to be 2048 bits or greater.
     * Let's use that as a sane default but allow the default to be set dynamically
     * for those that need more stringent security requirements.
     */
    private const val DEFAULT_KEY_LENGTH_BITS = 2048

    const val KEY_LENGTH_BITS_4K = 4096
    const val HEX_CHUNK_BLOCK_LENGTH_2 = 2
    const val HEX_CHUNK_BLOCK_LENGTH_4 = 4

    private val secureRandom: SecureRandom
        get() = SecureRandom()

    fun generateRsaKeyPair(keyLength: Int = DEFAULT_KEY_LENGTH_BITS): KeyPair {
        return try {
            val keyGen = KeyPairGenerator.getInstance("RSA")
            keyGen.initialize(keyLength, secureRandom)
            keyGen.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            // Should not reach here because every Android implementation must have RSA key pair generator.
            throw Error(e)
        }
    }

    fun uniqueReferenceCodeRandom(length: Int = 12): String {
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return List(length) { charPool.random() }.joinToString("")
    }

    fun rsaEncryptData(data: String, publicKey: Key?): String {
        val cipher: Cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val bytes = cipher.doFinal(data.decodeHexToBytes())
        return bytes.toHexString()
    }

    fun rsaDecryptData(data: String, privateKey: Key?, blockLength: Int): String {
        val cipher: Cipher = Cipher.getInstance("RSA/None/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        val secretMessage = data.decodeHexToBytes()
        val decodedData = cipher.doFinal(secretMessage)

        return decodedData.toHexString().decodeHexToAscii(blockLength)
    }

    /**
     * Encode pinBlock format 0 (ISO 9564)
     * @param pin pin
     * @param pan primary account number (PAN/CLN/CardNumber)
     * @return pinBlock in HEX format
     */
    fun format0Encode(pin: String, pan: String): String {
        val pinLenHead: String = pin.length.toString().padStart(2, '0') + pin
        val pinData: String = pinLenHead.padEnd(16, 'F')
        val bPin: ByteArray = pinData.decodeHexToBytes()
        val panPart = extractPanAccountNumberPart(pan)
        val panData: String = panPart.padStart(16, '0')
        val bPan: ByteArray = panData.decodeHexToBytes()
        val pinBlock = ByteArray(8)
        for (i in 0..7) pinBlock[i] = (bPin[i] xor bPan[i])

        return pinBlock.toHexString()
    }

    /**
     * @param accountNumber PAN - primary account number
     * @return extract right-most 12 digits of the primary account number (PAN)
     */
    private fun extractPanAccountNumberPart(accountNumber: String): String {
        return if (accountNumber.length > 12)
            accountNumber.substring(accountNumber.length - 13, accountNumber.length - 1)
        else
            accountNumber
    }

    /**
     * @param base64Certificate certificate in base64 string format
     * @return get the certificate object that has access to util public methods
     */
    fun generateCertificateObject(base64Certificate: String): X509Certificate? {
        val decoded = Base64.decode(base64Certificate, Base64.NO_WRAP)
        val inputStream = ByteArrayInputStream(decoded)

        return CertificateFactory.getInstance("X.509").generateCertificate(inputStream) as? X509Certificate
    }

    private fun ByteArray.toHexString(separator: CharSequence = "") =
        this.joinToString(separator) {
            String.format("%02X", it)
        }

    private fun String.decodeHexToBytes(): ByteArray {
        check(length % 2 == 0) { "Must have an even length" }

        return chunked(2)
            .map { it.toInt(16).toByte() }
            .toByteArray()
    }

    private fun String.decodeHexToAscii(blockLength: Int): String {
        require(length % blockLength == 0) { "Must be divisible with the block length" }
        return chunked(blockLength)
            .map { it.toInt(16).toByte() }
            .toByteArray()
            .toString(Charsets.US_ASCII)
    }
}