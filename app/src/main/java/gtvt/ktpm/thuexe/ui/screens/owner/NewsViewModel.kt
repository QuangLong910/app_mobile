package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.NewsItem
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NewsState(
    val loading: Boolean = false,
    val items: List<NewsItem> = emptyList(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val repo: NewsRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(NewsState())
    val state: StateFlow<NewsState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.news()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, items = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }
}
