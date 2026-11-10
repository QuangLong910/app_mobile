package gtvt.ktpm.thuexe.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.OffsetDateTime

// Tiền: luôn Long (đồng). Ngày thuê: LocalDate. Mốc hệ thống: OffsetDateTime.

data class User(
    val id: Long,
    val phone: String,
    val fullName: String,
    val email: String? = null,
    val isOwner: Boolean = false,
    val status: String = "HOAT_DONG",
    val createdAt: OffsetDateTime? = null,
)

data class IdDocument(
    val id: Long,
    val cccdNo: String? = null,
    val gplxNo: String? = null,
    val gplxClass: String? = null,
    val gplxExpiry: LocalDate? = null,
    val frontUrl: String,
    val backUrl: String,
    val selfieUrl: String? = null,
    val status: String,
    val rejectReason: String? = null,
    val reviewedAt: OffsetDateTime? = null,
)

/**
 * GET /me — backend trả phẳng, không lồng: chỉ có hồ sơ và MỘT chuỗi trạng thái giấy tờ,
 * không kèm chi tiết CCCD/GPLX. Muốn chi tiết thì phải xin N1 thêm trường.
 */
data class MeResponse(
    val id: Long,
    val phone: String,
    val fullName: String,
    val email: String? = null,
    val isOwner: Boolean = false,
    val status: String = "HOAT_DONG",
    val documentStatus: String = DocStatus.CHUA_NOP,
    /** Nơi nhận tiền hoàn cọc. Chưa khai thì null: lệnh hoàn ghi CHUA_CO. */
    val bankAccount: String? = null,
    val bankName: String? = null,
)

data class UploadedFile(val url: String)

/**
 * POST /me/documents chỉ trả về id và trạng thái, không trả lại cả bản ghi.
 * Đừng dùng IdDocument ở đây: frontUrl/backUrl khai không-null, Gson nhét null vào
 * và chỗ nào đọc tới là ném NullPointerException mà không ai hiểu vì sao.
 */
data class SubmitDocumentsResponse(
    val id: Long,
    val status: String,
)

data class CarPhoto(
    val id: Long? = null,
    val url: String,
    val sortOrder: Int = 0,
)

data class CarDocument(
    val id: Long? = null,
    val type: String,
    val url: String,
    val expiryDate: LocalDate? = null,
)

data class Car(
    val id: Long,
    val ownerId: Long? = null,
    val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val seats: Int,
    val transmission: String,
    val fuel: String,
    val odo: Int = 0,
    val district: String,
    /** Chỉ mở cho khách sau khi đơn đã xác nhận; chủ xe luôn thấy. */
    val pickupAddress: String? = null,
    val pricePerDay: Long,
    val maxKmDay: Int = 300,
    val deposit: Long = 15_000_000L,
    val description: String? = null,
    val status: String,
    val rejectReason: String? = null,
    val createdAt: OffsetDateTime? = null,
    val photos: List<CarPhoto> = emptyList(),
    val documents: List<CarDocument> = emptyList(),
)

data class CarBrief(
    val id: Long,
    val plate: String,
    val brand: String,
    val model: String,
    val photoUrl: String? = null,
)

data class RenterBrief(
    val id: Long,
    val fullName: String,
    /** Backend chỉ trả sau khi đơn đã xác nhận. */
    val phone: String? = null,
    val idDocumentStatus: String? = null,
)

data class Booking(
    val id: Long,
    val code: String,
    val carId: Long,
    val car: CarBrief? = null,
    val renter: RenterBrief? = null,
    val startDate: LocalDate,
    /** Ngày trả — KHÔNG tính tiền, nhưng lịch vẫn khoá ngày này. */
    val endDate: LocalDate,
    val days: Int,
    val pricePerDay: Long,
    val rentTotal: Long,
    val deposit: Long,
    val commission: Long,
    val status: String,
    val holdExpiresAt: OffsetDateTime? = null,
    val cancelReason: String? = null,
    val createdAt: OffsetDateTime? = null,
    /** Phí phát sinh vượt cọc khi chốt đơn. Còn nợ khi > 0 và debtPaidAt == null. */
    val debtAmount: Long = 0L,
    val debtPaidAt: OffsetDateTime? = null,
    /** Đơn đang TRANH_CHAP thì bốn trường này có giá trị; preDisputeStatus là chỗ sẽ quay lại. */
    val preDisputeStatus: String? = null,
    val disputeReason: String? = null,
    /** CHU_XE | KHACH | null (sàn tự mở do quá hạn không ai xử lý). */
    val disputeOpenedBy: String? = null,
    val disputeOpenedAt: OffsetDateTime? = null,
    /** Khách xin gia hạn, chờ chủ xe duyệt. */
    val extendRequestedEndDate: LocalDate? = null,
    val extendRequestedAt: OffsetDateTime? = null,
) {
    val conNo: Boolean get() = debtAmount > 0 && debtPaidAt == null
    val dangTranhChap: Boolean get() = status == BookingStatus.TRANH_CHAP
    val coYeuCauGiaHan: Boolean get() = extendRequestedEndDate != null
}

data class Payment(
    val id: Long,
    val bookingId: Long,
    val amount: Long,
    /** Backend chỉ sinh mã và QR lúc chủ xe nhận đơn, nên trước đó hai trường này rỗng. */
    val transferCode: String? = null,
    val qrUrl: String? = null,
    val status: String,
    val receivedAmount: Long? = null,
    val confirmedAt: OffsetDateTime? = null,
    val bankNote: String? = null,
)

data class Charge(
    val id: Long? = null,
    val type: String,
    val amount: Long,
    val note: String? = null,
)

data class HandoverPhoto(
    val id: Long? = null,
    val slot: String,
    val url: String,
    val takenBy: String? = null,
    val note: String? = null,
)

data class Handover(
    val id: Long,
    val bookingId: Long,
    val kind: String,
    val createdBy: String,
    val odo: Int,
    /** Thang 8 nấc. */
    val fuelLevel: Int,
    val note: String? = null,
    val status: String,
    val signCreator: String? = null,
    val signReviewer: String? = null,
    val objection: String? = null,
    val reviewedAt: OffsetDateTime? = null,
    val createdAt: OffsetDateTime? = null,
    val photos: List<HandoverPhoto> = emptyList(),
)

/** Ghi lại sự cố giữa chuyến (tai nạn, va quẹt…), chỉ để làm bằng chứng — không đổi trạng thái đơn. */
data class Incident(
    val id: Long,
    val bookingId: Long,
    val reportedBy: String,
    /** VA_CHAM | KHAC — xem LoaiSuCo. */
    val type: String = LoaiSuCo.KHAC,
    val note: String,
    val createdAt: OffsetDateTime? = null,
    val photoUrls: List<String> = emptyList(),
)

data class BookingDetail(
    val booking: Booking,
    val payment: Payment? = null,
    val handovers: List<Handover> = emptyList(),
    val charges: List<Charge> = emptyList(),
    val incidents: List<Incident> = emptyList(),
)

data class Payout(
    val id: Long,
    val bookingId: Long,
    val bookingCode: String? = null,
    val payeeType: String,
    val bankAccount: String,
    val bankName: String,
    val amount: Long,
    val status: String,
    val transferRef: String? = null,
    val paidAt: OffsetDateTime? = null,
    val createdAt: OffsetDateTime? = null,
)

/** GET /cars/{id}/availability — ngày nào còn trống trong khoảng hỏi. */
data class Availability(
    @SerializedName("carId") val carId: Long,
    val from: LocalDate,
    val to: LocalDate,
    val busyDays: List<LocalDate> = emptyList(),
    val blockedDays: List<LocalDate> = emptyList(),
)

data class AgreementInfo(
    val version: String,
    val content: String,
    val signed: Boolean,
    val signedAt: OffsetDateTime? = null,
)

/**
 * NGOÀI §04 — bảng API của kế hoạch chưa có GET /news.
 * Đã báo N1 bổ sung; nếu backend không làm thì bỏ màn tin tức đi.
 */
data class NewsItem(
    val id: Long,
    val tag: String,
    val title: String,
    val summary: String,
    val imageUrl: String? = null,
    val publishedAt: LocalDate? = null,
    /** Tin lấy về từ nguồn ngoài (ví dụ VnExpress); null thì là tin của sàn. */
    val source: String? = null,
    /** Bài gốc, để mở khi bấm vào — không hiển thị toàn văn trong app. */
    val link: String? = null,
)

/** GET /cars — một trang kết quả tìm xe. */
data class Paged<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Int,
    val totalPages: Int,
)

/** Bản rút gọn của xe ở màn tìm kiếm: không có địa chỉ giao xe. */
data class CarSearchItem(
    val id: Long,
    val plate: String,
    val brand: String,
    val model: String,
    val year: Int,
    val seats: Int,
    val transmission: String,
    val fuel: String,
    val district: String,
    val pricePerDay: Long,
    val maxKmDay: Int = 300,
    val deposit: Long = 0L,
    val photoUrl: String? = null,
)

/** POST /bookings/quote — tính giá, chưa tạo đơn. */
data class Quote(
    val carId: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: Int,
    val pricePerDay: Long,
    val rentTotal: Long,
    val deposit: Long,
    val commission: Long,
    /** Khách chuyển một lần: tiền thuê cộng tiền cọc. */
    val tongPhaiTra: Long,
    val conTrong: Boolean,
    val ngayBanTrongKhoang: List<LocalDate> = emptyList(),
)
