package gtvt.ktpm.thuexe.data.net

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

/**
 * Lỗi nghiệp vụ đọc ra từ lớp bọc. Backend trả HTTP 200 cho cả lỗi, mã thật nằm trong
 * thân, nên Retrofit không ném HttpException — phải tự ném từ đây.
 *
 * Kế thừa IOException để Retrofit coi là lỗi đọc thân bình thường; apiCall bắt kiểu này
 * TRƯỚC IOException, nếu không sẽ bị quy nhầm thành lỗi mạng.
 */
class LoiNghiepVu(
    val code: String,
    val thongDiep: String?,
    val httpStatus: Int,
) : IOException("$code: ${thongDiep ?: ""}")

/**
 * Gỡ lớp bọc { status, data, code, message } trước khi giao cho Gson.
 *
 * Chịu được cả hai đời backend: bản mới bọc, bản cũ trả thẳng. Nhờ vậy đổi địa chỉ
 * backend không phải dựng lại app — trên máy dùng bản nào cũng chạy.
 */
class BocConverterFactory(private val gson: Gson) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> {
        val boChuyen = gson.getAdapter(TypeToken.get(type))
        val laUnit = type == Unit::class.java || type == Void::class.java

        return Converter<ResponseBody, Any?> { body ->
            body.use { than ->
                val chuoi = than.string()
                if (chuoi.isBlank()) return@Converter if (laUnit) Unit else null

                val phan = goiBenTrong(JsonParser.parseString(chuoi))
                when {
                    laUnit -> Unit
                    phan == null || phan.isJsonNull -> null
                    else -> boChuyen.fromJsonTree(phan)
                }
            }
        }
    }

    /** Thân yêu cầu vẫn để Gson lo, không đụng vào. */
    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<*, RequestBody>? = null

    /**
     * Nhận ra lớp bọc bằng cách đòi đủ CẢ BỐN trường. Chỉ đòi "status" thì nhầm ngay:
     * Booking, Car, Payment đều có trường status của riêng chúng.
     */
    private fun goiBenTrong(goc: JsonElement): JsonElement? {
        if (!goc.isJsonObject) return goc
        val o = goc.asJsonObject
        val laBoc = o.has("status") && o.has("data") && o.has("code") && o.has("message")
        if (!laBoc) return goc

        val status = runCatching { o.get("status").asInt }.getOrDefault(200)
        if (status !in 200..299) {
            throw LoiNghiepVu(
                code = o.get("code").takeIf { !it.isJsonNull }?.asString ?: ErrorCode.UNKNOWN,
                thongDiep = o.get("message").takeIf { !it.isJsonNull }?.asString,
                httpStatus = status,
            )
        }
        return o.get("data")
    }
}
