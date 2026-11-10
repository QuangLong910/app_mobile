package gtvt.ktpm.thuexe.ui.widget.form

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import java.io.ByteArrayOutputStream

/** Các nét đã vẽ. Giữ toạ độ thô để vừa vẽ lên màn vừa kết xuất ra PNG từ cùng một nguồn. */
class ChuKyState {
    val net = mutableStateListOf<List<Offset>>()
    var dangVe by mutableStateOf<List<Offset>>(emptyList())
    var khung by mutableStateOf(IntSize.Zero)

    val coNet: Boolean get() = net.isNotEmpty() || dangVe.size > 1

    fun xoa() {
        net.clear()
        dangVe = emptyList()
    }

    /** Nền trắng, nét đen — gửi lên backend như một ảnh bình thường qua POST /files. */
    fun thanhPng(): ByteArray? {
        if (!coNet || khung.width <= 0) return null
        val bmp = Bitmap.createBitmap(khung.width, khung.height, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bmp)
        canvas.drawColor(android.graphics.Color.WHITE)
        val but = Paint().apply {
            color = android.graphics.Color.BLACK
            strokeWidth = khung.height * 0.018f
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            isAntiAlias = true
        }
        (net + listOf(dangVe)).forEach { diem ->
            if (diem.size < 2) return@forEach
            val duong = android.graphics.Path()
            duong.moveTo(diem[0].x, diem[0].y)
            diem.drop(1).forEach { duong.lineTo(it.x, it.y) }
            canvas.drawPath(duong, but)
        }
        return ByteArrayOutputStream().use {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, it)
            bmp.recycle()
            it.toByteArray()
        }
    }
}

@Composable
fun rememberChuKyState(): ChuKyState = remember { ChuKyState() }

/**
 * Ô ký tay. Ký xong bấm "Xong" là kết xuất PNG và trả ra ngoài để tải lên;
 * đã có chữ ký rồi thì hiện lại ảnh kèm nút ký lại.
 */
@Composable
fun ChuKyField(
    label: String,
    daKyUrl: String?,
    onXongKy: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    hint: String? = null,
    enabled: Boolean = true,
    /** Đang đẩy chữ ký lên máy chủ. Vẽ xong chưa phải là lưu xong. */
    dangTaiLen: Boolean = false,
    /** Lưu hỏng. Phải nói ra, không thì người ký thấy chữ ký mà nút Ký vẫn khoá. */
    loiTaiLen: String? = null,
) {
    val state = rememberChuKyState()
    var kyLai by remember { mutableStateOf(false) }

    // Giữ chính tấm vừa ký trong bộ nhớ để xem lại ngay. Trước đây field tải lại ảnh
    // từ máy chủ, nên mất mạng hay sai đường dẫn là ô xem trước trắng trơn — người ký
    // không biết chữ ký của mình có được ghi hay không.
    var anhVuaKy by remember { mutableStateOf<ImageBitmap?>(null) }

    FormField(label, modifier, required, error, hint) {
        val cucBo = anhVuaKy
        if ((daKyUrl != null || cucBo != null) && !kyLai) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val khungAnh = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f / 2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, Color111245, RoundedCornerShape(10.dp))

                if (cucBo != null) {
                    // Vừa ký xong: vẽ lại đúng tấm đang cầm, không đi vòng qua mạng.
                    Image(
                        bitmap = cucBo,
                        contentDescription = label,
                        modifier = khungAnh,
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    // Mở lại biên bản đã ký từ trước: ảnh chỉ có trên máy chủ.
                    AsyncImage(
                        model = daKyUrl.urlAnh(),
                        contentDescription = label,
                        modifier = khungAnh,
                        contentScale = ContentScale.Fit,
                    )
                }
                TrangThaiLuuChuKy(dangTaiLen, loiTaiLen)
                TextButton(
                    onClick = { state.xoa(); anhVuaKy = null; kyLai = true },
                    modifier = Modifier.align(Alignment.End),
                    enabled = enabled,
                ) {
                    Text("Ký lại", style = Chu.Than.copy(color = Color78799C))
                }
            }
            return@FormField
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(5f / 2f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .border(1.dp, if (state.coNet) Color111245 else ColorCFD0E4, RoundedCornerShape(10.dp))
                    .onSizeChanged { state.khung = it }
                    .pointerInput(enabled) {
                        if (!enabled) return@pointerInput
                        detectDragGestures(
                            onDragStart = { state.dangVe = listOf(it) },
                            onDrag = { change, _ ->
                                state.dangVe = state.dangVe + change.position
                                change.consume()
                            },
                            onDragEnd = {
                                if (state.dangVe.size > 1) state.net.add(state.dangVe)
                                state.dangVe = emptyList()
                            },
                            onDragCancel = { state.dangVe = emptyList() },
                        )
                    },
                contentAlignment = Alignment.Center,
            ) {
                if (!state.coNet) {
                    Text(
                        text = "Ký bằng ngón tay vào khung này",
                        style = Chu.Than.copy(color = Color78799C),
                    )
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val doDay = size.height * 0.018f
                    (state.net + listOf(state.dangVe)).forEach { diem ->
                        if (diem.size < 2) return@forEach
                        val duong = Path().apply {
                            moveTo(diem[0].x, diem[0].y)
                            diem.drop(1).forEach { lineTo(it.x, it.y) }
                        }
                        drawPath(
                            path = duong,
                            color = Color111245,
                            style = Stroke(width = doDay, join = StrokeJoin.Round),
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                TextButton(onClick = { state.xoa() }, enabled = enabled && state.coNet) {
                    Text("Xoá", style = Chu.Than.copy(color = ColorD54646))
                }
                TextButton(
                    onClick = {
                        state.thanhPng()?.let { png ->
                            anhVuaKy = BitmapFactory
                                .decodeByteArray(png, 0, png.size)
                                ?.asImageBitmap()
                            onXongKy(png)
                            kyLai = false
                        }
                    },
                    enabled = enabled && state.coNet,
                ) {
                    Text(
                        text = "Xong",
                        style = Chu.Than.copy(
                            color = if (state.coNet) Color111245 else Color78799C,
                        ),
                    )
                }
            }
        }
    }
}

/** Một dòng nhỏ dưới ảnh xem trước: đang lưu, đã lưu, hay lưu hỏng. */
@Composable
private fun TrangThaiLuuChuKy(dangTaiLen: Boolean, loiTaiLen: String?) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (dangTaiLen) {
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 2.dp,
                color = Color78799C,
            )
        }
        Text(
            text = when {
                dangTaiLen -> "  Đang lưu chữ ký…"
                loiTaiLen != null -> "Chưa lưu được: $loiTaiLen — bấm Ký lại để thử lần nữa"
                else -> "Đã lưu chữ ký"
            },
            style = Chu.Phu.copy(
                color = if (loiTaiLen != null) ColorD54646 else Color78799C,
            ),
        )
    }
}
