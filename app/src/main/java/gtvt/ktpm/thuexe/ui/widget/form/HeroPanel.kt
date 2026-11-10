package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.Radius

/**
 * Khối mở đầu cho màn "một thứ có ảnh làm trung tâm" (chi tiết xe, chi tiết chuyến): ảnh
 * tràn hết chiều ngang, phía dưới là một panel bo góc đè nhẹ lên mép ảnh chứa tiêu đề và
 * (tuỳ chọn) một dòng phụ. Nhiều ảnh thì vuốt ngang, có chấm trang ở dưới.
 *
 * Đừng dùng cho màn danh sách hay biểu mẫu thuần — chỉ dùng khi ảnh THỰC SỰ là thứ người
 * dùng cần thấy đầu tiên (một chiếc xe cụ thể), không phải trang trí.
 */
@Composable
fun HeroPhotoPanel(
    photos: List<String>,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    val pager = rememberPagerState(pageCount = { photos.size.coerceAtLeast(1) })
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (photos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .background(ColorEAECF3),
                )
            } else {
                HorizontalPager(state = pager, modifier = Modifier.fillMaxWidth()) { trang ->
                    AsyncImage(
                        model = photos[trang].urlAnh(),
                        contentDescription = title,
                        modifier = Modifier.fillMaxWidth().aspectRatio(4f / 3f),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            if (onBack != null) {
                NutTronDo(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    moTa = "Quay lại",
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart),
                )
            }
            if (photos.size > 1) {
                Row(
                    modifier = Modifier.padding(bottom = 10.dp).align(Alignment.BottomCenter),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    repeat(photos.size) { i ->
                        Box(
                            modifier = Modifier
                                .size(if (i == pager.currentPage) 7.dp else 5.dp)
                                .background(Color.White, CircleShape),
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-16).dp)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
                .padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = Chu.TieuDeMan.copy(color = Color111245))
                    subtitle?.let {
                        Text(
                            it, modifier = Modifier.padding(top = 2.dp),
                            style = Chu.Than.copy(color = Color78799C),
                        )
                    }
                }
                trailing?.invoke()
            }
            content()
        }
    }
}

/** Nút tròn nền trắng mờ, dùng đè lên ảnh (nút quay lại) hoặc để gọi/nhắn ở panel dưới ảnh. */
@Composable
fun NutTronDo(
    icon: ImageVector,
    moTa: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    mau: Color = Color111245,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(38.dp)
            .background(Color.White.copy(alpha = 0.92f), CircleShape),
    ) {
        Icon(icon, moTa, tint = mau, modifier = Modifier.size(19.dp))
    }
}

/** Một ô trong lưới thông số: icon tròn nền nhạt, nhãn phụ, giá trị đậm — 3 ô một hàng. */
@Composable
fun OThongSo(icon: ImageVector, nhan: String, gtri: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ColorEAECF3, RoundedCornerShape(Radius.R2.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = gtvt.ktpm.thuexe.ui.theme.Primary, modifier = Modifier.size(19.dp))
        }
        Text(
            nhan, modifier = Modifier.padding(top = 8.dp),
            style = Chu.Phu.copy(color = Color78799C),
        )
        Text(gtri, style = Chu.ThanDam.copy(color = Color111245))
    }
}

/** Lưới thông số 3 cột, tự xuống hàng. Dùng thay cho danh sách InfoRow khi có từ 3 giá trị ngắn trở lên. */
@Composable
fun LuoiThongSo(cac: List<Triple<ImageVector, String, String>>, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        cac.chunked(3).forEach { hang ->
            Row(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                hang.forEach { (icon, nhan, gtri) ->
                    OThongSo(icon, nhan, gtri, modifier = Modifier.weight(1f))
                }
                repeat(3 - hang.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}
