package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SessionState(
    val loading: Boolean = false,
    val loggedIn: Boolean = false,
    val me: MeResponse? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState(loggedIn = auth.isLoggedIn))
    val state: StateFlow<SessionState> = _state.asStateFlow()

    fun register(phone: String, password: String, fullName: String, isOwner: Boolean = false, email: String? = null) =
        run { auth.register(phone, password, fullName, isOwner, email) }

    fun verifyOtp(phone: String, otp: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = auth.verifyOtp(phone, otp)) {
            is ApiResult.Ok -> { _state.update { it.copy(loading = false, loggedIn = true) }; me() }
            is ApiResult.Err -> fail(r)
        }
    }

    fun login(phone: String, password: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = auth.login(phone, password)) {
            is ApiResult.Ok -> { _state.update { it.copy(loading = false, loggedIn = true) }; me() }
            is ApiResult.Err -> fail(r)
        }
    }

    fun me() = viewModelScope.launch {
        when (val r = auth.me()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, me = r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    /** Mở app vào đúng chế độ lần trước: mặc định là khách thuê. */
    val cheDoChuXe: Boolean get() = auth.cheDoChuXe

    fun logout() {
        auth.logout()
        _state.value = SessionState(loggedIn = false)
    }

    private fun run(block: suspend () -> ApiResult<*>) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = block()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false) }
            is ApiResult.Err -> fail(r)
        }
    }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
