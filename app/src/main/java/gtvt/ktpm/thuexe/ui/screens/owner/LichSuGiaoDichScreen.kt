package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.data.model.Payout
import gtvt.ktpm.thuexe.data.model.PayoutStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.Success
import gtvt.ktpm.thuexe.ui.theme.heroGradient
import gtvt.ktpm.thuexe.ui.widget.form.EmptyBox
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PayoutStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai
import gtvt.ktpm.thuexe.ui.widget.form.gioNgay
import gtvt.ktpm.thuexe.ui.widget.form.tien

/**
 * Lịch sử giao dịch của chủ xe: tiền đã nhận, tiền sàn còn đang chờ chuyển và từng lệnh chi.
 * Bấm một lệnh thì mở đơn tương ứng.
 */
@Composable
fun LichSuGiaoDichScreen(
    onBack: () -> Unit = {},
    onMoDon: (String) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var the by rememberSaveable { mutableIntStateOf(0) }

    TaiLaiKhiQuayLai(Unit) { viewModel.load() }
    TaiLaiKhiCoTinDay { viewModel.load() }

    val hangCho = state.payouts.filter { it.status != PayoutStatus.DA_CHI }
    val daNhan = state.payouts.filter { it.status == PayoutStatus.DA_CHI }

    OwnerScaffold(title = "Lịch sử giao dịch", onBack = onBack) { padding ->
        if (state.loading && state.payouts.isEmpty() && state.bookings.isEmpty()) {
            LoadingBox()
            return@OwnerScaffold
        }
        Column(modifier = padding.fillMaxWidth().verticalScroll(rememberScrollState())) {
            ErrorBanner(state.errorCode, state.errorMessage)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(heroGradient)
                    .padding(16.dp),
            ) {
                Text(
                    "Sàn đang chờ chuyển cho bạn",
                    style = Chu.Phu.copy(color = Color.White.copy(alpha = 0.9f)),
                )
                Text(
                    text = state.tienDangCho.tien(),
                    modifier = Modifier.padding(top = 6.dp),
                    style = Chu.SoLon.copy(color = Color.White, fontSize = 28.sp),
                )
                Text(
                    "${hangCho.size} lệnh đang chờ · sàn chuyển khoản vào tài khoản trong cam kết chủ xe",
                    modifier = Modifier.padding(top = 4.dp),
                    style = Chu.Phu.copy(color = Color.White.copy(alpha = 0.85f)),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Tien("Đã nhận", state.tienDaNhan, Success, Modifier.weight(1f))
                Tien("Trong chuyến chưa chốt", state.tienSapVe, Color78799C, Modifier.weight(1f))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = the == 0, onClick = { the = 0 },
                    label = { Text("Hàng chờ (${hangCho.size})") },
                )
                FilterChip(
                    selected = the == 1, onClick = { the = 1 },
                    label = { Text("Đã nhận (${daNhan.size})") },
                )
            }

            val ds = if (the == 0) hangCho else daNhan
            if (ds.isEmpty()) {
                EmptyBox(
                    text = if (the == 0) "Không có lệnh nào đang chờ.\nTiền về sau khi sàn chốt đơn."
                    else "Chưa nhận khoản nào.",
                )
            } else {
                Card(
                    modifier = Modifier.padding(top = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, ColorCFD0E4),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Column {
                        ds.forEachIndexed { i, p ->
                            if (i > 0) HorizontalDivider(color = ColorCFD0E4)
                            LenhChi(p, onClick = { p.bookingCode?.let(onMoDon) })
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Tien(nhan: String, soTien: Long, mau: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(12.dp),
    ) {
        Text(nhan, style = Chu.Phu.copy(color = Color78799C))
        Text(soTien.tien(), modifier = Modifier.padding(top = 4.dp), style = Chu.TieuDeThe.copy(color = mau))
    }
}

@Composable
private fun LenhChi(p: Payout, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = p.bookingCode != null, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(p.bookingCode ?: "Đơn #${p.bookingId}", style = Chu.Than.copy(color = Color111245))
            Text("${p.bankName} · ${p.bankAccount}", style = Chu.Phu.copy(color = Color78799C))
            p.paidAt?.let { Text("Chuyển lúc ${it.gioNgay()}", style = Chu.Phu.copy(color = Color78799C)) }
            p.transferRef?.let { Text("Mã GD: $it", style = Chu.Phu.copy(color = Color78799C)) }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(p.amount.tien(), style = Chu.Than.copy(color = Color111245))
            PayoutStatusChip(p.status, Modifier.padding(top = 3.dp))
        }
    }
}
