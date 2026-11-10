package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle

/**
 * Tải lại mỗi khi màn quay lại tiền cảnh: đổi sang app khác rồi quay về, hoặc thoát
 * màn con trở lại đây. Nhờ vậy bấm nút ở màn con xong là màn này có dữ liệu mới,
 * không phải tự truyền tín hiệu ngược qua NavController.
 */
@Composable
fun TaiLaiKhiQuayLai(khoa: Any?, tai: () -> Unit) {
    val taiHienTai by rememberUpdatedState(tai)
    LifecycleResumeEffect(khoa) {
        taiHienTai()
        onPauseOrDispose { }
    }
}

/**
 * Tải lại khi có thông báo đẩy tới lúc màn đang hiện (bên kia vừa nhận đơn, trả tiền, ký biên bản…).
 * Không hỏi định kỳ nên không tốn pin hay dữ liệu; app ở nền thì đã có TaiLaiKhiQuayLai lo khi mở lại.
 */
@Composable
fun TaiLaiKhiCoTinDay(tai: () -> Unit) {
    val taiHienTai by rememberUpdatedState(tai)
    val chuVongDoi = LocalLifecycleOwner.current
    LaunchedEffect(chuVongDoi) {
        chuVongDoi.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            gtvt.ktpm.thuexe.data.push.TinDay.moi.collect { taiHienTai() }
        }
    }
}
