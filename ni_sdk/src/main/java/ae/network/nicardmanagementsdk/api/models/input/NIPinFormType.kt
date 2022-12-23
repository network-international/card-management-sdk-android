package ae.network.nicardmanagementsdk.api.models.input

enum class NIPinFormType(val minSize: Int, val maxSize: Int) {
    DYNAMIC(4, 6),
    FOUR_DIGITS(4, 4),
    FIVE_DIGITS (5, 5),
    SIX_DIGITS (6, 6)
}