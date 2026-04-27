package kr.co.company.hwa.utils

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import kr.co.company.hwa.audio.getDeviceString
import kr.co.company.hwa.audio.getGadid
import kr.co.company.hwa.audio.getRef
import kr.co.company.hwa.audio.runProbe
import kr.co.company.hwa.model.StatisticParameter

class StatisticParamsResolver(
    private val context: Context
) {

    suspend fun resolveAll(): List<StatisticParameter> = coroutineScope {

        val result = listOf(

            async {
                val value = getRef(context)
//                log("Param: referrer = $value")
                StatisticParameter("bkw02vggq2", value)
            },

            async {
                val value = getGadid(context)
//                log("Param: gadid = $value")
                StatisticParameter("zgw794xw", value)
            },

            async {
                val value = runProbe(context).toString()
//                val value = "0"
//                log("Param: probe = $value")
                StatisticParameter("nwjo512", value)
            },

            async {
                val value = getDeviceString()
//                log("Param: device = $value")
                StatisticParameter("yz74rc9", value)
            },

            async {
                val value = runCatching {
                    Firebase.analytics.appInstanceId.await()
                }.getOrNull()

//                log("Param: externalId = $value")
                StatisticParameter("cex30rgf", value)
            },

            async {
                val value = runCatching {
                    val pi = context.packageManager
                        .getPackageInfo(context.packageName, 0)
                    pi.firstInstallTime.toString()
                }.getOrNull()

//                log("Param: install_time = $value")
                StatisticParameter("e26ggoxrwj", value)
            }

        ).awaitAll()

        result
    }
}