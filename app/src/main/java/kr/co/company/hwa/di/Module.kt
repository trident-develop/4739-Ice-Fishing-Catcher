package kr.co.company.hwa.di

import kr.co.company.hwa.audio.SolutionUseCase
import kr.co.company.hwa.model.StatisticComposer
import kr.co.company.hwa.storage.NotifyPrefs
import kr.co.company.hwa.storage.ScoreDao
import kr.co.company.hwa.storage.ScoreDbHelper
import kr.co.company.hwa.storage.ScoreStorage
import kr.co.company.hwa.utils.ShiftCodec
import kr.co.company.hwa.utils.ShiftCodec.DM
import kr.co.company.hwa.utils.StatisticParamsResolver
import kr.co.company.hwa.viewmodel.LoadingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single { ScoreDbHelper(get()) }
    single { ScoreDao(get()) }
    single { ScoreStorage(get()) }
    single { NotifyPrefs(get()) }

    single {
        StatisticParamsResolver(
            context = get()
        )
    }

    single { StatisticComposer(get()) }

    single {
        SolutionUseCase(
            storage = get(),
            statisticComposer = get(),
            baseStatistic = "${ShiftCodec.decode(DM)}/sfxccj"
        )
    }

    viewModelOf(::LoadingViewModel)
}