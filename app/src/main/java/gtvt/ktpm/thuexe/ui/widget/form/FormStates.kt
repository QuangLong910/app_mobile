package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.gradient

@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color111245)
    }
}

@Composable
fun EmptyBox(
    text: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            style = Chu.Than.copy(color = Color78799C),
        )
        if (actionText != null && onAction != null) {
            OutlinedButton(onClick = onAction, modifier = Modifier.padding(top = 12.dp)) {
                Text(actionText, style = Chu.Than.copy(color = Color111245))
            }
        }
    }
}

/** Nhận thẳng mã lỗi; câu chữ do nhanLoi quyết định, không phải backend. */
@Composable
fun ErrorBox(
    code: String?,
    modifier: Modifier = Modifier,
    message: String? = null,
    onRetry: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = nhanLoi(code, message),
            textAlign = TextAlign.Center,
            style = Chu.Than.copy(color = ColorD54646),
        )
        if (onRetry != null) {
            OutlinedButton(onClick = onRetry, modifier = Modifier.padding(top = 12.dp)) {
                Text("Thử lại", style = Chu.Than.copy(color = Color111245))
            }
        }
    }
}

/** Dải lỗi mỏng đặt trên đầu danh sách, dùng khi đã có dữ liệu cũ để hiện. */
@Composable
fun ErrorBanner(code: String?, message: String? = null, modifier: Modifier = Modifier) {
    if (code == null) return
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(gtvt.ktpm.thuexe.ui.theme.ErrorTag, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Text(
            text = nhanLoi(code, message),
            style = Chu.Than.copy(color = ColorD54646, textAlign = TextAlign.Start),
        )
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(
                brush = if (enabled) gradient else androidx.compose.ui.graphics.SolidColor(gtvt.ktpm.thuexe.ui.theme.Neutral5),
                shape = RoundedCornerShape(8.dp),
            ),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        ),
    ) {
        Text(
            text = text,
            style = Chu.Nut.copy(color = if (enabled) gtvt.ktpm.thuexe.ui.theme.Neutral9 else Color78799C),
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Text(text, style = Chu.Nut.copy(color = Color111245))
    }
}

/** Hộp hỏi lý do — dùng cho từ chối đơn, huỷ đơn, phản đối biên bản. Lý do rỗng thì không cho gửi. */
@Composable
fun LyDoDialog(
    title: String,
    confirmText: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
    hintText: String = "Ghi rõ để bên kia hiểu vì sao",
) {
    var lyDo by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = Chu.TieuDeThe.copy(color = Color111245)) },
        text = {
            FormTextField(
                label = "Lý do",
                value = lyDo,
                onValueChange = { lyDo = it },
                hintText = hintText,
                required = true,
                singleLine = false,
                minLines = 3,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(lyDo.trim()) }, enabled = lyDo.isNotBlank()) {
                Text(confirmText, style = Chu.Than.copy(color = ColorD54646))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Thôi", style = Chu.Than.copy(color = Color78799C))
            }
        },
        containerColor = Color.White,
    )
}

/**
 * Hộp báo sự cố giữa chuyến — có thêm ô "Va chạm/tai nạn" vì mức độ này cần sàn xử lý khác:
 * xem bảo hiểm xe để hướng dẫn bồi thường, thay vì chỉ ghi nhận thông thường.
 */
@Composable
fun BaoSuCoDialog(onDismiss: () -> Unit, onConfirm: (ghiChu: String, vaCham: Boolean) -> Unit) {
    var ghiChu by remember { mutableStateOf("") }
    var vaCham by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Báo sự cố giữa chuyến", style = Chu.TieuDeThe.copy(color = Color111245)) },
        text = {
            Column {
                FormTextField(
                    label = "Mô tả sự cố",
                    value = ghiChu,
                    onValueChange = { ghiChu = it },
                    hintText = "Ví dụ: va quẹt, xe có tiếng lạ…",
                    required = true,
                    singleLine = false,
                    minLines = 3,
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        .clickable { vaCham = !vaCham },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.Checkbox(checked = vaCham, onCheckedChange = { vaCham = it })
                    Text("Có va chạm / tai nạn", style = Chu.Than.copy(color = Color111245))
                }
                if (vaCham) {
                    Text(
                        "Giữ nguyên hiện trường, chụp thêm ảnh nếu có thể. Sàn sẽ liên hệ để " +
                            "hướng dẫn làm bồi thường bảo hiểm.",
                        modifier = Modifier.padding(top = 4.dp),
                        style = Chu.Phu.copy(color = ColorD54646),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(ghiChu.trim(), vaCham) },
                enabled = ghiChu.isNotBlank(),
            ) {
                Text("Gửi", style = Chu.Than.copy(color = Color111245))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Thôi", style = Chu.Than.copy(color = Color78799C))
            }
        },
        containerColor = Color.White,
    )
}

/** Hộp chọn ngày trả mới — dùng cho khách xin gia hạn. Ngày phải sau ngày trả hiện tại. */
@Composable
fun GiaHanDialog(
    ngayTraHienTai: java.time.LocalDate,
    onDismiss: () -> Unit,
    onConfirm: (java.time.LocalDate) -> Unit,
) {
    var ngayMoi by remember { mutableStateOf<java.time.LocalDate?>(null) }
    val loi = ngayMoi?.takeIf { !it.isAfter(ngayTraHienTai) }?.let { "Phải sau ngày trả hiện tại (${ngayTraHienTai.ngay()})" }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xin gia hạn", style = Chu.TieuDeThe.copy(color = Color111245)) },
        text = {
            Column {
                Text(
                    "Đang trả xe ngày ${ngayTraHienTai.ngay()}. Chọn ngày trả mới, chờ chủ xe duyệt.",
                    style = Chu.Phu.copy(color = Color78799C),
                    modifier = Modifier.padding(bottom = 10.dp),
                )
                FormDateField("Ngày trả mới", ngayMoi, { ngayMoi = it }, required = true, error = loi)
            }
        },
        confirmButton = {
            TextButton(
                onClick = { ngayMoi?.let(onConfirm) },
                enabled = ngayMoi != null && loi == null,
            ) {
                Text("Gửi yêu cầu", style = Chu.Than.copy(color = Color111245))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Thôi", style = Chu.Than.copy(color = Color78799C))
            }
        },
        containerColor = Color.White,
    )
}
