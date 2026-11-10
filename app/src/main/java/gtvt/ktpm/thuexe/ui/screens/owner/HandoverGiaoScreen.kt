package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormIntField
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.FormTextField
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.ChuKyField
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.SixSlotPhotoGrid
import gtvt.ktpm.thuexe.ui.widget.form.nhanKhungAnh

/**
 * Chủ xe lập biên bản GIAO. Nộp xong đơn vào CHO_SOI: khách soi từng ảnh, chụp bổ sung
 * vết mình thấy rồi mới ký. Khách ký thì đơn mới sang DANG_THUE.
 */
@Composable
fun HandoverGiaoScreen(
    maDon: String,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
    /** GIAO do chủ xe lập; TRA do khách lập. */
    kind: String = gtvt.ktpm.thuexe.data.model.HandoverKind.GIAO,
    viewModel: HandoverViewModel = hiltViewModel(),
) {
    val laTra = kind == gtvt.ktpm.thuexe.data.model.HandoverKind.TRA
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var odo by remember { mutableStateOf(0) }
    var nhienLieu by remember { mutableStateOf(8f) }
    var ghiChu by remember { mutableStateOf("") }
    // Nộp hỏng thì ở lại màn đọc lỗi, không lặng lẽ thoát.
    LaunchedEffect(state.daHoanTat) { if (state.daHoanTat) onDone() }

    val thieu = state.thieuAnh
    val hopLe = thieu.isEmpty() && odo > 0 && state.chuKyDaLuu

    OwnerScaffold(
        title = if (laTra) "Biên bản trả xe" else "Biên bản giao xe",
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = "Nộp biên bản",
                enabled = hopLe && !state.loading,
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    viewModel.lapBienBan(
                        kind = kind,
                        maDon = maDon,
                        odo = odo,
                        fuelLevel = nhienLieu.toInt(),
                        signatureUrl = state.signatureUrl!!,
                        note = ghiChu.trim().ifBlank { null },
                    )
                },
            )
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            FormSection("Sáu ảnh bắt buộc") {
                Text(
                    text = "Chụp đủ sáu khung. Vết xước có sẵn nhớ ghi vào phần ghi chú — " +
                        "lúc trả xe không còn gì để cãi.",
                    style = Chu.Than.copy(color = Color78799C),
                )
                SixSlotPhotoGrid(
                    photos = state.photos,
                    scope = scope,
                    modifier = Modifier.padding(top = 8.dp),
                    onPicked = { slot, bytes -> viewModel.addPhoto(slot, bytes) },
                )
                if (thieu.isNotEmpty()) {
                    Text(
                        text = "Còn thiếu: " + thieu.joinToString { nhanKhungAnh(it) },
                        modifier = Modifier.padding(top = 6.dp),
                        style = Chu.Than.copy(color = Color78799C),
                    )
                }
            }

            FormSection(if (laTra) "Tình trạng lúc trả" else "Tình trạng lúc giao") {
                FormIntField(
                    label = "Số ODO", value = odo, onValueChange = { odo = it },
                    suffix = "km", required = true,
                    hint = "Con số này là mốc tính quá km lúc trả xe",
                )
                Text(
                    text = "Mức nhiên liệu: ${nhienLieu.toInt()}/8 nấc",
                    style = Chu.Than.copy(color = Color111245),
                )
                Slider(
                    value = nhienLieu,
                    onValueChange = { nhienLieu = it },
                    valueRange = 0f..8f,
                    steps = 7,
                )
                FormTextField(
                    label = "Ghi chú", value = ghiChu, onValueChange = { ghiChu = it },
                    hintText = "Xước cản sau, đã có sẵn…",
                    singleLine = false, minLines = 3,
                )
            }

            FormSection("Chữ ký của bạn") {
                ChuKyField(
                    dangTaiLen = state.dangTaiChuKy,
                    loiTaiLen = state.loiChuKy,
                    label = "Ký tay vào khung",
                    daKyUrl = state.signatureUrl,
                    onXongKy = { png -> viewModel.addSignature(png) },
                    required = true,
                    hint = if (laTra) "Ký xong chủ xe sẽ soi ảnh và ký xác nhận" else "Ký xong đưa máy cho khách soi ảnh và ký tiếp",
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
