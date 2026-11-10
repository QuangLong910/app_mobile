package gtvt.ktpm.thuexe.data.push

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Có thông báo đẩy vừa tới khi app đang mở: các màn đang hiện tải lại dữ liệu của mình.
 * Thay cho việc hỏi backend theo chu kỳ.
 */
object TinDay {
    private val _moi = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val moi: SharedFlow<Unit> = _moi.asSharedFlow()

    fun baoCoTin() {
        _moi.tryEmit(Unit)
    }
}
