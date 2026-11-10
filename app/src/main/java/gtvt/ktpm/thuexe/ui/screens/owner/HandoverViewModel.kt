package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.HandoverPhotoRequest
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.data.repo.FileRepository
import gtvt.ktpm.thuexe.data.repo.HandoverRepository
import gtvt.ktpm.thuexe.data.repo.OwnerBookingRepository
import gtvt.ktpm.thuexe.ui.widget.form.nhanLoi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HandoverState(
    val loading: Boolean = false,
    val handover: Handover? = null,
    /** Ảnh đã tải lên, khoá ảnh là slot. */
    val photos: Map<String, String> = emptyMap(),
    /** Nút ký chỉ bật sau khi người soi đã xem hết 6 tấm (§05.6). */
    val seenSlots: Set<String> = emptySet(),
    val signatureUrl: String? = null,
    /** Chữ ký đang tải lên. Ảnh xem trước hiện ngay, nhưng chưa lưu xong thì chưa ký được. */
    val dangTaiChuKy: Boolean = false,
    /** Tải chữ ký hỏng. Tách khỏi errorCode để không che mất lỗi của cả màn. */
    val loiChuKy: String? = null,
    /** Ký hoặc phản đối đã xong ở backend — lúc này mới được rời màn. */
    val daHoanTat: Boolean = false,
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val thieuAnh: List<String> get() = PhotoSlot.BAT_BUOC.filterNot { it in photos }
    val duocKy: Boolean get() = PhotoSlot.BAT_BUOC.all { it in seenSlots }

    /** Có chữ ký đã lưu xong trên máy chủ, không phải chỉ vẽ xong trên màn. */
    val chuKyDaLuu: Boolean get() = signatureUrl != null && !dangTaiChuKy
}

@HiltViewModel
class HandoverViewModel @Inject constructor(
    private val handovers: HandoverRepository,
    private val files: FileRepository,
    private val bookings: OwnerBookingRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(HandoverState())
    val state: StateFlow<HandoverState> = _state.asStateFlow()

    /**
     * Mở biên bản đã có để soi. Lấy qua GET /bookings/{id} vì backend không có
     * đường dẫn đọc riêng một biên bản.
     */
    fun taiBienBan(maDon: String, kind: String = HandoverKind.TRA) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = bookings.detail(maDon)) {
            is ApiResult.Ok -> {
                val bb = r.data.handovers.firstOrNull { it.kind == kind }
                _state.update {
                    it.copy(
                        loading = false,
                        handover = bb,
                        photos = bb?.photos.orEmpty().associate { a -> a.slot to a.url },
                        errorCode = if (bb == null) "KHONG_CO_BIEN_BAN" else null,
                        errorMessage = if (bb == null) "Đơn này chưa có biên bản $kind" else null,
                    )
                }
            }
            is ApiResult.Err -> fail(r)
        }
    }

    /**
     * Chụp bổ sung một tấm rồi gắn luôn vào biên bản. Tách khỏi addPhoto vì ảnh bổ sung
     * phải đi qua POST /handovers/{id}/photos, không nằm trong lần tạo biên bản.
     */
    fun boSungMotAnh(handoverId: Long, bytes: ByteArray, note: String? = null) =
        viewModelScope.launch {
            _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
            when (val up = files.upload(bytes, "bo-sung.jpg")) {
                is ApiResult.Err -> fail(up)
                is ApiResult.Ok -> {
                    val them = listOf(HandoverPhotoRequest(PhotoSlot.KHAC, up.data, note))
                    when (val r = handovers.addPhotos(handoverId, them)) {
                        is ApiResult.Ok -> _state.update { it.copy(loading = false, handover = r.data) }
                        is ApiResult.Err -> fail(r)
                    }
                }
            }
        }

    /** Ảnh đã nén cạnh dài 1600px trước khi vào đây. */
    fun addPhoto(slot: String, bytes: ByteArray) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = files.upload(bytes, "$slot.jpg")) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, photos = it.photos + (slot to r.data)) }
            is ApiResult.Err -> fail(r)
        }
    }

    /**
     * Vẽ xong là ảnh xem trước hiện ngay, nhưng chữ ký chỉ có giá trị khi đã nằm trên
     * máy chủ. Trạng thái tải lên phải hiện ra, nếu không người ký thấy chữ ký của mình
     * mà nút Ký vẫn khoá và không hiểu vì sao.
     */
    fun addSignature(bytes: ByteArray) = viewModelScope.launch {
        _state.update { it.copy(dangTaiChuKy = true, loiChuKy = null, signatureUrl = null) }
        when (val r = files.upload(bytes, "chu-ky.jpg")) {
            is ApiResult.Ok -> _state.update {
                it.copy(dangTaiChuKy = false, signatureUrl = r.data)
            }
            is ApiResult.Err -> _state.update {
                it.copy(dangTaiChuKy = false, loiChuKy = nhanLoi(r.code, r.message))
            }
        }
    }

    fun markSeen(slot: String) = _state.update { it.copy(seenSlots = it.seenSlots + slot) }

    /** Chủ xe lập biên bản GIAO → đơn vào CHO_SOI, chờ khách soi ảnh rồi ký. */
    fun lapBienBanGiao(maDon: String, odo: Int, fuelLevel: Int, signatureUrl: String, note: String? = null) =
        lapBienBan(HandoverKind.GIAO, maDon, odo, fuelLevel, signatureUrl, note)

    /** Chủ xe lập GIAO, khách lập TRA: cùng một màn, khác loại. */
    fun lapBienBan(
        kind: String, maDon: String, odo: Int, fuelLevel: Int, signatureUrl: String, note: String? = null,
    ) =
        viewModelScope.launch {
            val photos = _state.value.photos.map { (slot, url) -> HandoverPhotoRequest(slot, url) }
            _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
            when (val r = handovers.create(kind, maDon, odo, fuelLevel, signatureUrl, photos, note)) {
                is ApiResult.Ok -> _state.update {
                    it.copy(loading = false, handover = r.data, daHoanTat = true)
                }
                is ApiResult.Err -> fail(r)
            }
        }

    /** Chủ xe soi biên bản TRA: chụp bổ sung hư hỏng mới trước khi ký. */
    fun boSungAnh(handoverId: Long, photos: List<HandoverPhotoRequest>) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = handovers.addPhotos(handoverId, photos)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, handover = r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    fun ky(handoverId: Long, signatureUrl: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = handovers.sign(handoverId, signatureUrl)) {
            is ApiResult.Ok -> _state.update {
                it.copy(loading = false, handover = r.data, daHoanTat = true)
            }
            is ApiResult.Err -> fail(r)
        }
    }

    fun phanDoi(handoverId: Long, objection: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = handovers.object_(handoverId, objection)) {
            is ApiResult.Ok -> _state.update {
                it.copy(loading = false, handover = r.data, daHoanTat = true)
            }
            is ApiResult.Err -> fail(r)
        }
    }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(loading = false, errorCode = err.code, errorMessage = err.message)
    }
}
