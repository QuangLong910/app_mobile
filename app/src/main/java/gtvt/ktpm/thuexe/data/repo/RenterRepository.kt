package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.BookingApi
import gtvt.ktpm.thuexe.data.model.Availability
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingDetail
import gtvt.ktpm.thuexe.data.model.CancelBookingRequest
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarSearchItem
import gtvt.ktpm.thuexe.data.model.ExtendRequestRequest
import gtvt.ktpm.thuexe.data.model.Incident
import gtvt.ktpm.thuexe.data.model.OpenDisputeRequest
import gtvt.ktpm.thuexe.data.model.Paged
import gtvt.ktpm.thuexe.data.model.Quote
import gtvt.ktpm.thuexe.data.model.QuoteRequest
import gtvt.ktpm.thuexe.data.model.ReportIncidentRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.apiCall
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/** Mọi lời gọi của khách thuê: tìm xe, báo giá, đặt xe, theo dõi và huỷ đơn. */
@Singleton
class RenterRepository @Inject constructor(
    private val api: BookingApi,
    private val gson: Gson,
) {
    suspend fun search(
        district: String? = null,
        from: LocalDate? = null,
        to: LocalDate? = null,
        seats: Int? = null,
        transmission: String? = null,
        maxPrice: Long? = null,
        page: Int = 1,
    ): ApiResult<Paged<CarSearchItem>> = apiCall(gson) {
        api.searchCars(
            district = district?.trim()?.ifBlank { null },
            from = from?.toString(),
            to = to?.toString(),
            seats = seats,
            transmission = transmission,
            maxPrice = maxPrice,
            page = page,
        )
    }

    suspend fun car(id: Long): ApiResult<Car> = apiCall(gson) { api.car(id) }

    suspend fun availability(id: Long, from: LocalDate, to: LocalDate): ApiResult<Availability> =
        apiCall(gson) { api.availability(id, from.toString(), to.toString()) }

    suspend fun quote(carId: Long, from: LocalDate, to: LocalDate): ApiResult<Quote> =
        apiCall(gson) { api.quote(QuoteRequest(carId, from, to)) }

    suspend fun book(carId: Long, from: LocalDate, to: LocalDate): ApiResult<Booking> =
        apiCall(gson) { api.createBooking(QuoteRequest(carId, from, to)) }

    suspend fun myBookings(status: String? = null): ApiResult<List<Booking>> =
        apiCall(gson) { api.myBookings(status) }

    suspend fun giaLapChuyenKhoan(maDon: String): ApiResult<Unit> = apiCall(gson) { api.giaLapChuyenKhoan(maDon) }

    suspend fun detail(maDon: String): ApiResult<BookingDetail> = apiCall(gson) { api.booking(maDon) }

    suspend fun cancel(maDon: String, reason: String): ApiResult<Booking> =
        apiCall(gson) { api.cancel(maDon, CancelBookingRequest(reason)) }

    /** Mở khi thấy chủ xe không giữ đúng cam kết. Đơn đứng lại ở TRANH_CHAP, chờ sàn phân xử. */
    suspend fun openDispute(maDon: String, reason: String): ApiResult<Booking> =
        apiCall(gson) { api.openDispute(maDon, OpenDisputeRequest(reason)) }

    /** Ghi lại sự cố giữa chuyến — chỉ làm bằng chứng, đơn không đổi trạng thái. */
    suspend fun reportIncident(
        maDon: String, note: String, type: String? = null, photoUrls: List<String> = emptyList(),
    ): ApiResult<Incident> =
        apiCall(gson) { api.reportIncident(maDon, ReportIncidentRequest(note, type, photoUrls.ifEmpty { null })) }

    /** Xin kéo dài chuyến, chờ chủ xe duyệt. */
    suspend fun extendRequest(maDon: String, newEndDate: LocalDate): ApiResult<Booking> =
        apiCall(gson) { api.extendRequest(maDon, ExtendRequestRequest(newEndDate)) }
}
