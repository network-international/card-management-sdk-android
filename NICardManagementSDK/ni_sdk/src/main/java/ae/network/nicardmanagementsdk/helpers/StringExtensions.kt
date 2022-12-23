package ae.network.nicardmanagementsdk.helpers

import java.lang.StringBuilder

fun String.toStarMaskedString(range: Int): String {
    val regex ="\\s+".toRegex()
    val names = this.replace(regex, " ").trim().split(" ")
    val output = mutableListOf<String>()
    names.forEach { name ->
        val newString = when (name.length) {
            in 1..range -> name
            else -> {
                name.take(range).padEnd(name.length, '*')
            }
        }
        output.add(newString)
    }
    return output.joinToString(separator = " ")
}

fun String.toSpacedPAN(range: Int = 1): String {
    val spaces = List(range){" "}.joinToString(separator = "")
    val builder = StringBuilder()
    builder.append(this.substring(0..3))
    builder.append(spaces)
    builder.append(this.substring(4..7))
    builder.append(spaces)
    builder.append(this.substring(8..11))
    builder.append(spaces)
    builder.append(this.substring(12..15))
    return builder.toString()
}