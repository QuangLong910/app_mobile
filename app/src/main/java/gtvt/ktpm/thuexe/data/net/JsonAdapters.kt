package gtvt.ktpm.thuexe.data.net

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/** Ngày thuê đi trên dây dưới dạng "2026-09-12" — không bao giờ là mốc thời gian. */
object LocalDateAdapter : TypeAdapter<LocalDate>() {
    override fun write(out: JsonWriter, value: LocalDate?) {
        if (value == null) out.nullValue() else out.value(value.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }

    override fun read(reader: JsonReader): LocalDate? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return LocalDate.parse(reader.nextString())
    }
}

/** Mốc hệ thống: chuỗi ISO có múi giờ. */
object OffsetDateTimeAdapter : TypeAdapter<OffsetDateTime>() {
    override fun write(out: JsonWriter, value: OffsetDateTime?) {
        if (value == null) out.nullValue() else out.value(value.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    }

    override fun read(reader: JsonReader): OffsetDateTime? {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull()
            return null
        }
        return OffsetDateTime.parse(reader.nextString())
    }
}
