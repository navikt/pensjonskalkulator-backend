package no.nav.pensjon.kalkulator.person

enum class Land(val code: String) {

    NORGE("NOR"),
    OTHER("OTHER");

    companion object {
        fun forCode(code: String) =
            Land.values().firstOrNull { it.code == code } ?: OTHER
    }
}
