package gtvt.ktpm.thuexe

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import gtvt.ktpm.thuexe.data.model.NewsItem
import gtvt.ktpm.thuexe.data.net.LocalDateAdapter
import gtvt.ktpm.thuexe.data.net.OffsetDateTimeAdapter
import java.time.LocalDate
import java.time.OffsetDateTime
import gtvt.ktpm.thuexe.data.net.BocConverterFactory
import gtvt.ktpm.thuexe.data.net.LoiNghiepVu
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Lớp gỡ bọc nằm trên đường đi của mọi lời gọi, nên sai ở đây là hỏng cả app.
 * Test chạy trên JVM, không cần máy.
 */
class BocPhanHoiTest {

    // Đúng cấu hình Gson mà NetworkModule dựng: thiếu hai bộ chuyển đổi ngày thì
    // Gson không đọc nổi java.time trên JDK 17.
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter)
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter)
        .create()
    private val retrofit = Retrofit.Builder().baseUrl("http://x/").build()
    private val factory = BocConverterFactory(gson)

    private fun <T> doc(type: Type, json: String): T {
        @Suppress("UNCHECKED_CAST")
        val bo = factory.responseBodyConverter(type, emptyArray(), retrofit)
                as Converter<okhttp3.ResponseBody, T>
        return bo.convert(json.toResponseBody("application/json".toMediaType()))!!
    }

    private val kieuDanhSachTin =
        object : com.google.gson.reflect.TypeToken<List<NewsItem>>() {}.type

    @Test
    fun `backend moi - go lop boc lay phan data`() {
        val tin: List<NewsItem> = doc(
            kieuDanhSachTin,
            """{"status":200,"data":[{"id":1,"tag":"MẸO","title":"A","summary":"B"}],
               "code":null,"message":null}"""
        )
        assertEquals(1, tin.size)
        assertEquals("A", tin[0].title)
    }

    @Test
    fun `backend cu - khong co lop boc thi doc thang`() {
        val tin: List<NewsItem> = doc(
            kieuDanhSachTin,
            """[{"id":7,"tag":"TIN","title":"Cũ","summary":"S"}]"""
        )
        assertEquals(7L, tin[0].id)
    }

    @Test
    fun `loi nghiep vu - nem LoiNghiepVu kem ma that`() {
        try {
            doc<List<NewsItem>>(
                kieuDanhSachTin,
                """{"status":422,"data":null,"code":"SLOT_TAKEN","message":"Ngày này có người đặt mất rồi"}"""
            )
            fail("Phải ném LoiNghiepVu")
        } catch (e: LoiNghiepVu) {
            assertEquals("SLOT_TAKEN", e.code)
            assertEquals(422, e.httpStatus)
            assertEquals("Ngày này có người đặt mất rồi", e.thongDiep)
        }
    }

    @Test
    fun `object thuong co truong status khong bi nham la lop boc`() {
        // Booking, Car, Payment đều có trường "status" riêng. Thiếu đủ bốn trường
        // thì không được coi là lớp bọc, nếu không sẽ nuốt mất cả đối tượng.
        val tin: NewsItem = doc(
            NewsItem::class.java,
            """{"id":3,"tag":"T","title":"Có status","summary":"S","status":"DANG_BAN"}"""
        )
        assertEquals("Có status", tin.title)
    }

    @Test
    fun `data rong tra ve null`() {
        val tin: List<NewsItem>? = runCatching {
            doc<List<NewsItem>>(
                kieuDanhSachTin,
                """{"status":200,"data":null,"code":null,"message":null}"""
            )
        }.getOrNull()
        assertNull(tin)
    }
}
