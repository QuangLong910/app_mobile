package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.Availability
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import gtvt.ktpm.thuexe.data.repo.OwnerCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

data class CarCalendarState(
    val loading: Boolean = false,
    val car: Car? = null,
    val thang: YearMonth = YearMonth.now(),
    val availability: Availability? = null,
    val chuyenCuaXe: List<Booking> = emptyList(),
    val dangChon: Set<LocalDate> = emptySet(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val ngayBan: Set<LocalDate> get() = availability?.busyDays.orEmpty().toSet()
    val ngayKhoa: Set<LocalDate> get() = availability?.blockedDays.orEmpty().toSet()

    /** Chuyến gần nhất còn hiệu lực — thứ chủ xe muốn thấy khi mở màn theo dõi. */
    val chuyenSapToi: Booking?
        get() = chuyenCuaXe
            .filter { it.status in DANG_SONG && !it.endDate.isBefore(LocalDate.now()) }
            .minByOrNull { it.startDate }

    private companion object {
        val DANG_SONG = setOf(
            BookingStatus.CHO_CHU_XE,
            BookingStatus.CHO_THANH_TOAN,
            BookingStatus.DA_XAC_NHAN,
            BookingStatus.DANG_THUE,
        )
    }
}

@HiltViewModel
class CarCalendarViewModel @Inject constructor(
    private val cars: OwnerCarRepository,
    private val bookings: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CarCalendarState())
    val state: StateFlow<CarCalendarState> = _state.asStateFlow()

    fun load(carId: Long) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }

        when (val r = cars.car(carId)) {
            is ApiResult.Ok -> _state.update { it.copy(car = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        when (val r = bookings.myBookings()) {
            is ApiResult.Ok -> _state.update { s -> s.copy(chuyenCuaXe = r.data.filter { it.carId == carId }) }
            is ApiResult.Err -> return@launch fail(r)
        }
        napLich(carId, _state.value.thang)
    }

    fun doiThang(carId: Long, thang: YearMonth) {
        _state.update { it.copy(thang = thang, dangChon = emptySet()) }
        viewModelScope.launch { napLich(carId, thang) }
    }

    fun chon(ngay: LocalDate) = _state.update {
        it.copy(dangChon = if (ngay in it.dangChon) it.dangChon - ngay else it.dangChon + ngay)
    }

    fun boChon() = _state.update { it.copy(dangChon = emptySet()) }

    fun khoaNgayDangChon(carId: Long) = capNhatLich(carId, khoa = true)

    fun moNgayDangChon(carId: Long) = capNhatLich(carId, khoa = false)

    private fun capNhatLich(carId: Long, khoa: Boolean) = viewModelScope.launch {
        val ngay = _state.value.dangChon.toList()
        if (ngay.isEmpty()) return@launch
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        val r = if (khoa) cars.blockDays(carId, ngay) else cars.openDays(carId, ngay)
        when (r) {
            is ApiResult.Ok -> {
                _state.update { it.copy(dangChon = emptySet()) }
                napLich(carId, _state.value.thang)
            }
            is ApiResult.Err -> fail(r)
        }
    }

    private suspend fun napLich(carId: Long, thang: YearMonth) {
        when (val r = cars.availability(carId, thang.atDay(1), thang.atEndOfMonth())) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, availability = r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
