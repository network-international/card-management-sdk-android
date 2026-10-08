package ae.network.nicardmanagementsdk.repository.implementation

import ae.network.nicardmanagementsdk.core.security.CryptoManager

abstract class BaseRepository : IBaseRepository {

    override fun headerRetrofit(token: String, bankCode: String): Map<String, String> {
        val uniqueReferenceCode = CryptoManager.uniqueReferenceCodeRandom()
        return mapOf(
            Pair("Authorization", "Bearer $token"),
            Pair("Content-Type", "application/json"),
            Pair("Accept", "application/json"),
            Pair("Unique-Reference-Code", uniqueReferenceCode),
            Pair("Financial-Id", bankCode),
            Pair("Channel-Id", CHANNEL_ID),
            )
    }
}