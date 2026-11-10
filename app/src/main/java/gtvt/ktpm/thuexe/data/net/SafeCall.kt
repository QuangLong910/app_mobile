package gtvt.ktpm.thuexe.data.net

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.io.IOException

private data class ErrorBody(val code: String?, val message: String?)

/**
 * Gọi một điểm cuối và quy mọi thất bại về ApiResult.Err có mã.
 * Lỗi nghiệp vụ giữ nguyên code backend gửi; lỗi mạng thành NETWORK.
 */
suspend fun <T> apiCall(gson: Gson, block: suspend () -> T): ApiResult<T> = try {
    ApiResult.Ok(block())
} catch (e: LoiNghiepVu) {
    // Backend mới trả HTTP 200 cho cả lỗi; mã thật do BocConverterFactory moi ra.
    if (e.code == ErrorCode.UNAUTHORIZED) PhienDangNhap.baoHetHan()
    ApiResult.Err(code = e.code, message = e.thongDiep, httpStatus = e.httpStatus)
} catch (e: HttpException) {
    val raw = runCatching { e.response()?.errorBody()?.string() }.getOrNull()
    val parsed = raw?.let {
        try {
            gson.fromJson(it, ErrorBody::class.java)
        } catch (_: JsonSyntaxException) {
            null
        }
    }
    if (e.code() == 401 || parsed?.code == ErrorCode.UNAUTHORIZED) PhienDangNhap.baoHetHan()
    val fallback = if (e.code() == 401 || e.code() == 403) ErrorCode.UNAUTHORIZED else ErrorCode.UNKNOWN
    ApiResult.Err(
        code = parsed?.code ?: fallback,
        message = parsed?.message ?: e.message(),
        httpStatus = e.code(),
    )
} catch (e: IOException) {
    ApiResult.Err(ErrorCode.NETWORK, e.message)
}
