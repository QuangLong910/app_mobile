package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import gtvt.ktpm.thuexe.data.repo.RenterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Số việc đang chờ mình, hiện thành chấm đỏ trên thanh tab. Thay cho thông báo đẩy: chủ xe
 * chỉ có 30 phút để nhận đơn, không thể bắt họ mở từng tab mới biết có đơn mới.
 *
 * Hỏi lại mỗi 30 giây khi màn còn sống. Rớt mạng thì giữ số cũ, không nhảy về 0.
 */
@HiltViewModel
class TabBadgeViewModel @Inject constructor(
    private val chuXe: OwnerBookingRepository,
    private val khach: RenterRepository,
) : ViewModel() {

    private val _donChoToiDuyet = MutableStateFlow(0)
    /** Chủ xe: đơn đang chờ mình nhận. */
    val donChoToiDuyet: StateFlow<Int> = _donChoToiDuyet.asStateFlow()

    private val _chuyenCanLam = MutableStateFlow(0)
    /** Khách: chuyến đang chờ mình chuyển tiền hoặc chờ mình soi biên bản. */
    val chuyenCanLam: StateFlow<Int> = _chuyenCanLam.asStateFlow()

    /** Lấy số mới một lần. Gọi lại mỗi khi quay về màn chính hoặc có thông báo đẩy. */
    fun lamMoi(laChuXe: Boolean) {
        viewModelScope.launch {
            if (laChuXe) {
                (chuXe.myBookings(BookingStatus.CHO_CHU_XE) as? ApiResult.Ok)
                    ?.let { _donChoToiDuyet.value = it.data.size }
            } else {
                (khach.myBookings() as? ApiResult.Ok)?.let { r ->
                    _chuyenCanLam.value = r.data.count { it.status == BookingStatus.CHO_THANH_TOAN }
                }
            }
        }
    }
}
