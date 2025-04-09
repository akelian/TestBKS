package by.devnmisko.test.utils

fun formatPrice(priceInKopecks: Int): String {
    val rubles = priceInKopecks / 100
    val kopecks = priceInKopecks % 100
    return "$rubles руб. ${kopecks.toString().padStart(2, '0')} коп."
}

fun formatUnitName(unitType: Int, unitName: String): String {
    return if (unitType == 0) unitName else "кг"
}