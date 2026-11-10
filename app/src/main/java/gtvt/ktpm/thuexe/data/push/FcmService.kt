package gtvt.ktpm.thuexe.data.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import gtvt.ktpm.thuexe.MainActivity
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.data.net.TokenStore
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

const val KENH_THONG_BAO = "thongbao"

fun taoKenhThongBao(context: Context) {
    // Kênh thông báo chỉ có từ Android 8; máy cũ hơn (minSdk 24) không cần và gọi vào sẽ sập.
    if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.O) return
    val nm = context.getSystemService(NotificationManager::class.java)
    nm.createNotificationChannel(
        NotificationChannel(KENH_THONG_BAO, "Thông báo đơn thuê", NotificationManager.IMPORTANCE_HIGH)
    )
}

@AndroidEntryPoint
class FcmService : FirebaseMessagingService() {
    @Inject lateinit var tokenStore: TokenStore
    @Inject lateinit var registrar: PushRegistrar

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        // Chưa đăng nhập thì thôi; lúc đăng nhập app sẽ tự lấy token và gửi.
        if (!tokenStore.token.isNullOrBlank()) scope.launch { registrar.dangKy(token) }
    }

    /** App đang mở thì hệ thống không tự hiện thông báo, nên tự dựng. */
    override fun onMessageReceived(message: RemoteMessage) {
        TinDay.baoCoTin()
        val tieuDe = message.notification?.title ?: message.data["title"] ?: return
        val noiDung = message.notification?.body ?: message.data["body"] ?: ""
        val maDon = message.data["bookingCode"]
        taoKenhThongBao(this)

        // requestCode theo mã đơn để hai thông báo của hai đơn khác nhau không đè PendingIntent
        // của nhau; không có mã đơn thì dùng 0 như trước.
        val mo = PendingIntent.getActivity(
            this, maDon?.hashCode() ?: 0,
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .putExtra(MainActivity.EXTRA_MA_DON, maDon),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val tb = NotificationCompat.Builder(this, KENH_THONG_BAO)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(tieuDe)
            .setContentText(noiDung)
            .setStyle(NotificationCompat.BigTextStyle().bigText(noiDung))
            .setAutoCancel(true)
            .setContentIntent(mo)
            .build()
        try {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), tb)
        } catch (_: SecurityException) {
            // Chưa được cấp quyền thông báo (Android 13+): bỏ qua.
        }
    }
}
