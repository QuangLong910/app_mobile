package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingDetail
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.DocStatus
import gtvt.ktpm.thuexe.data.model.MeResponse
import gtvt.ktpm.thuexe.data.repo.AuthRepository
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.data.model.HandoverStatus
import gtvt.ktpm.thuexe.data.model.LoaiSuCo
import gtvt.ktpm.thuexe.data.model.Quote
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.ErrorCode
import gtvt.ktpm.thuexe.data.repo.RenterRepository
import gtvt.ktpm.thuexe.ui.screens.owner.DaiChoDoi
import gtvt.ktpm.thuexe.ui.screens.owner.DongBienBan
import gtvt.ktpm.thuexe.ui.screens.owner.MaChuyenKhoan
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.Primary
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.DateRange
import gtvt.ktpm.thuexe.ui.widget.form.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

// ─────────────────────────── Đặt xe ───────────────────────────

data class BookState(
    val loading: Boolean = false,
    val car: Car? = null,
    val quote: Quote? = null,
    val dangBaoGia: Boolean = false,
    val dangDat: Boolean = false,
    /** Mã đơn vừa tạo: có là rời màn. */
    val maDon: String? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class BookCarViewModel @Inject constructor(
    private val repo: RenterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(BookState())
    val state: StateFlow<BookState> = _state.asStateFlow()

    fun taiXe(carId: Long) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.car(carId)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, car = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun baoGia(carId: Long, from: LocalDate, to: LocalDate) = viewModelScope.launch {
        _state.update { it.copy(dangBaoGia = true, quote = null, errorCode = null, errorMessage = null) }
        when (val r = repo.quote(carId, from, to)) {
            is ApiResult.Ok -> _state.update { it.copy(dangBaoGia = false, quote = r.data) }
            is ApiResult.Err -> _state.update {
                it.copy(dangBaoGia = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun dat(carId: Long, from: LocalDate, to: LocalDate) = viewModelScope.launch {
        _state.update { it.copy(dangDat = true, errorCode = null, errorMessage = null) }
        when (val r = repo.book(carId, from, to)) {
            is ApiResult.Ok -> _state.update { it.copy(dangDat = false, maDon = r.data.code) }
            is ApiResult.Err -> _state.update {
                it.copy(dangDat = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }
}

@Composable
fun BookCarScreen(
    carId: Long,
    onBack: () -> Unit = {},
    onDaDat: (String) -> Unit = {},
    onNopGiayTo: () -> Unit = {},
    viewModel: BookCarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var tu by remember { mutableStateOf<LocalDate?>(null) }
    var den by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(carId) { viewModel.taiXe(carId) }
    LaunchedEffect(state.maDon) { state.maDon?.let(onDaDat) }

    // Chọn đủ hai ngày hợp lệ là tự báo giá, khỏi bắt bấm thêm một nút.
    val loiNgay = when {
        tu != null && den != null && !den!!.isAfter(tu) -> "Ngày trả phải sau ngày nhận."
        tu != null && tu!!.isBefore(LocalDate.now()) -> "Không đặt được ngày trong quá khứ."
        else -> null
    }
    LaunchedEffect(tu, den) {
        if (tu != null && den != null && loiNgay == null) viewModel.baoGia(carId, tu!!, den!!)
    }

    val q = state.quote
    val datDuoc = q != null && q.conTrong && loiNgay == null && !state.dangDat

    OwnerScaffold(
        title = "Đặt xe",
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = if (q != null) "Đặt xe · trả ${q.tongPhaiTra.tien()}" else "Đặt xe",
                onClick = { viewModel.dat(carId, tu!!, den!!) },
                modifier = Modifier.padding(vertical = 8.dp),
                enabled = datDuoc,
            )
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)
            if (state.errorCode == ErrorCode.KYC_REQUIRED) {
                SecondaryButton(
                    text = "Nộp CCCD và GPLX",
                    onClick = onNopGiayTo,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            state.car?.let {
                FormSection("Xe") {
                    Text("${it.brand} ${it.model} ${it.year}", style = Chu.TieuDeThe.copy(color = Color111245))
                    Text(
                        "${it.pricePerDay.tien()} / ngày · cọc ${it.deposit.tien()}",
                        style = Chu.Than.copy(color = Color78799C),
                    )
                }
            }

            FormSection("Thời gian thuê") {
                FormDateField("Nhận xe", tu, { tu = it }, required = true)
                FormDateField("Trả xe", den, { den = it }, required = true, error = loiNgay)
                Text(
                    "Ngày trả xe không tính tiền: thuê từ 12 đến 15 là 3 ngày.",
                    style = Chu.Phu.copy(color = Color78799C),
                )
            }

            if (state.dangBaoGia) LoadingBox(Modifier.padding(top = 16.dp))

            if (q != null) {
                if (!q.conTrong) {
                    Text(
                        "Xe đã có người đặt: " + q.ngayBanTrongKhoang.joinToString { it.ngay() } + ". Chọn ngày khác.",
                        modifier = Modifier.padding(top = 12.dp),
                        style = Chu.Than.copy(color = ColorD54646),
                    )
                }
                FormSection("Chi phí") {
                    InfoRow("Đơn giá", "${q.pricePerDay.tien()} / ngày")
                    InfoRow("Số ngày", "${q.days} ngày")
                    InfoRow("Tiền thuê", q.rentTotal.tien())
                    InfoRow("Tiền cọc", q.deposit.tien())
                    HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 6.dp))
                    InfoRow("Tổng phải chuyển", q.tongPhaiTra.tien(), valueColor = Primary)
                }
                Text(
                    "Đặt xong chủ xe có 30 phút để nhận đơn. Chưa mất tiền cho tới khi chủ xe nhận " +
                        "và bạn chuyển khoản theo mã QR. Cọc được hoàn sau khi trả xe.",
                    modifier = Modifier.padding(top = 10.dp, bottom = 16.dp),
                    style = Chu.Phu.copy(color = Color78799C),
                )
            }
        }
    }
}

// ─────────────────────────── Chuyến của tôi ───────────────────────────

private val TAB_DON = listOf(
    "Tất cả" to null,
    "Chờ chủ xe" to BookingStatus.CHO_CHU_XE,
    "Chờ thanh toán" to BookingStatus.CHO_THANH_TOAN,
    "Sắp đi" to BookingStatus.DA_XAC_NHAN,
    "Đang thuê" to BookingStatus.DANG_THUE,
)

data class MyBookingsState(
    val loading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val repo: RenterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(MyBookingsState())
    val state: StateFlow<MyBookingsState> = _state.asStateFlow()

    private var dangTai: kotlinx.coroutines.Job? = null
    private var loc: String? = null

    fun load(status: String?) {
        dangTai?.cancel()
        val doiTab = status != loc
        loc = status
        dangTai = viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true, errorCode = null, errorMessage = null,
                    bookings = if (doiTab) emptyList() else it.bookings,
                )
            }
            when (val r = repo.myBookings(status)) {
                is ApiResult.Ok -> _state.update { it.copy(loading = false, bookings = r.data) }
                is ApiResult.Err -> _state.update {
                    it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
                }
            }
        }
    }
}

@Composable
fun MyBookingsScreen(
    onMoDon: (String) -> Unit = {},
    viewModel: MyBookingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(0) }

    TaiLaiKhiQuayLai(tab) { viewModel.load(TAB_DON[tab].second) }
    TaiLaiKhiCoTinDay { viewModel.load(TAB_DON[tab].second) }

    OwnerScaffold(title = "Chuyến của tôi") { padding ->
        ScrollableTabRow(
            selectedTabIndex = tab, edgePadding = 0.dp,
            containerColor = Color.White, contentColor = Color111245,
        ) {
            TAB_DON.forEachIndexed { i, (ten, _) ->
                Tab(selected = tab == i, onClick = { tab = i }, text = { Text(ten, style = Chu.Than) })
            }
        }
        when {
            state.loading && state.bookings.isEmpty() -> LoadingBox()
            state.bookings.isEmpty() && state.errorCode != null ->
                ErrorBox(state.errorCode, message = state.errorMessage) { viewModel.load(TAB_DON[tab].second) }
            state.bookings.isEmpty() -> EmptyBox(
                text = "Chưa có chuyến nào ở mục này.\nSang tab Tìm xe để đặt chuyến đầu tiên.",
                actionText = "Tải lại",
                onAction = { viewModel.load(TAB_DON[tab].second) },
            )
            else -> LazyColumn(
                modifier = padding.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                item { ErrorBanner(state.errorCode, state.errorMessage) }
                items(state.bookings, key = { it.id }) { don -> ChuyenCard(don) { onMoDon(don.code) } }
            }
        }
    }
}

@Composable
private fun ChuyenCard(don: Booking, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCFD0E4),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(don.code, style = Chu.TieuDeThe.copy(color = Color111245))
                BookingStatusChip(don.status)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = don.car?.photoUrl.urlAnh(),
                    contentDescription = null,
                    modifier = Modifier.size(68.dp, 50.dp).clip(RoundedCornerShape(8.dp)).background(ColorEAECF3),
                    contentScale = ContentScale.Crop,
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    don.car?.let {
                        Text("${it.brand} ${it.model}", style = Chu.ThanDam.copy(color = Color111245))
                        Text(it.plate, style = Chu.Phu.copy(color = Color78799C))
                    }
                    Text(
                        "${don.startDate.ngay()} → ${don.endDate.ngay()} · ${don.days} ngày",
                        style = Chu.Phu.copy(color = Color78799C),
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Tiền thuê", style = Chu.Phu.copy(color = Color78799C))
                Text(don.rentTotal.tien(), style = Chu.Tien.copy(color = Color111245))
            }
            don.cancelReason?.let {
                Text(it, modifier = Modifier.padding(top = 6.dp), style = Chu.Phu.copy(color = ColorD54646))
            }
        }
    }
}

// ─────────────────────────── Chi tiết chuyến ───────────────────────────

data class RenterDetailState(
    val loading: Boolean = false,
    val detail: BookingDetail? = null,
    /** Xe đầy đủ để đọc địa chỉ nhận xe — chỉ có sau khi chủ xe nhận đơn. */
    val car: Car? = null,
    val me: MeResponse? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
) {
    val booking: Booking? get() = detail?.booking

    /** Chủ xe chỉ nhận được đơn khi giấy tờ của khách đã được duyệt. */
    val canNopGiayTo: Boolean
        get() = booking?.status == BookingStatus.CHO_CHU_XE && me != null && me.documentStatus != DocStatus.DAT
    private val handovers get() = detail?.handovers.orEmpty()
    val bbGiao get() = handovers.firstOrNull { it.kind == HandoverKind.GIAO }
    val bbTra get() = handovers.firstOrNull { it.kind == HandoverKind.TRA }

    val dangChoBenKia: Boolean
        get() = booking?.status in setOf(BookingStatus.CHO_CHU_XE, BookingStatus.CHO_THANH_TOAN)

    val duocHuy: Boolean
        get() = booking?.status in setOf(
            BookingStatus.CHO_CHU_XE, BookingStatus.CHO_THANH_TOAN, BookingStatus.DA_XAC_NHAN,
        ) && bbGiao == null

    /** Chủ xe đã lập biên bản GIAO: khách phải soi ảnh rồi ký thì mới nhận xe. */
    val canSoiGiao: Boolean
        get() = booking?.status == BookingStatus.DA_XAC_NHAN && bbGiao?.status == HandoverStatus.CHO_SOI

    val canLapTra: Boolean
        get() = booking?.status == BookingStatus.DANG_THUE && bbTra == null

    /** Chủ xe không giữ đúng cam kết (không giao xe, phí quyết toán vô lý…): mở tranh chấp. */
    val canMoTranhChap: Boolean
        get() = booking?.status in setOf(
            BookingStatus.DA_XAC_NHAN, BookingStatus.DANG_THUE, BookingStatus.CHO_QUYET_TOAN,
        )

    val canBaoSuCo: Boolean
        get() = booking?.status == BookingStatus.DANG_THUE

    val canXinGiaHan: Boolean
        get() = booking?.status == BookingStatus.DANG_THUE && booking?.coYeuCauGiaHan == false
}

@HiltViewModel
class RenterBookingDetailViewModel @Inject constructor(
    private val repo: RenterRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RenterDetailState())
    val state: StateFlow<RenterDetailState> = _state.asStateFlow()

    fun tai(maDon: String, thamLang: Boolean = false) = viewModelScope.launch {
        if (!thamLang) _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.detail(maDon)) {
            is ApiResult.Ok -> {
                _state.update { it.copy(loading = false, detail = r.data) }
                (auth.me() as? ApiResult.Ok)?.let { m -> _state.update { it.copy(me = m.data) } }
                // Địa chỉ nhận xe chỉ mở sau khi đơn đã qua bước xác nhận.
                val b = r.data.booking
                if (b.status !in setOf(BookingStatus.CHO_CHU_XE, BookingStatus.BI_TU_CHOI, BookingStatus.DA_HUY, BookingStatus.HET_HAN) &&
                    _state.value.car == null
                ) {
                    (repo.car(b.carId) as? ApiResult.Ok)?.let { c -> _state.update { it.copy(car = c.data) } }
                }
            }
            is ApiResult.Err -> if (!thamLang) _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    /** Chỉ bản debug: giả lập khách vừa chuyển khoản, để demo không cần bắn webhook. */
    fun giaLapChuyenKhoan(maDon: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.giaLapChuyenKhoan(maDon)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun huy(maDon: String, lyDo: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.cancel(maDon, lyDo)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun moTranhChap(maDon: String, lyDo: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.openDispute(maDon, lyDo)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun baoSuCo(maDon: String, ghiChu: String, loai: String) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.reportIncident(maDon, ghiChu, loai)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }

    fun xinGiaHan(maDon: String, ngayMoi: LocalDate) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.extendRequest(maDon, ngayMoi)) {
            is ApiResult.Ok -> tai(maDon)
            is ApiResult.Err -> _state.update {
                it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
            }
        }
    }
}

@Composable
fun RenterBookingDetailScreen(
    maDon: String,
    onBack: () -> Unit = {},
    onSoiBienBanGiao: () -> Unit = {},
    onLapBienBanTra: () -> Unit = {},
    onNopGiayTo: () -> Unit = {},
    viewModel: RenterBookingDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var hoiHuy by remember { mutableStateOf(false) }
    var hoiTranhChap by remember { mutableStateOf(false) }
    var hoiSuCo by remember { mutableStateOf(false) }
    var hoiGiaHan by remember { mutableStateOf(false) }

    TaiLaiKhiQuayLai(maDon) { viewModel.tai(maDon) }
    // Chủ xe nhận đơn hoặc tiền vào: thông báo đẩy tới thì cập nhật, không cần hỏi định kỳ.
    TaiLaiKhiCoTinDay { viewModel.tai(maDon, thamLang = true) }

    val don = state.booking
    HeroScaffold(
        bottomBar = {
            when {
                state.canSoiGiao -> PrimaryButton(
                    text = "Soi biên bản giao xe và ký",
                    onClick = onSoiBienBanGiao,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                state.canLapTra -> PrimaryButton(
                    text = "Lập biên bản trả xe",
                    onClick = onLapBienBanTra,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
                state.duocHuy -> SecondaryButton(
                    text = "Huỷ đơn",
                    onClick = { hoiHuy = true },
                    modifier = Modifier.padding(vertical = 8.dp),
                    enabled = !state.loading,
                )
            }
        },
    ) { padding ->
        if (don == null) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(12.dp),
            ) { NutTronDo(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", onBack) }
            if (state.loading) LoadingBox() else ErrorBanner(state.errorCode, state.errorMessage)
            return@HeroScaffold
        }
        Column(modifier = padding.verticalScroll(scroll)) {
            HeroPhotoPanel(
                photos = listOfNotNull(don.car?.photoUrl),
                title = don.code,
                subtitle = don.car?.let { "${it.brand} ${it.model} · ${it.plate}" },
                onBack = onBack,
                trailing = { BookingStatusChip(don.status) },
            ) {
                LuoiThongSo(
                    listOf(
                        Triple(Icons.Default.CalendarMonth, "Nhận xe", don.startDate.ngay()),
                        Triple(Icons.Default.EventAvailable, "Trả xe", don.endDate.ngay()),
                        Triple(Icons.Default.DateRange, "Số ngày", "${don.days} ngày"),
                    ),
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            TienTrinhDon(don.status)

            if (don.dangTranhChap) {
                Text(
                    text = "Lý do: ${don.disputeReason ?: "—"}. Sàn sẽ liên hệ hai bên để xử lý.",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        .background(gtvt.ktpm.thuexe.ui.theme.ErrorTag, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    style = Chu.Than.copy(color = ColorD54646),
                )
            }

            if (don.coYeuCauGiaHan) {
                Text(
                    text = "Bạn đã xin gia hạn tới ${don.extendRequestedEndDate?.ngay()}, đang chờ chủ xe duyệt.",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        .background(gtvt.ktpm.thuexe.ui.theme.PrimaryTag, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    style = Chu.Than.copy(color = Color111245),
                )
            }

            // Nợ từ phí phát sinh vượt cọc: nói rõ số tiền và hệ quả, không để khách tự đoán.
            if (don.conNo) {
                Text(
                    text = "Bạn còn nợ ${don.debtAmount.tien()} (phí phát sinh vượt tiền cọc). " +
                        "Chuyển khoản cho sàn để trả nợ; chưa trả thì chưa đặt được chuyến mới.",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        .background(gtvt.ktpm.thuexe.ui.theme.ErrorTag, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    style = Chu.Than.copy(color = ColorD54646),
                )
            }

            // Chủ xe không nhận được đơn khi giấy tờ chưa duyệt: báo sớm để khách kịp nộp.
            if (state.canNopGiayTo) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        .background(gtvt.ktpm.thuexe.ui.theme.PrimaryTag, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                ) {
                    Text(
                        text = when (state.me?.documentStatus) {
                            DocStatus.CHO_DUYET -> "Sàn đang duyệt giấy tờ của bạn. Duyệt xong chủ xe mới nhận được đơn."
                            DocStatus.TU_CHOI -> "Giấy tờ bị từ chối. Nộp lại để chủ xe nhận được đơn."
                            else -> "Bạn chưa nộp CCCD và GPLX. Chủ xe chỉ nhận đơn khi giấy tờ đã được sàn duyệt."
                        },
                        style = Chu.Than.copy(color = Color111245),
                    )
                    if (state.me?.documentStatus != DocStatus.CHO_DUYET) {
                        SecondaryButton(
                            text = "Nộp giấy tờ",
                            onClick = onNopGiayTo,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }

            if (state.dangChoBenKia) {
                DaiChoDoi(
                    if (don.status == BookingStatus.CHO_CHU_XE) "Đang chờ chủ xe nhận đơn"
                    else "Đang chờ tiền chuyển khoản",
                )
            }

            if (state.car?.pickupAddress != null || don.holdExpiresAt != null || don.cancelReason != null) {
                FormSection("Chuyến thuê") {
                    state.car?.pickupAddress?.let { InfoRow("Địa chỉ nhận xe", it) }
                    don.holdExpiresAt?.takeIf { don.status == BookingStatus.CHO_CHU_XE }
                        ?.let { InfoRow("Chủ xe trả lời trước", it.gioNgay(), valueColor = ColorD54646) }
                    don.cancelReason?.let { InfoRow("Lý do", it, valueColor = ColorD54646) }
                }
            }

            FormSection("Tiền") {
                InfoRow("Đơn giá", "${don.pricePerDay.tien()} / ngày")
                InfoRow("Tiền thuê", don.rentTotal.tien())
                InfoRow("Tiền cọc", don.deposit.tien())
                HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 6.dp))
                InfoRow("Tổng phải chuyển", (don.rentTotal + don.deposit).tien(), valueColor = Primary)
            }

            // Chỉ bản debug: bấm là coi như đã chuyển khoản, dành cho buổi demo không có ngân hàng thật.
            if (gtvt.ktpm.thuexe.BuildConfig.DEBUG && don.status == BookingStatus.CHO_THANH_TOAN) {
                SecondaryButton(
                    text = "Giả lập đã chuyển khoản (chỉ để demo)",
                    onClick = { viewModel.giaLapChuyenKhoan(maDon) },
                    modifier = Modifier.padding(top = 8.dp),
                    enabled = !state.loading,
                )
            }

            state.detail?.payment?.let { tt ->
                FormSection("Thanh toán") {
                    InfoRow("Số tiền chuyển", tt.amount.tien())
                    InfoRow("Trạng thái", if (tt.status == "DA_NHAN") "Sàn đã nhận tiền" else "Chờ chuyển khoản")
                    tt.transferCode?.let { MaChuyenKhoan(it) }
                    if (tt.status != "DA_NHAN") tt.qrUrl?.let { url ->
                        Text(
                            "Quét mã bằng app ngân hàng. Số tiền và nội dung đã điền sẵn, không phải gõ.",
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                            style = Chu.Phu.copy(color = Color78799C),
                        )
                        AsyncImage(
                            model = url.urlAnh(),
                            contentDescription = "Mã QR chuyển khoản",
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .border(1.dp, ColorCFD0E4, RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit,
                        )
                    }
                }
            }

            if (state.detail?.handovers.orEmpty().isNotEmpty()) {
                FormSection("Biên bản") {
                    state.detail!!.handovers.forEachIndexed { i, bb ->
                        if (i > 0) HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 8.dp))
                        DongBienBan(bb)
                    }
                }
            }

            val phi = state.detail?.charges.orEmpty()
            if (phi.isNotEmpty()) {
                FormSection("Phí phát sinh") {
                    phi.forEach { InfoRow(nhanPhi(it.type), it.amount.tien()) }
                    HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 6.dp))
                    InfoRow("Tổng phí", phi.sumOf { it.amount }.tien(), valueColor = ColorD54646)
                }
            }

            val suCo = state.detail?.incidents.orEmpty()
            if (suCo.isNotEmpty()) {
                FormSection("Sự cố đã báo") {
                    suCo.forEachIndexed { i, sc ->
                        if (i > 0) HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 8.dp))
                        InfoRow(
                            label = "${nhanBen(sc.reportedBy)} báo",
                            value = nhanLoaiSuCo(sc.type),
                            valueColor = if (sc.type == LoaiSuCo.VA_CHAM) ColorD54646 else Color78799C,
                        )
                        Text(sc.note, style = Chu.Phu.copy(color = Color78799C))
                    }
                }
            }

            if (!don.dangTranhChap && (state.canMoTranhChap || state.canBaoSuCo || state.canXinGiaHan)) {
                FormSection("Cần hỗ trợ?") {
                    if (state.canXinGiaHan) {
                        SecondaryButton(
                            text = "Xin gia hạn",
                            onClick = { hoiGiaHan = true },
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    if (state.canBaoSuCo) {
                        SecondaryButton(
                            text = "Báo sự cố giữa chuyến",
                            onClick = { hoiSuCo = true },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                    if (state.canMoTranhChap) {
                        SecondaryButton(
                            text = "Chủ xe không giữ đúng cam kết? Báo với sàn",
                            onClick = { hoiTranhChap = true },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (hoiHuy) {
        LyDoDialog(
            title = "Huỷ đơn",
            confirmText = "Huỷ đơn",
            hintText = "Chủ xe sẽ đọc được lý do này",
            onDismiss = { hoiHuy = false },
            onConfirm = { lyDo -> viewModel.huy(maDon, lyDo); hoiHuy = false },
        )
    }

    if (hoiTranhChap) {
        LyDoDialog(
            title = "Báo với sàn",
            confirmText = "Gửi",
            hintText = "Sàn sẽ xem xét và liên hệ cả hai bên",
            onDismiss = { hoiTranhChap = false },
            onConfirm = { lyDo -> viewModel.moTranhChap(maDon, lyDo); hoiTranhChap = false },
        )
    }

    if (hoiSuCo) {
        BaoSuCoDialog(
            onDismiss = { hoiSuCo = false },
            onConfirm = { ghiChu, vaCham ->
                viewModel.baoSuCo(maDon, ghiChu, if (vaCham) LoaiSuCo.VA_CHAM else LoaiSuCo.KHAC)
                hoiSuCo = false
            },
        )
    }

    if (hoiGiaHan && don != null) {
        GiaHanDialog(
            ngayTraHienTai = don.endDate,
            onDismiss = { hoiGiaHan = false },
            onConfirm = { ngayMoi -> viewModel.xinGiaHan(maDon, ngayMoi); hoiGiaHan = false },
        )
    }
}
