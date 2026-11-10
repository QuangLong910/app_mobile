package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.Availability
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingDetail
import gtvt.ktpm.thuexe.data.model.CancelBookingRequest
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarSearchItem
import gtvt.ktpm.thuexe.data.model.Paged
import gtvt.ktpm.thuexe.data.model.Quote
import gtvt.ktpm.thuexe.data.model.QuoteRequest
import gtvt.ktpm.thuexe.data.model.CreateHandoverRequest
import gtvt.ktpm.thuexe.data.model.ExtendRequestRequest
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.Incident
import gtvt.ktpm.thuexe.data.model.OpenDisputeRequest
import gtvt.ktpm.thuexe.data.model.ReportIncidentRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Các điểm cuối dùng chung app–web mà chủ xe vẫn cần. */
interface BookingApi {

    @GET("cars/{id}")
    suspend fun car(@Path("id") carId: Long): Car

    /** Ngày trống trong khoảng, để tô lịch. Ngày là chuỗi "2026-09-12". */
    @GET("cars/{id}/availability")
    suspend fun availability(
        @Path("id") carId: Long,
        @Query("from") from: String,
        @Query("to") to: String,
    ): Availability

    /** Tìm xe của khách: mọi tham số đều tuỳ chọn. */
    @GET("cars")
    suspend fun searchCars(
        @Query("district") district: String? = null,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("seats") seats: Int? = null,
        @Query("transmission") transmission: String? = null,
        @Query("maxPrice") maxPrice: Long? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
    ): Paged<CarSearchItem>

    @POST("bookings/quote")
    suspend fun quote(@Body body: QuoteRequest): Quote

    /** Đặt xe: đơn vào CHO_CHU_XE, giữ lịch, chưa thu tiền. */
    @POST("bookings")
    suspend fun createBooking(@Body body: QuoteRequest): Booking

    /** Đơn của tôi ở vai khách. */
    @GET("bookings")
    suspend fun myBookings(@Query("status") status: String? = null): List<Booking>

    /** Chỉ môi trường dev: giả lập khách vừa chuyển khoản đủ tiền, để demo không cần webhook. */
    @POST("dev/gia-lap-chuyen-khoan/{ma}")
    suspend fun giaLapChuyenKhoan(@Path("ma") ma: String)

    @GET("bookings/{id}")
    suspend fun booking(@Path("id") maDon: String): BookingDetail

    /** Chỉ cho phép khi chưa có biên bản giao (G4). */
    @POST("bookings/{id}/cancel")
    suspend fun cancel(@Path("id") maDon: String, @Body body: CancelBookingRequest): Booking

    /** Bên lập nộp biên bản — chủ xe lập biên bản GIAO. Vào trạng thái CHO_SOI. */
    @POST("bookings/{id}/handovers")
    suspend fun createHandover(@Path("id") maDon: String, @Body body: CreateHandoverRequest): Handover

    /** Bên nào cũng mở được khi thấy bên kia không giữ đúng cam kết. Đơn đứng lại ở TRANH_CHAP. */
    @POST("bookings/{id}/dispute")
    suspend fun openDispute(@Path("id") maDon: String, @Body body: OpenDisputeRequest): Booking

    /** Ghi lại sự cố giữa chuyến kèm ảnh, KHÔNG đổi trạng thái đơn. */
    @POST("bookings/{id}/incidents")
    suspend fun reportIncident(@Path("id") maDon: String, @Body body: ReportIncidentRequest): Incident

    /** Khách xin kéo dài chuyến, chờ chủ xe duyệt. */
    @POST("bookings/{id}/extend-request")
    suspend fun extendRequest(@Path("id") maDon: String, @Body body: ExtendRequestRequest): Booking
}
