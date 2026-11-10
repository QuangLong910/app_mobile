package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.AuthApi
import gtvt.ktpm.thuexe.data.model.AuthResponse
import gtvt.ktpm.thuexe.data.model.SubmitDocumentsResponse
import gtvt.ktpm.thuexe.data.model.BankAccountRequest
import gtvt.ktpm.thuexe.data.model.LoginRequest
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.model.RegisterRequest
import gtvt.ktpm.thuexe.data.model.SubmitDocumentsRequest
import gtvt.ktpm.thuexe.data.model.VerifyOtpRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.TokenStore
import gtvt.ktpm.thuexe.data.net.apiCall
import java.time.LocalDate
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenStore: TokenStore,
    private val gson: Gson,
    private val push: gtvt.ktpm.thuexe.data.push.PushRegistrar,
) {
    val isLoggedIn: Boolean get() = !tokenStore.token.isNullOrBlank()

    /** Chế độ đang dùng: chủ xe hay khách thuê. Đăng xuất thì xoá cùng token. */
    var cheDoChuXe: Boolean
        get() = tokenStore.cheDoChuXe
        set(value) { tokenStore.cheDoChuXe = value }

    suspend fun register(
        phone: String, password: String, fullName: String,
        isOwner: Boolean = false, email: String? = null,
    ): ApiResult<Unit> =
        apiCall(gson) { api.register(RegisterRequest(phone, password, fullName, email, isOwner)) }

    suspend fun verifyOtp(phone: String, otp: String): ApiResult<AuthResponse> =
        apiCall(gson) { api.verifyOtp(VerifyOtpRequest(phone, otp)) }.also(::keepToken).also { dangKyPush(it) }

    suspend fun login(phone: String, password: String): ApiResult<AuthResponse> =
        apiCall(gson) { api.login(LoginRequest(phone, password)) }.also(::keepToken).also { dangKyPush(it) }

    suspend fun me(): ApiResult<MeResponse> = apiCall(gson) { api.me() }

    suspend fun capNhatTaiKhoan(soTaiKhoan: String, nganHang: String): ApiResult<MeResponse> =
        apiCall(gson) { api.updateBankAccount(BankAccountRequest(soTaiKhoan.trim(), nganHang.trim())) }

    suspend fun submitDocuments(
        cccdNo: String?,
        gplxNo: String?,
        gplxClass: String?,
        gplxExpiry: LocalDate?,
        frontUrl: String,
        backUrl: String,
        selfieUrl: String? = null,
    ): ApiResult<SubmitDocumentsResponse> = apiCall(gson) {
        api.submitDocuments(
            SubmitDocumentsRequest(cccdNo, gplxNo, gplxClass, gplxExpiry, frontUrl, backUrl, selfieUrl)
        )
    }

    /** Thông báo đẩy là phần phụ: đăng ký lỗi cũng không được làm đăng nhập thất bại. */
    private suspend fun dangKyPush(result: ApiResult<AuthResponse>) {
        if (result is ApiResult.Ok) runCatching { push.dangKy() }
    }

    fun logout() {
        val phien = tokenStore.token
        tokenStore.clear()
        // Máy này thôi nhận thông báo của tài khoản vừa đăng xuất. Chạy nền, không chờ.
        if (!phien.isNullOrBlank()) kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO)
            .launch { runCatching { push.huy(phien) } }
    }

    private fun keepToken(result: ApiResult<AuthResponse>) {
        if (result is ApiResult.Ok) {
            tokenStore.token = result.data.token
            tokenStore.userId = result.data.profile.id
            // Không còn nút chuyển vai: tài khoản chủ xe vào thẳng phần chủ xe, còn lại là phần thuê xe.
            tokenStore.cheDoChuXe = result.data.profile.isOwner
        }
    }
}
