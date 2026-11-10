package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.model.Payout
import gtvt.ktpm.thuexe.data.model.PayoutStatus
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import gtvt.ktpm.thuexe.data.repo.OwnerCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class DashboardState(
    val loading: Boolean = false,
    val me: MeResponse? = null,
    val cars: List<Car> = emptyList(),
    val bookings: List<Booking> = emptyList(),
    val payouts: List<Payout> = emptyList(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val xeDangChoThue: Int get() = cars.count { it.status == CarStatus.DANG_BAN }
    val xeChoDuyet: Int get() = cars.count { it.status == CarStatus.CHO_DUYET }

    val donChoDuyet: List<Booking> get() = bookings.filter { it.status == BookingStatus.CHO_CHU_XE }
    val chuyenDangChay: List<Booking> get() = bookings.filter { it.status == BookingStatus.DANG_THUE }
    val choKhachTraTien: List<Booking> get() = bookings.filter { it.status == BookingStatus.CHO_THANH_TOAN }

    /** Đơn đã xác nhận mà chưa có biên bản giao — hôm nay hoặc mai phải giao xe. */
    val sapPhaiGiaoXe: List<Booking>
        get() = bookings.filter {
            it.status == BookingStatus.DA_XAC_NHAN && it.startDate <= LocalDate.now().plusDays(1)
        }

    val tienDangCho: Long get() = payouts.filter { it.status == PayoutStatus.CHO }.sumOf { it.amount }
    val tienDaNhan: Long get() = payouts.filter { it.status == PayoutStatus.DA_CHI }.sumOf { it.amount }

    /** Tiền còn nằm trong các chuyến chưa quyết toán — chưa phải tiền thật cho tới khi chốt đơn. */
    val tienSapVe: Long
        get() = bookings
            .filter { it.status in DANG_CHAY }
            .sumOf { it.rentTotal - it.commission }

    private companion object {
        val DANG_CHAY = setOf(
            BookingStatus.DA_XAC_NHAN,
            BookingStatus.DANG_THUE,
            BookingStatus.CHO_QUYET_TOAN,
            BookingStatus.CHO_CHI_TRA,
        )
    }
}

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val cars: OwnerCarRepository,
    private val bookings: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }

        when (val r = auth.me()) {
            is ApiResult.Ok -> _state.update { it.copy(me = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        when (val r = cars.myCars()) {
            is ApiResult.Ok -> _state.update { it.copy(cars = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        when (val r = bookings.myBookings()) {
            is ApiResult.Ok -> _state.update { it.copy(bookings = r.data) }
            is ApiResult.Err -> return@launch fail(r)
        }
        when (val r = bookings.payouts()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, payouts = r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
