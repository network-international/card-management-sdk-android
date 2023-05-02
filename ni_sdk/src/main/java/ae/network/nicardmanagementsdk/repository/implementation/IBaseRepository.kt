package ae.network.nicardmanagementsdk.repository.implementation

interface IBaseRepository {
    fun headerRetrofit(token: String, bankCode: String): Map<String, String>
}
