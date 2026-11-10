package gtvt.ktpm.thuexe.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max

/**
 * Ảnh 12 MP gửi thẳng lên là yêu cầu chết. Hạ cạnh dài về 1600px, JPEG 80% rồi mới gửi.
 * Backend đặt max-file-size 8MB, sau khi nén thì không đời nào chạm trần đó.
 */
object AnhNen {

    private const val CANH_DAI_TOI_DA = 1600
    private const val CHAT_LUONG = 80

    suspend fun tuUri(context: Context, uri: Uri): ByteArray? = withContext(Dispatchers.IO) {
        val goc = doc(context, uri) ?: return@withContext null
        val xoay = huong(context, uri)
        val nho = thuNho(goc)
        val cuoi = if (xoay == 0f) nho else xoayAnh(nho, xoay)
        ByteArrayOutputStream().use { out ->
            cuoi.compress(Bitmap.CompressFormat.JPEG, CHAT_LUONG, out)
            cuoi.recycle()
            out.toByteArray()
        }
    }

    /** Đọc kích thước trước để inSampleSize, tránh nạp nguyên tấm 12 MP vào bộ nhớ. */
    private fun doc(context: Context, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        if (bounds.outWidth <= 0) return null

        val canhDai = max(bounds.outWidth, bounds.outHeight)
        var mau = 1
        while (canhDai / mau > CANH_DAI_TOI_DA * 2) mau *= 2

        val opts = BitmapFactory.Options().apply { inSampleSize = mau }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts)
        }
    }

    private fun thuNho(src: Bitmap): Bitmap {
        val canhDai = max(src.width, src.height)
        if (canhDai <= CANH_DAI_TOI_DA) return src
        val ti = CANH_DAI_TOI_DA.toFloat() / canhDai
        val ra = Bitmap.createScaledBitmap(src, (src.width * ti).toInt(), (src.height * ti).toInt(), true)
        if (ra != src) src.recycle()
        return ra
    }

    /** Ảnh chụp dọc bằng CameraX hay bị nằm ngang nếu bỏ qua EXIF. */
    private fun huong(context: Context, uri: Uri): Float =
        context.contentResolver.openInputStream(uri)?.use { input ->
            when (ExifInterface(input).getAttributeInt(
                ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
            )) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                else -> 0f
            }
        } ?: 0f

    private fun xoayAnh(src: Bitmap, goc: Float): Bitmap {
        val m = Matrix().apply { postRotate(goc) }
        val ra = Bitmap.createBitmap(src, 0, 0, src.width, src.height, m, true)
        if (ra != src) src.recycle()
        return ra
    }
}
