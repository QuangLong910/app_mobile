package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.AddHandoverPhotosRequest
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.ReviewHandoverRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface HandoverApi {

    /** Bên soi bổ sung ảnh vết mà bên lập chưa ghi — chủ xe soi biên bản TRA. */
    @POST("handovers/{id}/photos")
    suspend fun addPhotos(@Path("id") handoverId: Long, @Body body: AddHandoverPhotosRequest): Handover

    /** Bên soi ký hoặc phản đối. Ký thì đơn mới đổi trạng thái. */
    @POST("handovers/{id}/review")
    suspend fun review(@Path("id") handoverId: Long, @Body body: ReviewHandoverRequest): Handover
}
