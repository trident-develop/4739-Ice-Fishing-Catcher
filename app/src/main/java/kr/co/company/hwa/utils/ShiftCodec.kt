package kr.co.company.hwa.utils

object ShiftCodec {
    private const val SHIFT = 6

    fun decode(input: String): String {
        return input.map { (it.code - SHIFT).toChar() }.joinToString("")
    }


    const val WV = "}|"
    const val DM = "nzzvy@55oikloynotmigzinkx4}khyozk"
}