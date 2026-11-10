package gtvt.ktpm.thuexe

import com.google.gson.GsonBuilder
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.net.LocalDateAdapter
import gtvt.ktpm.thuexe.data.net.OffsetDateTimeAdapter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.OffsetDateTime

/** Hai quy ước dễ vỡ nhất trên dây: ngày thuê và tiền. */
class JsonContractTest {

    private val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter)
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter)
        .create()

    @Test
    fun `ngay thue la chuoi yyyy-MM-dd, khong lech mui gio`() {
        val json = """
            {"id":1,"code":"KNM4F2C9","carId":7,"startDate":"2026-09-12","endDate":"2026-09-15",
             "days":3,"pricePerDay":600000,"rentTotal":1800000,"deposit":3000000,
             "commission":270000,"status":"CHO_CHU_XE"}
        """.trimIndent()

        val booking = gson.fromJson(json, Booking::class.java)

        assertEquals(LocalDate.of(2026, 9, 12), booking.startDate)
        assertEquals(LocalDate.of(2026, 9, 15), booking.endDate)
        assertTrue(gson.toJson(booking).contains(""""startDate":"2026-09-12""""))
    }

    @Test
    fun `tien la so nguyen dong, khong ra so thap phan`() {
        val json = """
            {"id":1,"code":"KNM4F2C9","carId":7,"startDate":"2026-09-12","endDate":"2026-09-15",
             "days":3,"pricePerDay":600000,"rentTotal":1800000,"deposit":3000000,
             "commission":270000,"status":"CHO_CHU_XE"}
        """.trimIndent()

        val booking = gson.fromJson(json, Booking::class.java)

        assertEquals(1_800_000L, booking.rentTotal)
        assertEquals(270_000L, booking.commission)
        assertTrue(gson.toJson(booking).contains(""""rentTotal":1800000"""))
    }

    @Test
    fun `moc he thong giu nguyen mui gio`() {
        val value = gson.fromJson("\"2026-09-12T10:30:00+07:00\"", OffsetDateTime::class.java)
        assertEquals("\"2026-09-12T10:30:00+07:00\"", gson.toJson(value))
    }
}
