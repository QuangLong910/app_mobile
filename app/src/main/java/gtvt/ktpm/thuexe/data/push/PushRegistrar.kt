package gtvt.ktpm.thuexe.data.push

import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.AuthApi
import gtvt.ktpm.thuexe.data.model.DeviceTokenRequest
import gtvt.ktpm.thuexe.data.net.apiCall
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.withLock

/** Đăng ký / huỷ mã thiết bị FCM với backend. Lỗi ở đây không được làm hỏng luồng đăng nhập. */
@Singleton
class PushRegistrar @Inject constructor(
    private val api: AuthApi,
    private val gson: Gson,
) {
    private var daDangKy: String? = null
    private val khoa = kotlinx.coroutines.sync.Mutex()

    private suspend fun layToken(): String? = suspendCancellableCoroutine { k ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { k.resume(it) }
            .addOnFailureListener { k.resume(null) }
    }

    /** Gọi khi đã có phiên đăng nhập. Cùng một token thì chỉ gửi một lần mỗi lần chạy app. */
    suspend fun dangKy(token: String? = null) {
        val t = token ?: layToken() ?: return
        // Đổi màn liên tục bắn nhiều lượt cùng lúc: xếp hàng để chỉ lượt đầu gửi thật.
        khoa.withLock {
            if (t == daDangKy) return
            if (apiCall(gson) { api.registerDevice(DeviceTokenRequest(t)) } is gtvt.ktpm.thuexe.data.net.ApiResult.Ok)
                daDangKy = t
        }
    }

    /** [phien] là token đăng nhập vừa bị xoá, cần gắn tay vào lời gọi. */
    suspend fun huy(phien: String) {
        val t = layToken() ?: return
        apiCall(gson) { api.unregisterDevice("Bearer $phien", t) }
        daDangKy = null
    }
}
