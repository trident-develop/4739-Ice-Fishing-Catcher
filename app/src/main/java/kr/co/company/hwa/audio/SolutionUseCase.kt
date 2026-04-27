package kr.co.company.hwa.audio

import kr.co.company.hwa.model.ScoreResult
import kr.co.company.hwa.model.ScoreSource
import kr.co.company.hwa.model.StatisticComposer
import kr.co.company.hwa.storage.ScoreStorage

class SolutionUseCase(
    private val storage: ScoreStorage,
    private val statisticComposer: StatisticComposer,
    private val baseStatistic: String
) {

    suspend operator fun invoke(): ScoreResult {
        val score = storage.getSavedScore()

        if (!score.isNullOrBlank()) {
            //            log("UseCase: link from DB = $score")
            return ScoreResult(
                score = score,
                source = ScoreSource.DATABASE
            )
        }

        val finalStatistic = statisticComposer.compose(baseStatistic)

        //        log("UseCase: final link built = $finalStatistic")

        return ScoreResult(
            score = finalStatistic,
            source = ScoreSource.BUILT
        )
    }
}