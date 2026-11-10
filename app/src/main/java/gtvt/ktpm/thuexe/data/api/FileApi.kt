package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.UploadedFile
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {

    /** Một điểm cuối cho mọi loại ảnh: ảnh xe, giấy tờ, ảnh biên bản, chữ ký. */
    @Multipart
    @POST("files")
    suspend fun upload(@Part file: MultipartBody.Part): UploadedFile
}
