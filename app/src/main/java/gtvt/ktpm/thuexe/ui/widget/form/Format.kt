package gtvt.ktpm.thuexe.ui.widget.form

import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.data.model.ChargeType
import gtvt.ktpm.thuexe.data.model.DocStatus
import gtvt.ktpm.thuexe.data.model.Fuel
import gtvt.ktpm.thuexe.data.model.HandoverStatus
import gtvt.ktpm.thuexe.data.model.PayoutStatus
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.data.model.Transmission
import gtvt.ktpm.thuexe.data.net.ErrorCode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

private val tienFormat = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US).apply {
    groupingSeparator = '.'
})

/** 1800000 -> "1.800.000 đ". Chỉ ở đây mới định dạng tiền; mọi chỗ khác giữ Long. */
fun Long.tien(): String = tienFormat.format(this) + " đ"

fun Long.tienKhongDonVi(): String = tienFormat.format(this)

private val ngayVn = DateTimeFormatter.ofPattern("dd/MM/yyyy")
private val gioNgayVn = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy")

fun LocalDate.ngay(): String = format(ngayVn)

fun OffsetDateTime.gioNgay(): String = format(gioNgayVn)

fun nhanTrangThaiDon(status: String): String = when (status) {
    BookingStatus.CHO_CHU_XE -> "Chờ bạn duyệt"
    BookingStatus.CHO_THANH_TOAN -> "Chờ khách trả tiền"
    BookingStatus.DA_XAC_NHAN -> "Đã xác nhận"
    BookingStatus.DANG_THUE -> "Đang thuê"
    BookingStatus.CHO_QUYET_TOAN -> "Chờ quyết toán"
    BookingStatus.CHO_CHI_TRA -> "Chờ chi trả"
    BookingStatus.HOAN_TAT -> "Hoàn tất"
    BookingStatus.BI_TU_CHOI -> "Bạn đã từ chối"
    BookingStatus.HET_HAN -> "Hết hạn"
    BookingStatus.DA_HUY -> "Đã huỷ"
    BookingStatus.TRANH_CHAP -> "Đang tranh chấp"
    else -> status
}

fun nhanTrangThaiXe(status: String): String = when (status) {
    CarStatus.NHAP -> "Nháp"
    CarStatus.CHO_DUYET -> "Chờ duyệt"
    CarStatus.DANG_BAN -> "Đang cho thuê"
    CarStatus.AN -> "Đã ẩn"
    else -> status
}

fun nhanTrangThaiGiayTo(status: String): String = when (status) {
    DocStatus.CHO_DUYET -> "Chờ duyệt"
    DocStatus.DAT -> "Đã duyệt"
    DocStatus.TU_CHOI -> "Bị từ chối"
    else -> status
}

fun nhanTrangThaiBienBan(status: String): String = when (status) {
    HandoverStatus.CHO_SOI -> "Chờ bên kia soi"
    HandoverStatus.DA_KY -> "Đã ký"
    HandoverStatus.PHAN_DOI -> "Có phản đối"
    else -> status
}

fun nhanTrangThaiChiTra(status: String): String = when (status) {
    PayoutStatus.CHO -> "Chờ chuyển"
    PayoutStatus.DA_CHI -> "Đã chuyển"
    PayoutStatus.LOI -> "Chuyển lỗi"
    else -> status
}

fun nhanHopSo(value: String): String = when (value) {
    Transmission.SO_SAN -> "Số sàn"
    Transmission.SO_TU_DONG -> "Số tự động"
    else -> value
}

fun nhanNhienLieu(value: String): String = when (value) {
    Fuel.XANG -> "Xăng"
    Fuel.DAU -> "Dầu"
    Fuel.DIEN -> "Điện"
    else -> value
}

fun nhanPhi(type: String): String = when (type) {
    ChargeType.QUA_GIO -> "Quá giờ"
    ChargeType.QUA_KM -> "Quá số km"
    ChargeType.NHIEN_LIEU -> "Nhiên liệu"
    ChargeType.HU_HONG -> "Hư hỏng, trầy xước"
    ChargeType.VE_SINH -> "Vệ sinh xe"
    ChargeType.MAT_PHU_KIEN -> "Mất phụ kiện"
    ChargeType.PHI_CAU_DUONG -> "Phí cầu đường"
    ChargeType.PHI_DO_XE -> "Phí đỗ xe"
    ChargeType.NGAY_NGHI_SUA -> "Ngày xe nghỉ sửa"
    ChargeType.PHAT_NGUOI -> "Phạt nguội"
    else -> type
}

/** CHU_XE/KHACH — dùng cho "bên mở tranh chấp", "bên báo sự cố". */
fun nhanBen(value: String): String = when (value) {
    gtvt.ktpm.thuexe.data.model.PartySide.CHU_XE -> "Chủ xe"
    gtvt.ktpm.thuexe.data.model.PartySide.KHACH -> "Khách"
    else -> value
}

fun nhanLoaiSuCo(value: String): String = when (value) {
    gtvt.ktpm.thuexe.data.model.LoaiSuCo.VA_CHAM -> "Va chạm / tai nạn"
    gtvt.ktpm.thuexe.data.model.LoaiSuCo.KHAC -> "Khác"
    else -> value
}

fun nhanKhungAnh(slot: String): String = when (slot) {
    PhotoSlot.TRUOC -> "Đầu xe"
    PhotoSlot.SAU -> "Đuôi xe"
    PhotoSlot.TRAI -> "Hông trái"
    PhotoSlot.PHAI -> "Hông phải"
    PhotoSlot.TAPLO -> "Táp lô"
    PhotoSlot.ODO -> "Đồng hồ ODO"
    PhotoSlot.KHAC -> "Ảnh thêm"
    else -> slot
}

/** Giao diện bắt theo code, không bắt theo câu chữ backend gửi. */
fun nhanLoi(code: String?, message: String? = null): String = when (code) {
    ErrorCode.NETWORK -> "Mất kết nối tới máy chủ. Kiểm tra mạng rồi thử lại."
    ErrorCode.UNAUTHORIZED -> "Phiên đăng nhập đã hết. Đăng nhập lại giúp mình."
    ErrorCode.WRONG_STATE -> "Đơn vừa đổi trạng thái. Tải lại rồi thao tác tiếp."
    ErrorCode.SLOT_TAKEN -> "Những ngày này đã có đơn khác giữ mất rồi."
    ErrorCode.CAR_UNAVAILABLE -> "Xe đang không ở trạng thái cho thuê."
    ErrorCode.KYC_REQUIRED -> message ?: "Giấy tờ chưa được sàn duyệt."
    ErrorCode.DEBT_OUTSTANDING -> "Bạn còn khoản nợ từ chuyến trước. Trả nợ rồi mới đặt chuyến mới được."
    ErrorCode.OWNER_AGREEMENT_REQUIRED -> "Phải ký cam kết chủ xe trước khi đăng xe."
    ErrorCode.HANDOVER_INCOMPLETE -> message ?: "Biên bản còn thiếu ảnh hoặc chữ ký."
    // Mã do backend ASP.NET trả. BizException đặt message bằng chính mã nếu không truyền
    // câu chữ, nên không dịch ở đây là người dùng đọc thấy "LOGIN_FAILED".
    "LOGIN_FAILED", "SAI_DANG_NHAP" -> "Số điện thoại hoặc mật khẩu không đúng."
    "ACCOUNT_LOCKED" -> "Tài khoản đã bị khoá. Liên hệ sàn để mở lại."
    "PHONE_TAKEN" -> "Số điện thoại này đã có tài khoản."
    "OTP_INVALID" -> "Mã OTP không đúng hoặc đã hết hạn."
    "USER_NOT_FOUND" -> "Không tìm thấy tài khoản."
    "INVALID_INPUT" -> message ?: "Dữ liệu nhập chưa hợp lệ."
    "INTERNAL_ERROR" -> "Máy chủ đang lỗi. Thử lại sau giúp mình."
    "FORBIDDEN" -> message ?: "Bạn không có quyền với mục này."
    "PLATE_TAKEN" -> "Biển số này đã có người đăng."
    else -> message ?: "Có lỗi xảy ra, thử lại giúp mình."
}
