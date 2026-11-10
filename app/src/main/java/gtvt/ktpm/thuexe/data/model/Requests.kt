package gtvt.ktpm.thuexe.data.model

import java.time.LocalDate

data class RegisterRequest(
    val phone: String,
    val password: String,
    val fullName: String,
    val email: String? = null,
    val isOwner: Boolean = false,
)

data class VerifyOtpRequest(val phone: String, val otp: String)

data class LoginRequest(val phone: String, val password: String)

/** Backend đặt tên trường là "profile", không phải "user". */
data class AuthResponse(val token: String, val profile: MeResponse)

data class SubmitDocumentsRequest(
    val cccdNo: String?,
    val gplxNo: String?,
    val gplxClass: String?,
    val gplxExpiry: LocalDate?,
    val frontUrl: String,
    val backUrl: String,
    val selfieUrl: String? = null,
)

data class AcceptAgreementRequest(
    val version: String,
    val cccdNo: String,
    val bankAccount: String,
    val bankName: String,
    val signatureUrl: String,
)

data class CarDocumentRequest(
    val type: String,
    val url: String,
    val expiryDate: LocalDate? = null,
)

/** Dùng cho cả POST /owner/cars lẫn PUT /owner/cars/{id}. */
data class CarUpsertRequest(
    val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val seats: Int,
    val transmission: String,
    val fuel: String,
    val odo: Int,
    val district: String,
    val pickupAddress: String,
    val pricePerDay: Long,
    val maxKmDay: Int = 300,
    val deposit: Long = 15_000_000L,
    val description: String? = null,
    val photoUrls: List<String> = emptyList(),
    val documents: List<CarDocumentRequest> = emptyList(),
)

/** PUT /owner/cars/{id}/calendar — blocked = true là khoá, false là mở lại. */
data class CalendarUpdateRequest(
    val days: List<LocalDate>,
    val blocked: Boolean,
)

data class RejectBookingRequest(val reason: String)

data class CancelBookingRequest(val reason: String)

data class HandoverPhotoRequest(
    val slot: String,
    val url: String,
    val note: String? = null,
)

/** POST /bookings/{id}/handovers — bên lập nộp biên bản. */
data class CreateHandoverRequest(
    val kind: String,
    val odo: Int,
    val fuelLevel: Int,
    val note: String? = null,
    /** Chữ ký của bên lập, là url ảnh đã tải qua POST /files. */
    val signature: String,
    val photos: List<HandoverPhotoRequest>,
)

data class AddHandoverPhotosRequest(val photos: List<HandoverPhotoRequest>)

/** POST /handovers/{id}/review — agreed = false thì objection bắt buộc. */
data class ReviewHandoverRequest(
    val agreed: Boolean,
    val objection: String? = null,
    val signature: String? = null,
)

/** Dùng chung cho POST /bookings/quote và POST /bookings. */
data class QuoteRequest(val carId: Long, val startDate: LocalDate, val endDate: LocalDate)

/** POST /bookings/{id}/dispute — bên nào cũng mở được. */
data class OpenDisputeRequest(val reason: String)

/** POST /bookings/{id}/incidents — ghi bằng chứng, không đổi trạng thái đơn. */
data class ReportIncidentRequest(val note: String, val type: String? = null, val photoUrls: List<String>? = null)

/** POST /bookings/{id}/extend-request — khách xin gia hạn, chờ chủ xe duyệt. */
data class ExtendRequestRequest(val newEndDate: LocalDate)

/** PUT /me/bank-account — nơi nhận tiền hoàn cọc. */
data class BankAccountRequest(val bankAccount: String, val bankName: String)

data class DeviceTokenRequest(val token: String, val platform: String = "android")
