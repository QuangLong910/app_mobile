package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import gtvt.ktpm.thuexe.ui.theme.heroGradient
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.StatCard
import gtvt.ktpm.thuexe.ui.widget.form.ViecCanLamRow
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.tien

private val XANH = gtvt.ktpm.thuexe.ui.theme.Success

@Composable
fun DashboardScreen(
    onXemDon: () -> Unit = {},
    onXemXe: () -> Unit = {},
    onMoLichSu: () -> Unit = {},
    onLapBienBan: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    TaiLaiKhiQuayLai(Unit) { viewModel.load() }
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay { viewModel.load() }

    OwnerScaffold(title = "Tổng quan") { padding ->
        if (state.loading && state.cars.isEmpty() && state.bookings.isEmpty()) {
            LoadingBox()
            return@OwnerScaffold
        }

        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            // Khối đầu: lời chào và con số chủ xe quan tâm nhất.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(heroGradient)
                    .clickable(onClick = onMoLichSu)
                    .padding(16.dp),
            ) {
                Text(
                    text = "Chào ${state.me?.fullName ?: "bạn"}",
                    style = Chu.TieuDeThe.copy(color = Color.White),
                )
                Text(
                    text = "Hôm nay ${java.time.LocalDate.now().ngay()}",
                    style = Chu.Phu.copy(color = Color.White.copy(alpha = 0.85f)),
                )
                Text(
                    text = state.tienDangCho.tien(),
                    modifier = Modifier.padding(top = 14.dp),
                    style = Chu.SoLon.copy(color = Color.White, fontSize = 28.sp),
                )
                Text(
                    text = "Sàn đang chờ chuyển về tài khoản bạn",
                    style = Chu.Phu.copy(color = Color.White.copy(alpha = 0.9f)),
                )
                Text(
                    text = "Xem lịch sử giao dịch ›",
                    modifier = Modifier.padding(top = 8.dp),
                    style = Chu.Nhan.copy(color = Color.White),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard(
                    value = state.donChoDuyet.size.toString(),
                    label = "Đơn chờ bạn duyệt",
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    color = ColorFF9064,
                    modifier = Modifier.weight(1f),
                    onClick = onXemDon,
                )
                StatCard(
                    value = state.chuyenDangChay.size.toString(),
                    label = "Chuyến đang chạy",
                    icon = Icons.Default.Schedule,
                    color = Color464BD5,
                    modifier = Modifier.weight(1f),
                    onClick = onXemDon,
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StatCard(
                    value = state.xeDangChoThue.toString(),
                    label = "Xe đang cho thuê",
                    icon = Icons.Default.DirectionsCar,
                    color = XANH,
                    modifier = Modifier.weight(1f),
                    onClick = onXemXe,
                )
                StatCard(
                    value = state.xeChoDuyet.toString(),
                    label = "Xe chờ sàn duyệt",
                    icon = Icons.Default.Payments,
                    color = Color111245,
                    modifier = Modifier.weight(1f),
                    onClick = onXemXe,
                )
            }

            ThePhang("Việc cần làm") {
                val viec = listOfNotNull(
                    Triple("Đơn chờ bạn duyệt", state.donChoDuyet.size, ColorD54646)
                        .takeIf { state.donChoDuyet.isNotEmpty() },
                    Triple("Sắp tới ngày giao xe", state.sapPhaiGiaoXe.size, ColorFF9064)
                        .takeIf { state.sapPhaiGiaoXe.isNotEmpty() },
                    Triple("Chờ khách chuyển tiền", state.choKhachTraTien.size, ColorFF9064)
                        .takeIf { state.choKhachTraTien.isNotEmpty() },
                    Triple("Xe đang chờ sàn duyệt", state.xeChoDuyet, Color464BD5)
                        .takeIf { state.xeChoDuyet > 0 },
                )
                if (viec.isEmpty()) {
                    Text(
                        text = "Không có việc nào đang chờ. Nhàn.",
                        style = Chu.Than.copy(color = Color78799C),
                    )
                } else {
                    viec.forEachIndexed { i, (ten, so, mau) ->
                        if (i > 0) HorizontalDivider(color = ColorCFD0E4)
                        ViecCanLamRow(ten, so, mau, onClick = if (ten.contains("Xe")) onXemXe else onXemDon)
                    }
                }
            }

            if (state.sapPhaiGiaoXe.isNotEmpty()) {
                ThePhang("Giao xe sắp tới") {
                    state.sapPhaiGiaoXe.forEachIndexed { i, don ->
                        if (i > 0) HorizontalDivider(color = ColorCFD0E4)
                        Column(modifier = Modifier.padding(vertical = 8.dp)) {
                            Text(
                                text = "${don.code} · ${don.car?.plate ?: ""}",
                                style = Chu.Than.copy(color = Color111245),
                            )
                            Text(
                                text = "Giao ${don.startDate.ngay()} cho ${don.renter?.fullName ?: "khách"}",
                                style = Chu.Phu.copy(color = Color78799C),
                            )
                            Text(
                                text = "Lập biên bản giao xe →",
                                modifier = Modifier
                                    .padding(top = 4.dp)
                                    .clickable { onLapBienBan(don.code) }
                                    .background(gtvt.ktpm.thuexe.ui.theme.ErrorTag, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                style = Chu.Phu.copy(color = ColorD54646),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThePhang(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
        Text(
            text = title.uppercase(),
            modifier = Modifier.padding(bottom = 4.dp),
            style = Chu.Nhan.copy(color = Color78799C),
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ColorCFD0E4),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) { content() }
        }
    }
}
