package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.data.model.Handover
import gtvt.ktpm.thuexe.data.model.HandoverStatus
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorE8E9F5
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.widget.form.BookingStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.InfoRow
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.LyDoDialog
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.SecondaryButton
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay
import gtvt.ktpm.thuexe.ui.widget.form.StatusChip
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai
import gtvt.ktpm.thuexe.ui.widget.form.gioNgay
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Call
import gtvt.ktpm.thuexe.ui.widget.form.HeroScaffold
import gtvt.ktpm.thuexe.ui.widget.form.HeroPhotoPanel
import gtvt.ktpm.thuexe.ui.widget.form.LuoiThongSo
import gtvt.ktpm.thuexe.ui.widget.form.NutTronDo
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.nhanBen
import gtvt.ktpm.thuexe.ui.widget.form.nhanLoaiSuCo
import gtvt.ktpm.thuexe.ui.widget.form.nhanPhi
import gtvt.ktpm.thuexe.ui.widget.form.nhanTrangThaiBienBan
import gtvt.ktpm.thuexe.ui.widget.form.tien
import gtvt.ktpm.thuexe.ui.widget.form.urlAnh

/**
 * Toàn cảnh một đơn: tiền, mã chuyển khoản kèm QR, hai biên bản, phí phát sinh.
 * Đây là chỗ duy nhất đọc GET /bookings/{id} — các màn khác chỉ có bản rút gọn.
 */
@Composable
fun BookingDetailScreen(
    maDon: String,
    onBack: () -> Unit = {},
    onLapBienBanGiao: (String) -> Unit = {},
    onSoiBienBanTra: (String) -> Unit = {},
    viewModel: BookingDetailViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()
    var hoiLyDo by remember { mutableStateOf(false) }
    var hoiTranhChap by remember { mutableStateOf(false) }
    var hoiSuCo by remember { mutableStateOf(false) }

    // Mở màn, và mỗi lần quay lại từ màn con hay từ app khác, đều lấy dữ liệu mới.
    TaiLaiKhiQuayLai(maDon) { viewModel.tai(maDon) }
    // Khách trả tiền hoặc ký biên bản: thông báo đẩy tới thì cập nhật, không cần hỏi định kỳ.
    TaiLaiKhiCoTinDay { viewModel.tai(maDon, thamLang = true) }

HeroScaffold(
        bottomBar = {
            when {
                state.duocDuyet -> Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryButton(
                        text = "Từ chối",
                        onClick = { hoiLyDo = true },
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                    )
                    PrimaryButton(
                        text = "Nhận đơn",
                        onClick = { viewModel.xacNhan(maDon) },
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                    )
                }

                state.duocLapBienBanGiao -> PrimaryButton(
                    text = "Lập biên bản giao xe",
                    onClick = { onLapBienBanGiao(maDon) },
                    modifier = Modifier.padding(vertical = 8.dp),
                    enabled = !state.loading,
                )

                state.duocSoiBienBanTra -> PrimaryButton(
                    text = "Soi biên bản trả xe",
                    onClick = { onSoiBienBanTra(maDon) },
                    modifier = Modifier.padding(vertical = 8.dp),
                    enabled = !state.loading,
                )
            }
        },
    ) { padding ->
        val don = state.booking
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
            if (state.dangChoBenKia) {
                DaiChoDoi(
                    when (state.booking?.status) {
                        gtvt.ktpm.thuexe.data.model.BookingStatus.CHO_THANH_TOAN ->
                            "Đang chờ khách chuyển tiền"
                        else -> "Đang chờ khách soi ảnh và ký biên bản"
                    },
                )
            }
            if (don.dangTranhChap) {
                Text(
                    text = "Đang tranh chấp — bên ${don.disputeOpenedBy?.let { nhanBen(it) } ?: "sàn"} báo: " +
                        "${don.disputeReason ?: "—"}. Sàn sẽ liên hệ để xử lý.",
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                        .background(gtvt.ktpm.thuexe.ui.theme.ErrorTag, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    style = Chu.Than.copy(color = ColorD54646),
                )
            }

            if (state.coYeuCauGiaHan) {
                FormSection("Khách xin gia hạn") {
                    InfoRow("Ngày trả mới", don.extendRequestedEndDate?.ngay() ?: "—")
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SecondaryButton(
                            text = "Từ chối",
                            onClick = { viewModel.tuChoiGiaHan(maDon) },
                            modifier = Modifier.weight(1f),
                            enabled = !state.loading,
                        )
                        PrimaryButton(
                            text = "Duyệt",
                            onClick = { viewModel.duyetGiaHan(maDon) },
                            modifier = Modifier.weight(1f),
                            enabled = !state.loading,
                        )
                    }
                }
            }

            if (don.conNo || don.holdExpiresAt != null || don.cancelReason != null) {
                FormSection("Cần chú ý") {
                    if (don.conNo) InfoRow("Khách còn nợ", don.debtAmount.tien(), valueColor = ColorD54646)
                    don.holdExpiresAt?.takeIf { don.status == gtvt.ktpm.thuexe.data.model.BookingStatus.CHO_CHU_XE }
                        ?.let { InfoRow("Giữ chỗ tới", it.gioNgay(), valueColor = ColorD54646) }
                    don.cancelReason?.let { InfoRow("Lý do", it, valueColor = ColorD54646) }
                }
            }

            FormSection("Tiền") {
                InfoRow("Đơn giá", "${don.pricePerDay.tien()} / ngày")
                InfoRow("Tiền thuê", don.rentTotal.tien())
                InfoRow("Tiền cọc", don.deposit.tien())
                InfoRow("Hoa hồng sàn", don.commission.tien(), valueColor = Color78799C)
                HorizontalDivider(color = ColorCFD0E4, modifier = Modifier.padding(vertical = 6.dp))
                InfoRow(
                    label = "Chủ xe nhận",
                    value = (don.rentTotal - don.commission).tien(),
                    valueColor = Color464BD5,
                )
            }

            FormSection("Khách thuê") {
                val khach = don.renter
                InfoRow("Họ tên", khach?.fullName ?: "—")
                InfoRow(
                    label = "Điện thoại",
                    value = khach?.phone ?: "Hiện sau khi nhận đơn",
                    valueColor = if (khach?.phone == null) Color78799C else Color111245,
                )
                khach?.idDocumentStatus?.let { InfoRow("Giấy tờ", it) }
            }

            state.payment?.let { tt ->
                FormSection("Thanh toán") {
                    InfoRow("Số tiền khách chuyển", tt.amount.tien())
                    InfoRow("Trạng thái", tt.status)
                    tt.receivedAmount?.let { InfoRow("Thực nhận", it.tien()) }
                    tt.confirmedAt?.let { InfoRow("Xác nhận lúc", it.gioNgay()) }

                    tt.transferCode?.let { ma -> MaChuyenKhoan(ma) }
                    tt.qrUrl?.let { url ->
                        Text(
                            text = "Khách quét mã này để chuyển khoản. Số tiền và nội dung đã nằm " +
                                "sẵn trong mã nên khách không phải gõ gì.",
                            style = Chu.Phu.copy(color = Color78799C),
                            modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
                        )
                        AsyncImage(
                            model = url.urlAnh(),
                            contentDescription = "Mã QR chuyển khoản",
                            modifier = Modifier
                                .fillMaxWidth(0.62f)
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
                        if (i > 0) HorizontalDivider(
                            color = ColorCFD0E4,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
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
                            valueColor = if (sc.type == gtvt.ktpm.thuexe.data.model.LoaiSuCo.VA_CHAM) ColorD54646 else Color78799C,
                        )
                        Text(sc.note, style = Chu.Phu.copy(color = Color78799C))
                    }
                }
            }

            if (!don.dangTranhChap && (state.duocMoTranhChap || state.duocBaoSuCo)) {
                FormSection("Cần hỗ trợ?") {
                    if (state.duocBaoSuCo) {
                        SecondaryButton(
                            text = "Báo sự cố giữa chuyến",
                            onClick = { hoiSuCo = true },
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    if (state.duocMoTranhChap) {
                        SecondaryButton(
                            text = "Khách không giữ đúng cam kết? Báo với sàn",
                            onClick = { hoiTranhChap = true },
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }

            Box(Modifier.padding(bottom = 16.dp))
            }
        }
    }

    if (hoiLyDo) {
        LyDoDialog(
            title = "Từ chối đơn",
            confirmText = "Từ chối",
            hintText = "Khách sẽ đọc được lý do này",
            onDismiss = { hoiLyDo = false },
            onConfirm = { lyDo ->
                hoiLyDo = false
                viewModel.tuChoi(maDon, lyDo)
            },
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
        gtvt.ktpm.thuexe.ui.widget.form.BaoSuCoDialog(
            onDismiss = { hoiSuCo = false },
            onConfirm = { ghiChu, vaCham ->
                viewModel.baoSuCo(
                    maDon, ghiChu,
                    if (vaCham) gtvt.ktpm.thuexe.data.model.LoaiSuCo.VA_CHAM
                    else gtvt.ktpm.thuexe.data.model.LoaiSuCo.KHAC,
                )
                hoiSuCo = false
            },
        )
    }
}

/** Dải báo đang chờ bên kia. Có cập nhật thì thông báo đẩy tới và màn tự tải lại. */
@Composable
internal fun DaiChoDoi(loi: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(ColorE8E9F5)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "$loi — sẽ báo khi có cập nhật",
            style = Chu.Phu.copy(color = Color78799C),
        )
    }
}

/** Mã phải khớp từng ký tự trong nội dung chuyển khoản, nên cho bấm chép thay vì gõ lại. */
@Composable
internal fun MaChuyenKhoan(ma: String) {
    val clipboard = LocalClipboardManager.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("Nội dung chuyển khoản", style = Chu.Than.copy(color = Color78799C))
            Text(ma, style = Chu.TieuDeThe.copy(color = Color111245))
        }
        TextButton(onClick = { clipboard.setText(AnnotatedString(ma)) }) {
            Text("Chép", style = Chu.Nut.copy(color = Color464BD5))
        }
    }
}

@Composable
internal fun DongBienBan(bb: Handover) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (bb.kind == gtvt.ktpm.thuexe.data.model.HandoverKind.GIAO) "Biên bản giao"
                else "Biên bản trả",
                style = Chu.TieuDeThe.copy(color = Color111245),
            )
            StatusChip(
                text = nhanTrangThaiBienBan(bb.status),
                color = when (bb.status) {
                    HandoverStatus.DA_KY -> Color464BD5
                    HandoverStatus.PHAN_DOI -> ColorD54646
                    else -> Color78799C
                },
            )
        }
        InfoRow("ODO", "${bb.odo} km")
        InfoRow("Nhiên liệu", "${bb.fuelLevel}/8 vạch")
        InfoRow("Số ảnh", "${bb.photos.size} tấm")
        bb.note?.let { InfoRow("Ghi chú", it) }
        bb.objection?.let { InfoRow("Phản đối", it, valueColor = ColorD54646) }
        bb.reviewedAt?.let { InfoRow("Soi lúc", it.gioNgay()) }
    }
}
