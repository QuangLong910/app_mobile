package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.widget.form.BookingStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.InfoRow
import gtvt.ktpm.thuexe.ui.widget.form.LichThang
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.SecondaryButton
import gtvt.ktpm.thuexe.ui.widget.form.TrangThaiNgay
import gtvt.ktpm.thuexe.ui.widget.form.nhanNhienLieu
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import gtvt.ktpm.thuexe.ui.widget.form.HeroScaffold
import gtvt.ktpm.thuexe.ui.widget.form.HeroPhotoPanel
import gtvt.ktpm.thuexe.ui.widget.form.LuoiThongSo
import gtvt.ktpm.thuexe.ui.widget.form.NutTronDo
import gtvt.ktpm.thuexe.ui.widget.form.CarStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.tien
import java.time.LocalDate

/** Theo dõi một xe: lịch bận, chuyến sắp tới, và mở/khoá ngày. */
@Composable
fun CarCalendarScreen(
    carId: Long,
    onBack: () -> Unit = {},
    onSuaXe: (Long) -> Unit = {},
    viewModel: CarCalendarViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    LaunchedEffect(carId) { viewModel.load(carId) }

    val xe = state.car
    val homNay = LocalDate.now()

HeroScaffold(
        bottomBar = {
            if (state.dangChon.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                ) {
                    SecondaryButton(
                        text = "Mở lại",
                        onClick = { viewModel.moNgayDangChon(carId) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                    )
                    Spacer(Modifier.width(8.dp))
                    PrimaryButton(
                        text = "Khoá ${state.dangChon.size} ngày",
                        onClick = { viewModel.khoaNgayDangChon(carId) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                    )
                }
            }
        },
    ) { padding ->
        if (xe == null) {
            Row(
                modifier = Modifier.fillMaxWidth().windowInsetsPadding(WindowInsets.statusBars).padding(12.dp),
            ) { NutTronDo(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại", onBack) }
            if (state.loading) LoadingBox() else ErrorBanner(state.errorCode, state.errorMessage)
            return@HeroScaffold
        }

        Column(modifier = padding.verticalScroll(scroll)) {
            HeroPhotoPanel(
                photos = xe.photos.sortedBy { it.sortOrder }.map { it.url },
                title = "${xe.brand} ${xe.model} ${xe.year}",
                subtitle = xe.plate,
                onBack = onBack,
                trailing = { CarStatusChip(xe.status) },
            ) {
                LuoiThongSo(
                    listOf(
                        Triple(Icons.Default.Payments, "Giá thuê", xe.pricePerDay.tien() + "/ngày"),
                        Triple(Icons.Default.Speed, "Giới hạn/ngày", "${xe.maxKmDay} km"),
                        Triple(Icons.Default.LocalGasStation, "Nhiên liệu", nhanNhienLieu(xe.fuel)),
                        Triple(Icons.Default.Route, "ODO ghi nhận", "${xe.odo} km"),
                    ),
                )
                SecondaryButton(
                    text = "Sửa thông tin xe",
                    onClick = { onSuaXe(carId) },
                    modifier = Modifier.padding(top = 14.dp),
                )
            }
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            val chuyen = state.chuyenSapToi
            FormSection(if (chuyen?.status == BookingStatus.DANG_THUE) "Chuyến đang chạy" else "Chuyến sắp tới") {
                if (chuyen == null) {
                    Text(
                        text = "Xe đang rảnh, chưa có chuyến nào phía trước.",
                        style = Chu.Than.copy(color = Color78799C),
                    )
                } else {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Text(
                            text = chuyen.code,
                            style = Chu.TieuDeThe.copy(color = Color111245),
                        )
                        Spacer(Modifier.width(8.dp))
                        BookingStatusChip(chuyen.status)
                    }
                    InfoRow("Khách", chuyen.renter?.fullName ?: "—")
                    chuyen.renter?.phone?.let { InfoRow("Điện thoại", it) }
                    InfoRow("Nhận xe", chuyen.startDate.ngay())
                    InfoRow("Trả xe", chuyen.endDate.ngay())
                    InfoRow("Bạn nhận", (chuyen.rentTotal - chuyen.commission).tien())
                }
            }

            FormSection("Lịch xe") {
                Text(
                    text = "Chạm vào ngày trống để khoá, chạm ngày đã khoá để mở lại. " +
                        "Ngày đã có đơn thì phải huỷ đơn chứ không mở ở đây được.",
                    style = Chu.Phu.copy(color = Color78799C),
                )
                LichThang(
                    thang = state.thang,
                    dangChon = state.dangChon,
                    onDoiThang = { viewModel.doiThang(carId, it) },
                    onChonNgay = { viewModel.chon(it) },
                    trangThai = { ngay ->
                        when {
                            ngay in state.ngayBan -> TrangThaiNgay.CO_DON
                            ngay in state.ngayKhoa -> TrangThaiNgay.KHOA
                            ngay.isBefore(homNay) -> TrangThaiNgay.QUA_KHU
                            else -> TrangThaiNgay.TRONG
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(Modifier.height(24.dp))
            }
        }
    }
}
