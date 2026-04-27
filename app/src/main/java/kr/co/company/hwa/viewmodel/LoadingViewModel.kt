package kr.co.company.hwa.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kr.co.company.hwa.audio.SolutionUseCase
import kr.co.company.hwa.model.ScoreResult

class LoadingViewModel(
    private val solutionUseCase: SolutionUseCase
) : ViewModel() {

    private val _scoreState = MutableStateFlow<ScoreResult?>(null)
    val scoreState: StateFlow<ScoreResult?> = _scoreState

    fun loadScore() {
        viewModelScope.launch {
            val result = solutionUseCase()
            _scoreState.value = result
        }
    }
}