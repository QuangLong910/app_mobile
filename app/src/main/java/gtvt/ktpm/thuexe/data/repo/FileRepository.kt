package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.FileApi
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.apiCall
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileRepository @Inject constructor(
    private val api: FileApi,
    private val gson: Gson,
) {
    /** Trả về url backend cấp; mọi nơi khác chỉ truyền url này đi. */
    suspend fun upload(file: File, mimeType: String = "image/jpeg"): ApiResult<String> = apiCall(gson) {
        val part = MultipartBody.Part.createFormData(
            "file", file.name, file.asRequestBody(mimeType.toMediaType())
        )
        api.upload(part).url
    }

    /** Ảnh đã nén sẵn trong bộ nhớ — cạnh dài 1600px, JPEG 80% (§08). */
    suspend fun upload(bytes: ByteArray, fileName: String, mimeType: String = "image/jpeg"): ApiResult<String> =
        apiCall(gson) {
            val part = MultipartBody.Part.createFormData(
                "file", fileName, bytes.toRequestBody(mimeType.toMediaType())
            )
            api.upload(part).url
        }
}
