package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.BookingApi
import gtvt.ktpm.thuexe.data.api.OwnerApi
import gtvt.ktpm.thuexe.data.model.AcceptAgreementRequest
import gtvt.ktpm.thuexe.data.model.AgreementInfo
import gtvt.ktpm.thuexe.data.model.Availability
import gtvt.ktpm.thuexe.data.model.CalendarUpdateRequest
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarUpsertRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.apiCall
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OwnerCarRepository @Inject constructor(
    private val owner: OwnerApi,
    private val bookings: BookingApi,
    private val gson: Gson,
) {
    suspend fun agreement(): ApiResult<AgreementInfo> = apiCall(gson) { owner.agreement() }

    suspend fun acceptAgreement(
        version: String,
        cccdNo: String,
        bankAccount: String,
        bankName: String,
        signatureUrl: String,
    ): ApiResult<AgreementInfo> = apiCall(gson) {
        owner.acceptAgreement(AcceptAgreementRequest(version, cccdNo, bankAccount, bankName, signatureUrl))
    }

    suspend fun myCars(): ApiResult<List<Car>> = apiCall(gson) { owner.cars() }

    suspend fun car(carId: Long): ApiResult<Car> = apiCall(gson) { bookings.car(carId) }

    suspend fun createCar(body: CarUpsertRequest): ApiResult<Car> = apiCall(gson) { owner.createCar(body) }

    suspend fun updateCar(carId: Long, body: CarUpsertRequest): ApiResult<Car> =
        apiCall(gson) { owner.updateCar(carId, body) }

    suspend fun blockDays(carId: Long, days: List<LocalDate>): ApiResult<Unit> =
        apiCall(gson) { owner.updateCalendar(carId, CalendarUpdateRequest(days, blocked = true)) }

    suspend fun openDays(carId: Long, days: List<LocalDate>): ApiResult<Unit> =
        apiCall(gson) { owner.updateCalendar(carId, CalendarUpdateRequest(days, blocked = false)) }

    suspend fun availability(carId: Long, from: LocalDate, to: LocalDate): ApiResult<Availability> =
        apiCall(gson) {
            bookings.availability(carId, from.format(ISO), to.format(ISO))
        }

    private companion object {
        val ISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    }
}
