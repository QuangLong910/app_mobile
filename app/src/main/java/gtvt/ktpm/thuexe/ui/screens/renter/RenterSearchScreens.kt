package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Badge
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import gtvt.ktpm.thuexe.data.model.Availability
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarSearchItem
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.repo.RenterRepository
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.Primary
import gtvt.ktpm.thuexe.ui.theme.PrimaryTag
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Route
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import gtvt.ktpm.thuexe.ui.widget.form.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

// ─────────────────────────── Tìm xe ───────────────────────────

data class SearchState(
    val loading: Boolean = false,
    val items: List<CarSearchItem> = emptyList(),
    val total: Int = 0,
    val daTim: Boolean = false,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class SearchCarsViewModel @Inject constructor(
    private val repo: RenterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var dangTim: Job? = null

    /** Lần gọi mới huỷ lần cũ, không thì kết quả về trễ đè lên bộ lọc mới. */
    fun tim(
        district: String?,
        from: LocalDate?,
        to: LocalDate?,
        seats: Int?,
        transmission: String?,
        maxPrice: Long?,
    ) {
        dangTim?.cancel()
        dangTim = viewModelScope.launch {
            _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
            when (val r = repo.search(district, from, to, seats, transmission, maxPrice)) {
                is ApiResult.Ok -> _state.update {
                    it.copy(loading = false, items = r.data.items, total = r.data.total, daTim = true)
                }
                is ApiResult.Err -> _state.update {
                    it.copy(loading = false, errorCode = r.code, errorMessage = r.message)
                }
            }
        }
    }
}

private val HOP_SO = listOf("" to "Mọi loại", "SO_SAN" to "Số sàn", "SO_TU_DONG" to "Số tự động")

@Composable
fun SearchCarsScreen(
    onMoXe: (Long) -> Unit = {},
    viewModel: SearchCarsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var quan by rememberSaveable { mutableStateOf("") }
    var tu by remember { mutableStateOf<LocalDate?>(null) }
    var den by remember { mutableStateOf<LocalDate?>(null) }
    var soCho by rememberSaveable { mutableStateOf(0) }
    var hopSo by rememberSaveable { mutableStateOf("") }
    var giaToiDa by rememberSaveable { mutableStateOf(0L) }
    var loiNgay by remember { mutableStateOf<String?>(null) }
    var hienLoc by rememberSaveable { mutableStateOf(false) }
    val dangLoc = quan.isNotBlank() || tu != null || soCho > 0 || hopSo.isNotBlank() || giaToiDa > 0

    fun tim() {
        if ((tu == null) != (den == null)) {
            loiNgay = "Chọn cả ngày nhận và ngày trả, hoặc bỏ trống cả hai."
            return
        }
        if (tu != null && den != null && !den!!.isAfter(tu)) {
            loiNgay = "Ngày trả phải sau ngày nhận."
            return
        }
        loiNgay = null
        hienLoc = false
        viewModel.tim(
            district = quan.ifBlank { null },
            from = tu, to = den,
            seats = soCho.takeIf { it > 0 },
            transmission = hopSo.ifBlank { null },
            maxPrice = giaToiDa.takeIf { it > 0 },
        )
    }

    // Mở màn là hiện luôn danh sách xe đang cho thuê, khỏi bắt bấm tìm.
    LaunchedEffect(Unit) { if (!state.daTim) tim() }

    OwnerScaffold(
        title = "Tìm xe",
        actions = {
            IconButton(onClick = { hienLoc = !hienLoc }) {
                BadgedBox(badge = { if (dangLoc) Badge() }) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Bộ lọc tìm xe",
                        tint = Color111245,
                    )
                }
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = padding.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            item {
                AnimatedVisibility(visible = hienLoc) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ColorCFD0E4),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        FormTextField("Khu vực", quan, { quan = it }, hintText = "Ví dụ Quận 7")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FormDateField("Nhận xe", tu, { tu = it }, Modifier.weight(1f))
                            FormDateField("Trả xe", den, { den = it }, Modifier.weight(1f), error = loiNgay)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FormIntField("Số chỗ tối thiểu", soCho, { soCho = it }, Modifier.weight(1f))
                            FormDropdownField(
                                "Hộp số", hopSo, HOP_SO, { hopSo = it }, Modifier.weight(1f),
                            )
                        }
                        FormMoneyField("Giá tối đa / ngày", giaToiDa, { giaToiDa = it })
                        PrimaryButton(
                            text = "Tìm xe",
                            onClick = { tim() },
                            modifier = Modifier.padding(top = 8.dp),
                            enabled = !state.loading,
                        )
                    }
                }
                }
            }
            item { ErrorBanner(state.errorCode, state.errorMessage) }

            when {
                state.loading && state.items.isEmpty() -> item { LoadingBox() }
                state.items.isEmpty() && state.daTim && state.errorCode == null -> item {
                    EmptyBox(text = "Không có xe nào khớp.\nThử bớt điều kiện lọc hoặc đổi ngày.")
                }
                else -> {
                    item {
                        if (state.daTim) Text(
                            "${state.total} xe",
                            style = Chu.Nhan.copy(color = Color78799C),
                        )
                    }
                    items(state.items, key = { it.id }) { xe -> CarCard(xe) { onMoXe(xe.id) } }
                }
            }
        }
    }
}

@Composable
private fun CarCard(xe: CarSearchItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCFD0E4),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column {
            AsyncImage(
                model = xe.photoUrl.urlAnh(),
                contentDescription = "${xe.brand} ${xe.model}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(ColorEAECF3),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    "${xe.brand} ${xe.model} ${xe.year}",
                    style = Chu.TieuDeThe.copy(color = Color111245),
                )
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Nhan("${xe.seats} chỗ", Icons.Default.Groups)
                    Nhan(nhanHopSo(xe.transmission), Icons.Default.Settings)
                    Nhan(nhanNhienLieu(xe.fuel), Icons.Default.LocalGasStation)
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Text(xe.district, style = Chu.Phu.copy(color = Color78799C))
                    Text(
                        "${xe.pricePerDay.tien()} / ngày",
                        style = Chu.Tien.copy(color = Primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun Nhan(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector? = null) {
    Row(
        modifier = Modifier
            .background(PrimaryTag, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        icon?.let { Icon(it, null, tint = Color111245, modifier = Modifier.size(12.dp)) }
        Text(text = text, style = Chu.Phu.copy(color = Color111245))
    }
}

// ─────────────────────────── Chi tiết xe ───────────────────────────

data class CarDetailState(
    val loading: Boolean = false,
    val car: Car? = null,
    val lich: Availability? = null,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class CarDetailViewModel @Inject constructor(
    private val repo: RenterRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(CarDetailState())
    val state: StateFlow<CarDetailState> = _state.asStateFlow()

    fun tai(carId: Long) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = repo.car(carId)) {
            is ApiResult.Err -> {
                _state.update { it.copy(loading = false, errorCode = r.code, errorMessage = r.message) }
                return@launch
            }
            is ApiResult.Ok -> _state.update { it.copy(car = r.data) }
        }
        // Lịch chỉ để xem trước; hỏng thì vẫn cho xem xe, người dùng sẽ biết ở bước báo giá.
        val homNay = LocalDate.now()
        val lich = repo.availability(carId, homNay, homNay.plusDays(120))
        _state.update { it.copy(loading = false, lich = (lich as? ApiResult.Ok)?.data) }
    }
}

@Composable
fun CarDetailScreen(
    carId: Long,
    onBack: () -> Unit = {},
    onDatXe: (Long) -> Unit = {},
    viewModel: CarDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var thang by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(carId) { viewModel.tai(carId) }

    val xe = state.car
    HeroScaffold(
        bottomBar = {
            if (xe != null) PrimaryButton(
                text = "Chọn ngày và đặt xe · ${xe.pricePerDay.tien()}/ngày",
                onClick = { onDatXe(xe.id) },
                modifier = Modifier.padding(vertical = 8.dp),
            )
        },
    ) { padding ->
        if (xe == null) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(12.dp),
            ) { NutTronDo(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", onBack) }
            if (state.loading) LoadingBox() else ErrorBox(state.errorCode, message = state.errorMessage) {
                viewModel.tai(carId)
            }
            return@HeroScaffold
        }
        Column(modifier = padding.verticalScroll(scroll)) {
            HeroPhotoPanel(
                photos = xe.photos.sortedBy { it.sortOrder }.map { it.url },
                title = "${xe.brand} ${xe.model} ${xe.year}",
                subtitle = xe.plate,
                onBack = onBack,
            ) {
                LuoiThongSo(
                    listOf(
                        Triple(Icons.Default.Groups, "Số chỗ", "${xe.seats} chỗ"),
                        Triple(Icons.Default.Settings, "Hộp số", nhanHopSo(xe.transmission)),
                        Triple(Icons.Default.LocalGasStation, "Nhiên liệu", nhanNhienLieu(xe.fuel)),
                        Triple(Icons.Default.LocationOn, "Khu vực", xe.district),
                        Triple(Icons.Default.Speed, "Giới hạn/ngày", "${xe.maxKmDay} km"),
                        Triple(Icons.Default.Route, "Đã đi", "${xe.odo} km"),
                    ),
                )
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                FormSection("Giá") {
                    InfoRow("Giá thuê", "${xe.pricePerDay.tien()} / ngày")
                    InfoRow("Tiền cọc", xe.deposit.tien())
                    Text(
                        "Cọc được hoàn lại sau khi trả xe, trừ phí phát sinh nếu có.",
                        modifier = Modifier.padding(top = 4.dp),
                        style = Chu.Phu.copy(color = Color78799C),
                    )
                }
                xe.description?.takeIf { it.isNotBlank() }?.let {
                    FormSection("Mô tả") { Text(it, style = Chu.Than.copy(color = Color111245)) }
                }
                xe.pickupAddress?.let {
                    FormSection("Địa chỉ nhận xe") { Text(it, style = Chu.Than.copy(color = Color111245)) }
                }

                FormSection("Lịch xe") {
                    val lich = state.lich
                    if (lich == null) {
                        Text("Chưa tải được lịch.", style = Chu.Than.copy(color = Color78799C))
                    } else {
                        LichThang(
                            thang = thang,
                            trangThai = { d ->
                                when {
                                    d.isBefore(LocalDate.now()) -> TrangThaiNgay.QUA_KHU
                                    d in lich.busyDays -> TrangThaiNgay.CO_DON
                                    d in lich.blockedDays -> TrangThaiNgay.KHOA
                                    else -> TrangThaiNgay.TRONG
                                }
                            },
                            onDoiThang = { thang = it },
                            onChonNgay = {},
                            dangChon = emptySet(),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
