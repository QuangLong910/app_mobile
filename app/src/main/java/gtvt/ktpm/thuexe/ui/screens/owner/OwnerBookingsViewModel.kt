package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OwnerBookingsState(
    val loading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class OwnerBookingsViewModel @Inject constructor(
    private val repo: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OwnerBookingsState())
    val state: StateFlow<OwnerBookingsState> = _state.asStateFlow()

    /** Bộ lọc của tab đang mở, giữ lại để nạp lại đúng danh sách sau mỗi thao tác. */
    private var locHienTai: String? = null

    /** status = null lấy hết; truyền BookingStatus.CHO_CHU_XE cho màn "đơn chờ tôi duyệt". */
    fun load(status: String? = null): Job {
        // Đổi tab khi lời gọi cũ chưa xong: bỏ lời gọi cũ, không thì nó về sau và đè danh sách
        // của tab mới bằng dữ liệu của tab cũ.
        dangTai?.cancel()
        val doiTab = status != locHienTai
        locHienTai = status
        return viewModelScope.launch {
            taiDanhSach(status, doiTab)
        }.also { dangTai = it }
    }

    private var dangTai: Job? = null

    private suspend fun taiDanhSach(status: String?, doiTab: Boolean) {
        // Đổi tab thì xoá danh sách cũ ngay, tránh hiện đơn của tab trước dưới tên tab mới.
        _state.update {
            it.copy(
                loading = true, errorCode = null, errorMessage = null,
                bookings = if (doiTab) emptyList() else it.bookings,
            )
        }
        when (val r = repo.myBookings(status)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, bookings = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun confirm(maDon: String) = act { repo.confirm(maDon) }

    fun reject(maDon: String, reason: String) = act { repo.reject(maDon, reason) }

    fun cancel(maDon: String, reason: String) = act { repo.cancel(maDon, reason) }

    /**
     * Bấm nút xong thì nạp lại cả danh sách theo đúng tab đang mở.
     * Trước đây chỉ vá một dòng tại chỗ, nên đơn vừa nhận vẫn nằm lại tab "Chờ tôi duyệt"
     * dù đã sang CHO_THANH_TOAN — nhìn thì như nút không ăn.
     */
    private fun act(block: suspend () -> ApiResult<Booking>) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = block()) {
            is ApiResult.Ok -> {
                load(locHienTai)
                return@launch
            }

            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }
}
