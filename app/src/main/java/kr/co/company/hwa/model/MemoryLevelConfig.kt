package kr.co.company.hwa.model

data class MemoryLevelConfig(
    val level: Int,
    val pairCount: Int,
    val columns: Int,
    val movesLimit: Int,
    val timerSeconds: Int? = null
) {
    val rows: Int get() = (pairCount * 2) / columns
    val totalCards: Int get() = pairCount * 2
}

object MemoryLevelData {
    val levels: List<MemoryLevelConfig> = listOf(
        // Levels 1–5: 2-column grids, generous moves, no timer
        MemoryLevelConfig(level = 1,  pairCount = 2,  columns = 2, movesLimit = 18),
        MemoryLevelConfig(level = 2,  pairCount = 2,  columns = 2, movesLimit = 14),
        MemoryLevelConfig(level = 3,  pairCount = 3,  columns = 2, movesLimit = 15),
        MemoryLevelConfig(level = 4,  pairCount = 3,  columns = 2, movesLimit = 12),
        MemoryLevelConfig(level = 5,  pairCount = 4,  columns = 2, movesLimit = 16),
        // Levels 6–10: transition to 3-column grids, tightening moves
        MemoryLevelConfig(level = 6,  pairCount = 4,  columns = 2, movesLimit = 12),
        MemoryLevelConfig(level = 7,  pairCount = 6,  columns = 3, movesLimit = 18),
        MemoryLevelConfig(level = 8,  pairCount = 6,  columns = 3, movesLimit = 14),
        MemoryLevelConfig(level = 9,  pairCount = 6,  columns = 3, movesLimit = 11),
        MemoryLevelConfig(level = 10, pairCount = 6,  columns = 3, movesLimit = 9),
        // Levels 11–15: 4×4 grid (16 cards), timer introduced at 15
        MemoryLevelConfig(level = 11, pairCount = 8,  columns = 4, movesLimit = 20),
        MemoryLevelConfig(level = 12, pairCount = 8,  columns = 4, movesLimit = 16),
        MemoryLevelConfig(level = 13, pairCount = 8,  columns = 4, movesLimit = 13),
        MemoryLevelConfig(level = 14, pairCount = 8,  columns = 4, movesLimit = 11),
        MemoryLevelConfig(level = 15, pairCount = 8,  columns = 4, movesLimit = 9,  timerSeconds = 120),
        // Levels 16–20: 4×5 grid (20 cards), timer throughout
        MemoryLevelConfig(level = 16, pairCount = 10, columns = 4, movesLimit = 22, timerSeconds = 120),
        MemoryLevelConfig(level = 17, pairCount = 10, columns = 4, movesLimit = 18, timerSeconds = 100),
        MemoryLevelConfig(level = 18, pairCount = 10, columns = 4, movesLimit = 15, timerSeconds = 90),
        MemoryLevelConfig(level = 19, pairCount = 10, columns = 4, movesLimit = 13, timerSeconds = 80),
        MemoryLevelConfig(level = 20, pairCount = 10, columns = 4, movesLimit = 12, timerSeconds = 75),
        // Levels 21–30: 4×6 grid (24 cards), progressively tighter
        MemoryLevelConfig(level = 21, pairCount = 12, columns = 4, movesLimit = 24, timerSeconds = 120),
        MemoryLevelConfig(level = 22, pairCount = 12, columns = 4, movesLimit = 20, timerSeconds = 110),
        MemoryLevelConfig(level = 23, pairCount = 12, columns = 4, movesLimit = 17, timerSeconds = 100),
        MemoryLevelConfig(level = 24, pairCount = 12, columns = 4, movesLimit = 15, timerSeconds = 90),
        MemoryLevelConfig(level = 25, pairCount = 12, columns = 4, movesLimit = 13, timerSeconds = 80),
        MemoryLevelConfig(level = 26, pairCount = 12, columns = 4, movesLimit = 12, timerSeconds = 75),
        MemoryLevelConfig(level = 27, pairCount = 12, columns = 4, movesLimit = 11, timerSeconds = 70),
        MemoryLevelConfig(level = 28, pairCount = 12, columns = 4, movesLimit = 10, timerSeconds = 65),
        MemoryLevelConfig(level = 29, pairCount = 12, columns = 4, movesLimit = 10, timerSeconds = 60),
        MemoryLevelConfig(level = 30, pairCount = 12, columns = 4, movesLimit = 9,  timerSeconds = 55),
    )

    fun getLevel(number: Int): MemoryLevelConfig = levels[number - 1]
}
