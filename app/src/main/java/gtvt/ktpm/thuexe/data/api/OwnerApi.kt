package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.AcceptAgreementRequest
import gtvt.ktpm.thuexe.data.model.AgreementInfo
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.CalendarUpdateRequest
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarUpsertRequest
import gtvt.ktpm.thuexe.data.model.Payout
import gtvt.ktpm.thuexe.data.model.RejectBookingRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OwnerApi {

    /** Bản điều khoản hiện hành + đã ký hay chưa. */
    @GET("owner/agreement")
    suspend fun agreement(): AgreementInfo

    /** Chưa ký thì POST /owner/cars trả OWNER_AGREEMENT_REQUIRED. */
    @POST("owner/agreement/accept")
    suspend fun acceptAgreement(@Body body: AcceptAgreementRequest): AgreementInfo

    @GET("owner/cars")
    suspend fun cars(): List<Car>

    @POST("owner/cars")
    suspend fun createCar(@Body body: CarUpsertRequest): Car

    @PUT("owner/cars/{id}")
    suspend fun updateCar(@Path("id") carId: Long, @Body body: CarUpsertRequest): Car

    /** Mở hoặc khoá từng ngày trong lịch xe. */
    @PUT("owner/cars/{id}/calendar")
    suspend fun updateCalendar(@Path("id") carId: Long, @Body body: CalendarUpdateRequest)

    @GET("owner/bookings")
    suspend fun bookings(@Query("status") status: String? = null): List<Booking>

    /** Nhận đơn: sinh QR tiền thuê + cọc, hẹn khách 30 phút. */
    @POST("owner/bookings/{id}/confirm")
    suspend fun confirmBooking(@Path("id") maDon: String): Booking

    /** Từ chối đơn: nhả lịch, chưa thu tiền nên không phải hoàn. */
    @POST("owner/bookings/{id}/reject")
    suspend fun rejectBooking(@Path("id") maDon: String, @Body body: RejectBookingRequest): Booking

    /** Tiền đã nhận và tiền đang chờ chuyển. */
    @GET("owner/payouts")
    suspend fun payouts(): List<Payout>

    /** Duyệt gia hạn: khoá thêm ngày, cộng tiền thuê vào khoản nợ của khách. */
    @POST("owner/bookings/{id}/extend-approve")
    suspend fun extendApprove(@Path("id") maDon: String): Booking

    @POST("owner/bookings/{id}/extend-reject")
    suspend fun extendReject(@Path("id") maDon: String): Booking
}
