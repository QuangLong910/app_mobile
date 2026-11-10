package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai
import gtvt.ktpm.thuexe.ui.widget.form.urlAnh
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.widget.form.BookingStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.EmptyBox
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBox
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.LyDoDialog
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.SecondaryButton
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.tien

// "Tất cả" đứng đầu và là tab mặc định (chỉ số 0).
private val TABS = listOf(
    "Tất cả" to null,
    "Chờ tôi duyệt" to BookingStatus.CHO_CHU_XE,
    "Chờ thanh toán" to BookingStatus.CHO_THANH_TOAN,
    "Chờ giao xe" to BookingStatus.DA_XAC_NHAN,
    "Đang chạy" to BookingStatus.DANG_THUE,
)

@Composable
fun OwnerBookingsScreen(
    onBack: (() -> Unit)? = null,
    onMoChiTiet: (String) -> Unit = {},
    viewModel: OwnerBookingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(0) }
    var hoiTuChoi by remember { mutableStateOf<String?>(null) }
    var hoiHuy by remember { mutableStateOf<String?>(null) }

    TaiLaiKhiQuayLai(tab) { viewModel.load(TABS[tab].second) }
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay { viewModel.load(TABS[tab].second) }

    OwnerScaffold(title = "Đơn của xe tôi", onBack = onBack) { padding ->
        ScrollableTabRow(selectedTabIndex = tab, edgePadding = 0.dp, containerColor = Color.White, contentColor = Color111245) {
            TABS.forEachIndexed { i, (ten, _) ->
                Tab(selected = tab == i, onClick = { tab = i }, text = {
                    Text(ten, style = Chu.Than)
                })
            }
        }

        when {
            state.loading && state.bookings.isEmpty() -> LoadingBox()

            state.bookings.isEmpty() && state.errorCode != null ->
                ErrorBox(state.errorCode, message = state.errorMessage) { viewModel.load(TABS[tab].second) }

            state.bookings.isEmpty() -> EmptyBox(
                text = when (TABS[tab].second) {
                    BookingStatus.CHO_CHU_XE -> "Không có đơn nào đang chờ bạn.\nĐơn mới sẽ hiện ở đây, nhớ duyệt trong 30 phút."
                    BookingStatus.CHO_THANH_TOAN -> "Không có đơn nào đang chờ khách thanh toán.\nĐơn bạn đã nhận sẽ hiện ở đây cho tới khi khách chuyển tiền."
                    BookingStatus.DA_XAC_NHAN -> "Chưa có đơn nào chờ giao xe.\nĐơn đã nhận và khách đã chuyển tiền sẽ hiện ở đây."
                    BookingStatus.DANG_THUE -> "Chưa có chuyến nào đang chạy."
                    else -> "Chưa có đơn nào."
                },
                actionText = "Tải lại",
                onAction = { viewModel.load(TABS[tab].second) },
            )

            else -> LazyColumn(
                modifier = padding.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp),
            ) {
                item { ErrorBanner(state.errorCode, state.errorMessage) }
                items(state.bookings, key = { it.id }) { don ->
                    BookingCard(
                        booking = don,
                        dangBan = state.loading,
                        onConfirm = { viewModel.confirm(don.code) },
                        onReject = { hoiTuChoi = don.code },
                        onCancel = { hoiHuy = don.code },
                        onMoChiTiet = { onMoChiTiet(don.code) },
                    )
                }
            }
        }
    }

    hoiTuChoi?.let { id ->
        LyDoDialog(
            title = "Từ chối đơn",
            confirmText = "Từ chối",
            onDismiss = { hoiTuChoi = null },
            onConfirm = { lyDo -> viewModel.reject(id, lyDo); hoiTuChoi = null },
        )
    }
    hoiHuy?.let { id ->
        LyDoDialog(
            title = "Huỷ đơn",
            confirmText = "Huỷ đơn",
            hintText = "Khách sẽ đọc được lý do này. Lịch xe được nhả ngay để người khác đặt.",
            onDismiss = { hoiHuy = null },
            onConfirm = { lyDo -> viewModel.cancel(id, lyDo); hoiHuy = null },
        )
    }
}

@Composable
internal fun BookingCard(
    booking: Booking,
    dangBan: Boolean,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    onCancel: () -> Unit,
    onMoChiTiet: () -> Unit,
) {
    Card(
        // Chạm bất kỳ đâu trên thẻ là vào chi tiết. Trước đây chỉ vào được qua nút
        // "Lập biên bản giao xe", nên các trạng thái khác coi như không có đường vào.
        modifier = Modifier.fillMaxWidth().clickable { onMoChiTiet() },
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
                Text(booking.code, style = Chu.TieuDeThe.copy(color = Color111245))
                BookingStatusChip(booking.status)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AsyncImage(
                    model = booking.car?.photoUrl.urlAnh(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(68.dp, 50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ColorEAECF3),
                    contentScale = ContentScale.Crop,
                )
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    booking.car?.let {
                        Text(
                            text = "${it.brand} ${it.model}",
                            style = Chu.ThanDam.copy(color = Color111245),
                        )
                        Text(it.plate, style = Chu.Phu.copy(color = Color78799C))
                    }
                    booking.renter?.let {
                        Text(
                            text = it.fullName + (it.phone?.let { p -> " · $p" } ?: ""),
                            style = Chu.Phu.copy(color = Color78799C),
                        )
                    }
                }
            }

            // Dải ngày: một dòng thay cho ba dòng nhãn–giá trị.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .background(ColorEAECF3, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MocNgay("Nhận", booking.startDate.ngay())
                Text("→", style = Chu.ThanDam.copy(color = Color78799C))
                MocNgay("Trả", booking.endDate.ngay())
                Text(
                    text = "${booking.days} ngày",
                    style = Chu.Nhan.copy(color = Color464BD5),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Column {
                    Text("Bạn nhận", style = Chu.Phu.copy(color = Color78799C))
                    Text(
                        text = (booking.rentTotal - booking.commission).tien(),
                        style = Chu.Tien.copy(color = Color111245),
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Thuê ${booking.rentTotal.tien()}",
                        style = Chu.Phu.copy(color = Color78799C),
                    )
                    Text(
                        text = "Hoa hồng −${booking.commission.tien()}",
                        style = Chu.Phu.copy(color = Color78799C),
                    )
                }
            }

            booking.cancelReason?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(top = 6.dp),
                    style = Chu.Phu.copy(color = ColorD54646),
                )
            }

            when (booking.status) {
                BookingStatus.CHO_CHU_XE -> Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryButton("Từ chối", onReject, Modifier.weight(1f), enabled = !dangBan)
                    PrimaryButton("Nhận đơn", onConfirm, Modifier.weight(1f), enabled = !dangBan)
                }

                BookingStatus.DA_XAC_NHAN -> PrimaryButton(
                    text = "Lập biên bản giao xe",
                    onClick = onMoChiTiet,
                    modifier = Modifier.padding(top = 12.dp),
                    enabled = !dangBan,
                )

                BookingStatus.CHO_THANH_TOAN -> Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryButton("Huỷ đơn", onCancel, Modifier.weight(1f), enabled = !dangBan)
                    PrimaryButton("Xem mã QR", onMoChiTiet, Modifier.weight(1f), enabled = !dangBan)
                }

                // Khách chưa chắc đã lập biên bản trả, nên không hứa "soi biên bản" ở đây;
                // màn chi tiết mới biết có gì để soi và tự hiện nút đúng.
                BookingStatus.DANG_THUE -> PrimaryButton(
                    text = "Xem chuyến đi",
                    onClick = onMoChiTiet,
                    modifier = Modifier.padding(top = 12.dp),
                    enabled = !dangBan,
                )

                // Các trạng thái còn lại không có việc gì phải bấm gấp, nhưng vẫn
                // phải xem được tiền và biên bản.
                else -> SecondaryButton(
                    text = "Xem chi tiết",
                    onClick = onMoChiTiet,
                    modifier = Modifier.padding(top = 12.dp),
                    enabled = !dangBan,
                )
            }
        }
    }
}

@Composable
private fun MocNgay(nhan: String, ngay: String) {
    Column {
        Text(nhan, style = Chu.Phu.copy(color = Color78799C))
        Text(ngay, style = Chu.ThanDam.copy(color = Color111245))
    }
}
