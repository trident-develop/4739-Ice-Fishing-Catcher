package kr.co.company.hwa.model

import androidx.core.net.toUri
import kr.co.company.hwa.utils.StatisticParamsResolver

class StatisticComposer(
    private val paramsResolver: StatisticParamsResolver
) {

    suspend fun compose(baseStatistic: String): String {

        val params = paramsResolver.resolveAll()

        val filtered = params.filter { !it.value.isNullOrBlank() }

        val builder = baseStatistic.toUri().buildUpon()

        filtered.forEach {
            builder.appendQueryParameter(it.key, it.value)
        }

        val finalStatistic = builder.build().toString()

//        log("Composer: final url = $finalStatistic")

        return finalStatistic
    }
}