package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.widget.form.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegisterState(
    val loading: Boolean = false,
    val laChuXe: Boolean = false,
    /** Đã gửi OTP: chuyển sang bước nhập mã. */
    val daGuiOtp: Boolean = false,
    val xong: Boolean = false,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val auth: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun dangKy(phone: String, password: String, fullName: String, laChuXe: Boolean) = viewModelScope.launch {
        _state.update { it.copy(loading = true, laChuXe = laChuXe, errorCode = null, errorMessage = null) }
        when (val r = auth.register(phone, password, fullName, isOwner = laChuXe)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, daGuiOtp = true) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    /** Xác thực xong là có token, vào thẳng app, không bắt đăng nhập lại. */
    fun xacThuc(phone: String, otp: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = auth.verifyOtp(phone, otp)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, xong = true) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun quayLai() = _state.update { it.copy(daGuiOtp = false, errorCode = null, errorMessage = null) }
}

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var ten by rememberSaveable { mutableStateOf("") }
    var sdt by rememberSaveable { mutableStateOf("") }
    var matKhau by rememberSaveable { mutableStateOf("") }
    var nhapLai by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var laChuXe by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.xong) {
        if (state.xong) {
            // Vào đúng phần theo vai đã chọn lúc đăng ký; chủ xe mới chưa có xe, tự vào Hồ sơ để ký cam kết.
            val dich = if (state.laChuXe) gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute.HOME else RenterRoute.HOME
            navController.navigate(dich) { popUpTo(0) }
        }
    }

    val loiSdt = if (sdt.isNotBlank() && !Regex("^0\\d{9}$").matches(sdt.trim())) "Số điện thoại gồm 10 số, bắt đầu bằng 0" else null
    val loiMk = if (matKhau.isNotEmpty() && matKhau.length < 4) "Mật khẩu tối thiểu 4 ký tự" else null
    val loiNhapLai = if (nhapLai.isNotEmpty() && nhapLai != matKhau) "Mật khẩu nhập lại chưa khớp" else null
    val duDangKy = ten.isNotBlank() && loiSdt == null && sdt.isNotBlank() && matKhau.length >= 4 && nhapLai == matKhau

    OwnerScaffold(
        title = if (state.daGuiOtp) "Nhập mã xác thực" else "Tạo tài khoản",
        onBack = { if (state.daGuiOtp) viewModel.quayLai() else navController.popBackStack() },
        bottomBar = {
            if (state.daGuiOtp) PrimaryButton(
                text = "Xác thực",
                onClick = { viewModel.xacThuc(sdt.trim(), otp.trim()) },
                modifier = Modifier.padding(vertical = 8.dp),
                enabled = otp.trim().length == 6 && !state.loading,
            ) else PrimaryButton(
                text = "Nhận mã xác thực",
                onClick = { viewModel.dangKy(sdt.trim(), matKhau, ten.trim(), laChuXe) },
                modifier = Modifier.padding(vertical = 8.dp),
                enabled = duDangKy && !state.loading,
            )
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            if (!state.daGuiOtp) {
                Text(
                    "Bạn muốn dùng app để",
                    modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                    style = Chu.Nhan.copy(color = Color78799C),
                )
                ChonVaiTro(laChuXe = laChuXe, onChon = { laChuXe = it })
                FormTextField(
                    "Họ và tên", ten, { ten = it }, required = true,
                    modifier = Modifier.padding(top = 12.dp),
                )
                FormTextField(
                    "Số điện thoại", sdt, { sdt = it.filter(Char::isDigit).take(10) },
                    required = true, error = loiSdt, keyboardType = KeyboardType.Phone,
                )
                FormTextField(
                    "Mật khẩu", matKhau, { matKhau = it }, required = true, error = loiMk,
                    keyboardType = KeyboardType.Password,
                )
                FormTextField(
                    "Nhập lại mật khẩu", nhapLai, { nhapLai = it }, required = true, error = loiNhapLai,
                    keyboardType = KeyboardType.Password,
                )
            } else {
                Text(
                    "Mã gồm 6 số đã được gửi tới $sdt. Mã có hiệu lực 5 phút.",
                    modifier = Modifier.padding(top = 12.dp),
                    style = Chu.Than.copy(color = Color78799C),
                )
                FormTextField(
                    "Mã xác thực", otp, { otp = it.filter(Char::isDigit).take(6) },
                    required = true, keyboardType = KeyboardType.Number,
                )
                TextButton(onClick = { viewModel.dangKy(sdt.trim(), matKhau, ten.trim(), laChuXe) }) {
                    Text("Gửi lại mã", style = Chu.Nut)
                }
            }
        }
    }
}

/** Hai thẻ chọn một, giống lối chọn vai chủ xe / khách thuê ở các app cho thuê xe. */
@Composable
private fun ChonVaiTro(laChuXe: Boolean, onChon: (Boolean) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TheVaiTro(
            nhan = "Thuê xe", moTa = "Tìm và đặt xe của người khác",
            duocChon = !laChuXe, onClick = { onChon(false) }, modifier = Modifier.weight(1f),
        )
        TheVaiTro(
            nhan = "Cho thuê xe", moTa = "Đăng xe của tôi để cho thuê",
            duocChon = laChuXe, onClick = { onChon(true) }, modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun TheVaiTro(nhan: String, moTa: String, duocChon: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.Card(
        onClick = onClick,
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (duocChon) gtvt.ktpm.thuexe.ui.theme.Primary.copy(alpha = 0.08f) else androidx.compose.ui.graphics.Color.White,
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (duocChon) 2.dp else 1.dp,
            if (duocChon) gtvt.ktpm.thuexe.ui.theme.Primary else gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4,
        ),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(nhan, style = Chu.TieuDeThe.copy(color = if (duocChon) gtvt.ktpm.thuexe.ui.theme.Primary else gtvt.ktpm.thuexe.ui.theme.Color111245))
            Text(moTa, modifier = Modifier.padding(top = 2.dp), style = Chu.Phu.copy(color = Color78799C))
        }
    }
}
