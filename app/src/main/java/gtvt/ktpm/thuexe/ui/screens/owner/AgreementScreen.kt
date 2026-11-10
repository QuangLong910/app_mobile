package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.FormTextField
import gtvt.ktpm.thuexe.ui.widget.form.InfoRow
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.ChuKyField
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.gioNgay
import kotlinx.coroutines.launch

/** Chưa ký bản này thì POST /owner/cars luôn trả OWNER_AGREEMENT_REQUIRED. */
@Composable
fun AgreementScreen(
    onBack: () -> Unit = {},
    onSigned: () -> Unit = {},
    viewModel: OwnerCarsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var cccd by remember { mutableStateOf("") }
    var soTk by remember { mutableStateOf("") }
    var tenNganHang by remember { mutableStateOf("") }
    var chuKyUrl by remember { mutableStateOf<String?>(null) }
    var dongY by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadAgreement() }

    val banDieuKhoan = state.agreement
    val hopLe = cccd.length >= 9 && soTk.isNotBlank() && tenNganHang.isNotBlank() &&
        chuKyUrl != null && dongY

    OwnerScaffold(
        title = "Cam kết chủ xe",
        onBack = onBack,
        bottomBar = {
            if (banDieuKhoan != null && !banDieuKhoan.signed) {
                PrimaryButton(
                    text = "Ký cam kết",
                    enabled = hopLe && !state.loading,
                    modifier = Modifier.padding(vertical = 8.dp),
                    onClick = {
                        viewModel.acceptAgreement(
                            version = banDieuKhoan.version,
                            cccdNo = cccd.trim(),
                            bankAccount = soTk.trim(),
                            bankName = tenNganHang.trim(),
                            signatureUrl = chuKyUrl!!,
                        )
                        onSigned()
                    },
                )
            }
        },
    ) { padding ->
        when {
            banDieuKhoan == null && state.loading -> LoadingBox()

            banDieuKhoan == null -> Spacer(Modifier.height(0.dp))

            banDieuKhoan.signed -> Column(modifier = padding.fillMaxWidth()) {
                FormSection("Đã ký") {
                    InfoRow("Bản điều khoản", "v${banDieuKhoan.version}")
                    InfoRow("Ký lúc", banDieuKhoan.signedAt?.gioNgay() ?: "—")
                }
                Text(
                    text = "Đổi điều khoản thì sàn sẽ yêu cầu ký lại bản mới.",
                    modifier = Modifier.padding(16.dp),
                    style = Chu.Than.copy(color = Color78799C),
                )
            }

            else -> Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
                ErrorBanner(state.errorCode, state.errorMessage)

                FormSection("Điều khoản v${banDieuKhoan.version}") {
                    Text(
                        text = banDieuKhoan.content,
                        modifier = Modifier.heightIn(max = 280.dp).verticalScroll(rememberScrollState()),
                        style = Chu.Than.copy(color = Color111245),
                    )
                }

                FormSection("Đối chiếu và nhận tiền") {
                    FormTextField(
                        label = "Số CCCD", value = cccd, onValueChange = { cccd = it },
                        hintText = "Trùng tên trên đăng ký xe", required = true,
                        keyboardType = KeyboardType.Number,
                        hint = "Tên trên đăng ký xe khác CCCD thì phải nộp thêm giấy uỷ quyền",
                    )
                    FormTextField(
                        label = "Số tài khoản", value = soTk, onValueChange = { soTk = it },
                        required = true, keyboardType = KeyboardType.Number,
                    )
                    FormTextField(
                        label = "Ngân hàng", value = tenNganHang, onValueChange = { tenNganHang = it },
                        hintText = "Vietcombank", required = true,
                        hint = "Sàn chuyển tiền thuê về đúng tài khoản này",
                    )
                }

                FormSection("Chữ ký") {
                    ChuKyField(
                        label = "Ký tay vào khung",
                        daKyUrl = chuKyUrl,
                        onXongKy = { png ->
                            scope.launch {
                                viewModel.uploadPhoto(png, "chu-ky.png")?.let { chuKyUrl = it }
                            }
                        },
                        required = true,
                    )
                }

                androidx.compose.foundation.layout.Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Checkbox(checked = dongY, onCheckedChange = { dongY = it })
                    Text(
                        text = "Tôi đã đọc và đồng ý với toàn bộ điều khoản trên.",
                        style = Chu.Than.copy(color = Color111245),
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
