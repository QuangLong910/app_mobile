package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.AuthResponse
import gtvt.ktpm.thuexe.data.model.BankAccountRequest
import gtvt.ktpm.thuexe.data.model.SubmitDocumentsResponse
import gtvt.ktpm.thuexe.data.model.LoginRequest
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.model.RegisterRequest
import gtvt.ktpm.thuexe.data.model.SubmitDocumentsRequest
import gtvt.ktpm.thuexe.data.model.VerifyOtpRequest
import gtvt.ktpm.thuexe.data.model.DeviceTokenRequest
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {

    /** Tạo tài khoản; môi trường dev in OTP ra log backend. */
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest)

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: VerifyOtpRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    /** Gắn mã thiết bị FCM với tài khoản đang đăng nhập để nhận thông báo đẩy. */
    @POST("me/device-token")
    suspend fun registerDevice(@Body body: DeviceTokenRequest)

    /** Gọi lúc đăng xuất: token phiên đã bị xoá nên truyền tay, interceptor không còn gắn được. */
    @DELETE("me/device-token")
    suspend fun unregisterDevice(@Header("Authorization") bearer: String, @Query("token") token: String)

    @GET("me")
    suspend fun me(): MeResponse

    /** Khai tài khoản nhận tiền hoàn cọc. */
    @PUT("me/bank-account")
    suspend fun updateBankAccount(@Body body: BankAccountRequest): MeResponse

    /** Nộp CCCD + GPLX, hồ sơ chuyển sang chờ duyệt. */
    @POST("me/documents")
    suspend fun submitDocuments(@Body body: SubmitDocumentsRequest): SubmitDocumentsResponse
}
