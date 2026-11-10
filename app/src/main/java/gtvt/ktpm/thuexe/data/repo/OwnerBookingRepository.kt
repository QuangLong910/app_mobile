package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.BookingApi
import gtvt.ktpm.thuexe.data.api.OwnerApi
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingDetail
import gtvt.ktpm.thuexe.data.model.CancelBookingRequest
import gtvt.ktpm.thuexe.data.model.Incident
import gtvt.ktpm.thuexe.data.model.OpenDisputeRequest
import gtvt.ktpm.thuexe.data.model.Payout
import gtvt.ktpm.thuexe.data.model.RejectBookingRequest
import gtvt.ktpm.thuexe.data.model.ReportIncidentRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerBookingRepository @Inject constructor(
    private val owner: OwnerApi,
    private val bookings: BookingApi,
    private val gson: Gson,
) {
    /** status = null là lấy hết; truyền BookingStatus.CHO_CHU_XE cho màn "đơn chờ tôi duyệt". */
    suspend fun myBookings(status: String? = null): ApiResult<List<Booking>> =
        apiCall(gson) { owner.bookings(status) }

    suspend fun detail(maDon: String): ApiResult<BookingDetail> =
        apiCall(gson) { bookings.booking(maDon) }

    suspend fun confirm(maDon: String): ApiResult<Booking> =
        apiCall(gson) { owner.confirmBooking(maDon) }

    suspend fun reject(maDon: String, reason: String): ApiResult<Booking> =
        apiCall(gson) { owner.rejectBooking(maDon, RejectBookingRequest(reason)) }

    suspend fun cancel(maDon: String, reason: String): ApiResult<Booking> =
        apiCall(gson) { bookings.cancel(maDon, CancelBookingRequest(reason)) }

    suspend fun payouts(): ApiResult<List<Payout>> = apiCall(gson) { owner.payouts() }

    /** Mở khi thấy khách không giữ đúng cam kết. Đơn đứng lại ở TRANH_CHAP, chờ sàn phân xử. */
    suspend fun openDispute(maDon: String, reason: String): ApiResult<Booking> =
        apiCall(gson) { bookings.openDispute(maDon, OpenDisputeRequest(reason)) }

    /** Ghi lại sự cố giữa chuyến — chỉ làm bằng chứng, đơn không đổi trạng thái. */
    suspend fun reportIncident(
        maDon: String, note: String, type: String? = null, photoUrls: List<String> = emptyList(),
    ): ApiResult<Incident> =
        apiCall(gson) { bookings.reportIncident(maDon, ReportIncidentRequest(note, type, photoUrls.ifEmpty { null })) }

    /** Duyệt gia hạn: khoá thêm ngày, cộng tiền thuê vào khoản nợ của khách. */
    suspend fun extendApprove(maDon: String): ApiResult<Booking> = apiCall(gson) { owner.extendApprove(maDon) }

    suspend fun extendReject(maDon: String): ApiResult<Booking> = apiCall(gson) { owner.extendReject(maDon) }
}
