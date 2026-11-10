package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.AgreementInfo
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import gtvt.ktpm.thuexe.data.repo.OwnerCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val loading: Boolean = false,
    val me: MeResponse? = null,
    val agreement: AgreementInfo? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val cars: OwnerCarRepository,
    private val bookings: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }

        when (val r = auth.me()) {
            is ApiResult.Ok -> _state.update { it.copy(me = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        when (val r = cars.agreement()) {
            is ApiResult.Ok -> _state.update { it.copy(agreement = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        _state.update { it.copy(loading = false) }
    }

    fun logout() = auth.logout()


    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
