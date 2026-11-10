package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.DocStatus
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.TokenStore
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import gtvt.ktpm.thuexe.data.repo.FileRepository
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.Primary
import gtvt.ktpm.thuexe.ui.theme.heroGradient
import gtvt.ktpm.thuexe.ui.widget.form.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// ─────────────────────────── Cá nhân ───────────────────────────

data class RenterProfileState(
    val loading: Boolean = false,
    val dangLuu: Boolean = false,
    val daLuu: Boolean = false,
    val me: MeResponse? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class RenterProfileViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _state = MutableStateFlow(RenterProfileState())
    val state: StateFlow<RenterProfileState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = auth.me()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, me = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    /** Khai nơi nhận tiền hoàn cọc; thiếu cái này thì lệnh hoàn không chuyển đi đâu được. */
    fun luuTaiKhoan(soTaiKhoan: String, nganHang: String) = viewModelScope.launch {
        _state.update { it.copy(dangLuu = true, daLuu = false, errorCode = null, errorMessage = null) }
        when (val r = auth.capNhatTaiKhoan(soTaiKhoan, nganHang)) {
            is ApiResult.Ok -> _state.update { it.copy(dangLuu = false, daLuu = true, me = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(dangLuu = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    /** Nhớ chế độ để lần sau mở app vào đúng chỗ. */

    fun logout() = auth.logout()
}

@Composable
fun RenterProfileScreen(
    onNopGiayTo: () -> Unit = {},
    onDangXuat: () -> Unit = {},
    viewModel: RenterProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    TaiLaiKhiQuayLai(Unit) { viewModel.load() }

    OwnerScaffold(title = "Cá nhân") { padding ->
        val me = state.me
        if (me == null) {
            if (state.loading) LoadingBox() else ErrorBox(state.errorCode, message = state.errorMessage) { viewModel.load() }
            return@OwnerScaffold
        }
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.size(62.dp).background(heroGradient, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = me.fullName.trim().split(" ").lastOrNull()?.take(1)?.uppercase() ?: "?",
                        style = Chu.SoLon.copy(color = Color.White),
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(me.fullName, style = Chu.TieuDeThe.copy(color = Color111245))
                    Text(me.phone, style = Chu.Than.copy(color = Color78799C))
                    me.email?.let { Text(it, style = Chu.Phu.copy(color = Color78799C)) }
                }
            }

            FormSection("Tài khoản") {
                var nganHang by rememberSaveable(me.bankName) { mutableStateOf(me.bankName.orEmpty()) }
                var soTk by rememberSaveable(me.bankAccount) { mutableStateOf(me.bankAccount.orEmpty()) }
                Text(
                    text = "Nơi nhận hoàn cọc",
                    style = Chu.Nhan.copy(color = Color78799C),
                )
                Text(
                    text = if (me.bankAccount.isNullOrBlank())
                        "Chưa khai. Chưa khai thì sàn chưa hoàn được tiền cọc cho bạn."
                    else "Sàn sẽ hoàn cọc và tiền thừa (nếu có) vào tài khoản này.",
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp),
                    style = Chu.Phu.copy(color = if (me.bankAccount.isNullOrBlank()) ColorD54646 else Color78799C),
                )
                FormTextField("Ngân hàng", nganHang, { nganHang = it }, hintText = "Ví dụ Vietcombank")
                FormTextField(
                    "Số tài khoản", soTk, { soTk = it.filter(Char::isDigit).take(20) },
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                )
                SecondaryButton(
                    text = if (state.dangLuu) "Đang lưu…" else if (state.daLuu) "Đã lưu" else "Lưu tài khoản",
                    onClick = { viewModel.luuTaiKhoan(soTk, nganHang) },
                    modifier = Modifier.padding(top = 6.dp),
                    enabled = soTk.length >= 6 && nganHang.isNotBlank() && !state.dangLuu,
                )
            }

            FormSection("Giấy tờ lái xe") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Trạng thái", style = Chu.Than.copy(color = Color78799C))
                    StatusChip(
                        text = nhanTrangThaiGiayTo(me.documentStatus),
                        color = when (me.documentStatus) {
                            DocStatus.DAT -> Primary
                            DocStatus.CHO_DUYET -> gtvt.ktpm.thuexe.ui.theme.Warning
                            else -> ColorD54646
                        },
                    )
                }
                Text(
                    text = when (me.documentStatus) {
                        DocStatus.DAT -> "CCCD và GPLX đã được sàn duyệt. Bạn đặt xe được."
                        DocStatus.CHO_DUYET -> "Sàn đang xem giấy tờ của bạn. Duyệt xong mới đặt xe được."
                        DocStatus.TU_CHOI -> "Giấy tờ bị từ chối, nộp lại giúp sàn."
                        else -> "Chưa nộp CCCD và GPLX. Chưa nộp thì không đặt được xe."
                    },
                    modifier = Modifier.padding(top = 6.dp),
                    style = Chu.Phu.copy(color = Color78799C),
                )
                if (me.documentStatus != DocStatus.DAT && me.documentStatus != DocStatus.CHO_DUYET) {
                    PrimaryButton(
                        text = if (me.documentStatus == DocStatus.TU_CHOI) "Nộp lại giấy tờ" else "Nộp CCCD và GPLX",
                        onClick = onNopGiayTo,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }

            SecondaryButton(
                text = "Đăng xuất",
                onClick = { viewModel.logout(); onDangXuat() },
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp),
            )
        }
    }
}

// ─────────────────────────── Nộp giấy tờ ───────────────────────────

data class KycState(
    val dangTai: Set<String> = emptySet(),
    val urls: Map<String, String> = emptyMap(),
    val dangGui: Boolean = false,
    val daGui: Boolean = false,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class SubmitDocumentsViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val files: FileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(KycState())
    val state: StateFlow<KycState> = _state.asStateFlow()

    /** Ảnh đã nén cạnh dài 1600px trước khi vào đây. */
    fun taiAnh(khung: String, bytes: ByteArray) = viewModelScope.launch {
        _state.update { it.copy(dangTai = it.dangTai + khung, errorCode = null, errorMessage = null) }
        when (val r = files.upload(bytes, "$khung.jpg")) {
            is ApiResult.Ok -> _state.update {
                it.copy(dangTai = it.dangTai - khung, urls = it.urls + (khung to r.data))
            }
            is ApiResult.Err -> _state.update {
                it.copy(dangTai = it.dangTai - khung, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun gui(cccd: String, gplx: String, hang: String, han: LocalDate) = viewModelScope.launch {
        val u = _state.value.urls
        _state.update { it.copy(dangGui = true, errorCode = null, errorMessage = null) }
        when (val r = auth.submitDocuments(
            cccdNo = cccd.trim(), gplxNo = gplx.trim(), gplxClass = hang, gplxExpiry = han,
            frontUrl = u.getValue(TRUOC), backUrl = u.getValue(SAU), selfieUrl = u[SELFIE],
        )) {
            is ApiResult.Ok -> _state.update { it.copy(dangGui = false, daGui = true) }
            is ApiResult.Err -> _state.update {
                it.copy(dangGui = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    companion object {
        const val TRUOC = "truoc"
        const val SAU = "sau"
        const val SELFIE = "selfie"
    }
}

private val HANG_GPLX = listOf("B1", "B2", "C", "D", "E").map { it to it }

@Composable
fun SubmitDocumentsScreen(
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
    viewModel: SubmitDocumentsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    val scope = rememberCoroutineScope()
    var cccd by rememberSaveable { mutableStateOf("") }
    var gplx by rememberSaveable { mutableStateOf("") }
    var hang by rememberSaveable { mutableStateOf("B2") }
    var han by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(state.daGui) { if (state.daGui) onDone() }

    val loiCccd = if (cccd.isNotBlank() && cccd.trim().length != 12) "CCCD gồm 12 số" else null
    val loiHan = han?.takeIf { it.isBefore(LocalDate.now()) }?.let { "GPLX đã hết hạn" }
    val du = cccd.trim().length == 12 && gplx.isNotBlank() && han != null && loiHan == null &&
        SubmitDocumentsViewModel.TRUOC in state.urls && SubmitDocumentsViewModel.SAU in state.urls &&
        state.dangTai.isEmpty()

    OwnerScaffold(
        title = "Nộp giấy tờ",
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = "Gửi để sàn duyệt",
                onClick = { viewModel.gui(cccd, gplx, hang, han!!) },
                modifier = Modifier.padding(vertical = 8.dp),
                enabled = du && !state.dangGui,
            )
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            FormSection("Căn cước công dân") {
                FormTextField(
                    "Số CCCD", cccd, { cccd = it.filter(Char::isDigit).take(12) },
                    required = true, error = loiCccd, keyboardType = androidx.compose.ui.text.input.KeyboardType.Number,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhotoSlotBox(
                        "Mặt trước", state.urls[SubmitDocumentsViewModel.TRUOC],
                        { viewModel.taiAnh(SubmitDocumentsViewModel.TRUOC, it) },
                        Modifier.weight(1f), scope,
                    )
                    PhotoSlotBox(
                        "Mặt sau", state.urls[SubmitDocumentsViewModel.SAU],
                        { viewModel.taiAnh(SubmitDocumentsViewModel.SAU, it) },
                        Modifier.weight(1f), scope,
                    )
                }
            }

            FormSection("Giấy phép lái xe") {
                FormTextField("Số GPLX", gplx, { gplx = it.filter(Char::isLetterOrDigit).take(15) }, required = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FormDropdownField("Hạng", hang, HANG_GPLX, { hang = it }, Modifier.weight(1f))
                    FormDateField("Hết hạn", han, { han = it }, Modifier.weight(1f), required = true, error = loiHan)
                }
            }

            FormSection("Ảnh chân dung (không bắt buộc)") {
                PhotoSlotBox(
                    "Chụp gương mặt", state.urls[SubmitDocumentsViewModel.SELFIE],
                    { viewModel.taiAnh(SubmitDocumentsViewModel.SELFIE, it) },
                    Modifier.fillMaxWidth(0.5f), scope,
                )
            }
            Text(
                "Sàn chỉ dùng giấy tờ để xác minh người thuê. Thường duyệt trong vòng một ngày.",
                modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                style = Chu.Phu.copy(color = Color78799C),
            )
        }
    }
}
