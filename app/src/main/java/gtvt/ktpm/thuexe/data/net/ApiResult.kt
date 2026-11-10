package gtvt.ktpm.thuexe.data.net

/** Mã lỗi nghiệp vụ backend trả trong { "code": ..., "message": ... }. Bắt theo code, không bắt theo câu chữ. */
object ErrorCode {
    const val SLOT_TAKEN = "SLOT_TAKEN"
    const val CAR_UNAVAILABLE = "CAR_UNAVAILABLE"
    const val KYC_REQUIRED = "KYC_REQUIRED"
    const val OWNER_AGREEMENT_REQUIRED = "OWNER_AGREEMENT_REQUIRED"
    const val WRONG_STATE = "WRONG_STATE"
    const val DEBT_OUTSTANDING = "DEBT_OUTSTANDING"
    const val HANDOVER_INCOMPLETE = "HANDOVER_INCOMPLETE"

    /** Sinh ở phía app, không phải từ backend. */
    const val NETWORK = "NETWORK"
    const val UNAUTHORIZED = "UNAUTHORIZED"
    const val UNKNOWN = "UNKNOWN"
}

sealed interface ApiResult<out T> {
    data class Ok<T>(val data: T) : ApiResult<T>
    data class Err(
        val code: String,
        val message: String? = null,
        val httpStatus: Int? = null,
    ) : ApiResult<Nothing>
}

inline fun <T> ApiResult<T>.onOk(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Ok) block(data)
    return this
}

inline fun <T> ApiResult<T>.onErr(block: (ApiResult.Err) -> Unit): ApiResult<T> {
    if (this is ApiResult.Err) block(this)
    return this
}

fun <T> ApiResult<T>.getOrNull(): T? = (this as? ApiResult.Ok)?.data
