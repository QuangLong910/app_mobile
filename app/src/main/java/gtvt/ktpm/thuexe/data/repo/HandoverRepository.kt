package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.BookingApi
import gtvt.ktpm.thuexe.data.api.HandoverApi
import gtvt.ktpm.thuexe.data.model.AddHandoverPhotosRequest
import gtvt.ktpm.thuexe.data.model.CreateHandoverRequest
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.data.model.HandoverPhotoRequest
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.data.model.ReviewHandoverRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.ErrorCode
import gtvt.ktpm.thuexe.data.net.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandoverRepository @Inject constructor(
    private val bookings: BookingApi,
    private val handovers: HandoverApi,
    private val gson: Gson,
) {
    /**
     * Chủ xe lập biên bản GIAO. Đơn vào CHO_SOI, khách soi ảnh rồi ký thì mới sang DANG_THUE.
     * Chặn sớm ở app cho đỡ một vòng mạng; backend vẫn kiểm lại.
     */
    suspend fun createGiao(
        maDon: String,
        odo: Int,
        fuelLevel: Int,
        signatureUrl: String,
        photos: List<HandoverPhotoRequest>,
        note: String? = null,
    ): ApiResult<Handover> = create(HandoverKind.GIAO, maDon, odo, fuelLevel, signatureUrl, photos, note)

    /** Khách lập biên bản TRA khi đơn đang DANG_THUE; chủ xe soi và ký. */
    suspend fun createTra(
        maDon: String,
        odo: Int,
        fuelLevel: Int,
        signatureUrl: String,
        photos: List<HandoverPhotoRequest>,
        note: String? = null,
    ): ApiResult<Handover> = create(HandoverKind.TRA, maDon, odo, fuelLevel, signatureUrl, photos, note)

    suspend fun create(
        kind: String,
        maDon: String,
        odo: Int,
        fuelLevel: Int,
        signatureUrl: String,
        photos: List<HandoverPhotoRequest>,
        note: String? = null,
    ): ApiResult<Handover> {
        missingSlots(photos)?.let { return it }
        return apiCall(gson) {
            bookings.createHandover(
                maDon,
                CreateHandoverRequest(kind, odo, fuelLevel, note, signatureUrl, photos)
            )
        }
    }

    /** Chủ xe soi biên bản TRA: chụp bổ sung hư hỏng mới trước khi ký. */
    suspend fun addPhotos(handoverId: Long, photos: List<HandoverPhotoRequest>): ApiResult<Handover> =
        apiCall(gson) { handovers.addPhotos(handoverId, AddHandoverPhotosRequest(photos)) }

    /** Chủ xe ký biên bản TRA → đơn sang CHO_QUYET_TOAN. */
    suspend fun sign(handoverId: Long, signatureUrl: String): ApiResult<Handover> =
        apiCall(gson) { handovers.review(handoverId, ReviewHandoverRequest(agreed = true, signature = signatureUrl)) }

    /** Phản đối: đơn dừng lại, người vận hành xử (E9). */
    suspend fun object_(handoverId: Long, objection: String): ApiResult<Handover> =
        apiCall(gson) { handovers.review(handoverId, ReviewHandoverRequest(agreed = false, objection = objection)) }

    private fun missingSlots(photos: List<HandoverPhotoRequest>): ApiResult.Err? {
        val have = photos.map { it.slot }.toSet()
        val missing = PhotoSlot.BAT_BUOC.filterNot { it in have }
        return if (missing.isEmpty()) null
        else ApiResult.Err(ErrorCode.HANDOVER_INCOMPLETE, "Còn thiếu ảnh: ${missing.joinToString()}")
    }
}
