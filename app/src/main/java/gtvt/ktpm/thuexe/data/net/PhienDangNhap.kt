package gtvt.ktpm.thuexe.data.net

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Loa báo toàn app: token hết hạn hoặc bị từ chối. apiCall bắn vào đây, MyApp nghe và
 * đưa người dùng về màn đăng nhập — không màn nào phải tự xử lý 401.
 */
object PhienDangNhap {
    private val _hetHan = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val hetHan: SharedFlow<Unit> = _hetHan.asSharedFlow()

    fun baoHetHan() {
        _hetHan.tryEmit(Unit)
    }
}
