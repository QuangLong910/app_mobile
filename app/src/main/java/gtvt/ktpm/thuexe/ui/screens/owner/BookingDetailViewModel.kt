package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.BookingDetail
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.data.model.HandoverStatus
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookingDetailState(
    val loading: Boolean = false,
    val detail: BookingDetail? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val booking get() = detail?.booking
    val payment get() = detail?.payment
    val bienBanGiao: Handover? get() = detail?.handovers?.firstOrNull { it.kind == HandoverKind.GIAO }
    val bienBanTra: Handover? get() = detail?.handovers?.firstOrNull { it.kind == HandoverKind.TRA }

    val duocDuyet get() = booking?.status == BookingStatus.CHO_CHU_XE
    val duocLapBienBanGiao get() = booking?.status == BookingStatus.DA_XAC_NHAN && bienBanGiao == null

    /** Khách không giữ đúng cam kết, hoặc biên bản trả bị mình phản đối rồi vẫn cần báo thêm. */
    val duocMoTranhChap get() = booking?.status in setOf(
        BookingStatus.DA_XAC_NHAN, BookingStatus.DANG_THUE, BookingStatus.CHO_QUYET_TOAN,
    )
    val duocBaoSuCo get() = booking?.status == BookingStatus.DANG_THUE
    val coYeuCauGiaHan get() = booking?.coYeuCauGiaHan == true

    /**
     * Chủ xe chỉ soi biên bản TRA do khách lập. Biên bản GIAO là của chính mình nên
     * backend từ chối nếu tự soi.
     */
    val duocSoiBienBanTra get() = bienBanTra?.status == HandoverStatus.CHO_SOI

    /** Số điện thoại khách bị giấu cho tới khi đơn qua CHO_CHU_XE. */
    val soDienThoaiKhach: String? get() = booking?.renter?.phone

    /**
     * Đúng hai tình huống chủ xe ngồi nhìn màn hình chờ bên kia bấm. Chỉ hỏi vòng ở đây,
     * các trạng thái khác hoặc là đến lượt mình, hoặc chờ tính bằng ngày.
     */
    val dangChoBenKia: Boolean get() = when (booking?.status) {
        BookingStatus.CHO_THANH_TOAN -> true                              // chờ tiền về
        BookingStatus.DA_XAC_NHAN -> bienBanGiao?.status == HandoverStatus.CHO_SOI  // chờ khách ký
        else -> false
    }
}

@HiltViewModel
class BookingDetailViewModel @Inject constructor(
    private val bookings: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookingDetailState())
    val state: StateFlow<BookingDetailState> = _state.asStateFlow()

    /**
     * @param thamLang lần hỏi vòng định kỳ: không bật vòng xoay, không xoá lỗi đang hiện.
     * Dữ liệu y hệt lần trước thì StateFlow không phát, màn hình không vẽ lại.
     */
    fun tai(maDon: String, thamLang: Boolean = false) = viewModelScope.launch {
        if (!thamLang) _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.detail(maDon)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, detail = r.data) }
            // Hỏi vòng mà rớt mạng thì im lặng bỏ qua, lần sau hỏi lại. Nhảy báo lỗi
            // giữa lúc người ta đang xem là quấy rầy vô ích.
            is ApiResult.Err -> if (!thamLang) fail(r)
        }
    }

    /** Nhận đơn xong backend mới sinh mã chuyển khoản và ảnh QR, nên phải tải lại. */
    fun xacNhan(maDon: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.confirm(maDon)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    fun tuChoi(maDon: String, lyDo: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.reject(maDon, lyDo)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    fun moTranhChap(maDon: String, lyDo: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.openDispute(maDon, lyDo)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    fun baoSuCo(maDon: String, ghiChu: String, loai: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.reportIncident(maDon, ghiChu, loai)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    fun duyetGiaHan(maDon: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.extendApprove(maDon)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    fun tuChoiGiaHan(maDon: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.extendReject(maDon)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> fail(r)
        }
    }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
