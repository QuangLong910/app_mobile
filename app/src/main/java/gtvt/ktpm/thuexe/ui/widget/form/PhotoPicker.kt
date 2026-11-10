package gtvt.ktpm.thuexe.ui.widget.form

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.data.AnhNen
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Một ô ảnh: chạm là mở thư viện, ảnh được nén ngay tại đây rồi mới trả ByteArray ra ngoài.
 * Chỗ gọi chỉ việc đẩy lên POST /files.
 */
@Composable
fun PhotoSlotBox(
    label: String,
    url: String?,
    onPicked: (ByteArray) -> Unit,
    modifier: Modifier = Modifier,
    scope: CoroutineScope,
    enabled: Boolean = true,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch { AnhNen.tuUri(context, uri)?.let(onPicked) }
        }
    }

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(RoundedCornerShape(10.dp))
                .background(ColorEAECF3)
                .border(1.dp, if (url != null) Color111245 else ColorCFD0E4, RoundedCornerShape(10.dp))
                .clickable(enabled = enabled) {
                    launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                },
            contentAlignment = Alignment.Center,
        ) {
            if (url != null) {
                AsyncImage(
                    model = url.urlAnh(),
                    contentDescription = label,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color111245, RoundedCornerShape(50))
                        .padding(2.dp),
                )
            } else {
                Icon(Icons.Default.AddAPhoto, contentDescription = label, tint = Color78799C)
            }
        }
        Text(
            text = label,
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            textAlign = TextAlign.Center,
            style = Chu.Phu.copy(color = Color78799C),
        )
    }
}

/** Sáu khung bắt buộc của biên bản, hai cột. */
@Composable
fun SixSlotPhotoGrid(
    photos: Map<String, String>,
    onPicked: (slot: String, bytes: ByteArray) -> Unit,
    scope: CoroutineScope,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        PhotoSlot.BAT_BUOC.chunked(2).forEach { hang ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                hang.forEach { slot ->
                    PhotoSlotBox(
                        label = nhanKhungAnh(slot),
                        url = photos[slot],
                        onPicked = { onPicked(slot, it) },
                        modifier = Modifier.weight(1f),
                        scope = scope,
                        enabled = enabled,
                    )
                }
                if (hang.size == 1) Box(Modifier.weight(1f))
            }
        }
    }
}
