package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.data.AnhNen
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.data.model.HandoverStatus
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.widget.form.ChuKyField
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.InfoRow
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.LyDoDialog
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.SecondaryButton
import gtvt.ktpm.thuexe.ui.widget.form.nhanKhungAnh
import gtvt.ktpm.thuexe.ui.widget.form.urlAnh
import kotlinx.coroutines.launch

/**
 * Chủ xe soi biên bản TRA do khách lập. Nút ký chỉ bật sau khi đã mở xem đủ sáu khung —
 * đây là chỗ quyết định biên bản có giá trị hay không, không phải chi tiết giao diện.
 *
 * Ký xong đơn sang CHO_QUYET_TOAN; phản đối thì đơn dừng chờ người vận hành xử.
 */
@Composable
fun HandoverSoiScreen(
    maDon: String,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
    /** Loại biên bản đang soi: chủ xe soi TRA, khách soi GIAO. */
    kind: String = HandoverKind.TRA,
    viewModel: HandoverViewModel = hiltViewModel(),
) {
    val laTra = kind == HandoverKind.TRA
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scroll = rememberScrollState()

    var dangXem by remember { mutableStateOf<Int?>(null) }
    var hoiPhanDoi by remember { mutableStateOf(false) }

    LaunchedEffect(maDon, kind) { viewModel.taiBienBan(maDon, kind) }

    // Chỉ rời màn khi backend đã nhận. Trước đây gọi ky() rồi onDone() ngay trong một
    // lần bấm, nên ký hỏng vẫn thoát — người ký tưởng xong mà biên bản còn nguyên CHO_SOI.
    LaunchedEffect(state.daHoanTat) { if (state.daHoanTat) onDone() }

    val bienBan = state.handover
    val daXong = bienBan?.status != null && bienBan.status != HandoverStatus.CHO_SOI
    val daXem = PhotoSlot.BAT_BUOC.count { it in state.seenSlots }

    val chonAnh = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        val id = bienBan?.id ?: return@rememberLauncherForActivityResult
        if (uri != null) scope.launch {
            AnhNen.tuUri(context, uri)?.let { viewModel.boSungMotAnh(id, it, "Chủ xe chụp bổ sung") }
        }
    }

    OwnerScaffold(
        title = if (laTra) "Soi biên bản trả xe" else "Soi biên bản giao xe",
        onBack = onBack,
        bottomBar = {
            if (bienBan != null && !daXong) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SecondaryButton(
                        text = "Phản đối",
                        onClick = { hoiPhanDoi = true },
                        modifier = Modifier.weight(1f),
                        enabled = !state.loading,
                    )
                    PrimaryButton(
                        text = when {
                            !state.duocKy -> "Xem đủ 6 ảnh đã"
                            state.dangTaiChuKy -> "Đang lưu chữ ký…"
                            else -> "Ký xác nhận"
                        },
                        onClick = {
                            val chuKy = state.signatureUrl ?: return@PrimaryButton
                            viewModel.ky(bienBan.id, chuKy)
                        },
                        modifier = Modifier.weight(1f),
                        enabled = state.duocKy && state.chuKyDaLuu && !state.loading,
                    )
                }
            }
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)
            if (bienBan == null) {
                if (state.loading) LoadingBox()
                return@Column
            }

            FormSection(if (laTra) "Khách ghi nhận" else "Chủ xe ghi nhận") {
                InfoRow(if (laTra) "ODO lúc trả" else "ODO lúc giao", "${bienBan.odo} km")
                InfoRow("Nhiên liệu", "${bienBan.fuelLevel}/8 vạch")
                bienBan.note?.let { InfoRow(if (laTra) "Ghi chú của khách" else "Ghi chú của chủ xe", it) }
                bienBan.objection?.let { InfoRow("Phản đối", it, valueColor = ColorD54646) }
            }

            FormSection("Sáu khung ảnh — đã xem $daXem/6") {
                if (!daXong) {
                    LinearProgressIndicator(
                        progress = { daXem / 6f },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        color = Color464BD5,
                        trackColor = ColorCFD0E4,
                    )
                    Text(
                        text = "Vuốt ngang để xem lần lượt sáu ảnh, chạm vào ảnh nếu muốn phóng to. Xem đủ sáu ảnh thì mới ký được.",
                        style = Chu.Phu.copy(color = Color78799C),
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                AnhVuotXem(
                    photos = state.photos,
                    daXem = state.seenSlots,
                    onXem = { viewModel.markSeen(it) },
                    onPhongTo = { vitri -> dangXem = vitri },
                )
            }

            val boSung = bienBan.photos.filter { it.slot !in PhotoSlot.BAT_BUOC }
            FormSection("Ảnh bổ sung${if (boSung.isEmpty()) "" else " (${boSung.size})"}") {
                Text(
                    text = "Thấy vết hoặc hư hỏng mà " + (if (laTra) "khách" else "chủ xe") + " chưa chụp thì bổ sung vào đây trước khi ký. " +
                        "Ảnh bổ sung là căn cứ để người vận hành tính phí.",
                    style = Chu.Phu.copy(color = Color78799C),
                )
                boSung.forEach { anh ->
                    AsyncImage(
                        model = anh.url.urlAnh(),
                        contentDescription = anh.note,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(4f / 3f)
                            .padding(top = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White),
                        contentScale = ContentScale.Crop,
                    )
                }
                if (!daXong) {
                    TextButton(
                        onClick = {
                            chonAnh.launch(
                                androidx.activity.result.PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                        },
                        enabled = !state.loading,
                    ) {
                        Text("+ Chụp bổ sung", style = Chu.Nut.copy(color = Color464BD5))
                    }
                }
            }

            if (!daXong) {
                FormSection(if (laTra) "Chữ ký của chủ xe" else "Chữ ký của bạn") {
                    ChuKyField(
                        label = "Ký để xác nhận đã nhận xe",
                        daKyUrl = state.signatureUrl,
                        onXongKy = { viewModel.addSignature(it) },
                        required = true,
                        enabled = state.duocKy && !state.loading,
                        dangTaiLen = state.dangTaiChuKy,
                        loiTaiLen = state.loiChuKy,
                        hint = if (state.duocKy) null else "Xem hết sáu ảnh rồi mới ký được",
                    )
                }
            } else {
                FormSection("Kết quả") {
                    InfoRow(
                        label = "Trạng thái",
                        value = if (bienBan.status == HandoverStatus.DA_KY) "Đã ký, đơn chờ quyết toán"
                        else "Đã phản đối, chờ người vận hành xử lý",
                        valueColor = if (bienBan.status == HandoverStatus.DA_KY) Color464BD5 else ColorD54646,
                    )
                }
            }

            Box(Modifier.padding(bottom = 16.dp))
        }
    }

    dangXem?.let { batDau ->
        XemAnhToanMan(
            slots = PhotoSlot.BAT_BUOC,
            photos = state.photos,
            batDau = batDau,
            onXem = { slot -> viewModel.markSeen(slot) },
            onDong = { dangXem = null },
        )
    }

    if (hoiPhanDoi) {
        LyDoDialog(
            title = "Phản đối biên bản",
            confirmText = "Gửi phản đối",
            hintText = if (laTra) "Ghi rõ hư hỏng hoặc sai lệch so với lúc giao xe" else "Ghi rõ điểm chưa đúng so với tình trạng thật của xe",
            onDismiss = { hoiPhanDoi = false },
            onConfirm = { lyDo ->
                hoiPhanDoi = false
                viewModel.phanDoi(bienBan?.id ?: return@LyDoDialog, lyDo)
            },
        )
    }
}

/**
 * Sáu ảnh vuốt ngang ngay trên màn, không phải mở từng tấm. Trang nào dừng lại (không phải
 * lướt ngang qua) thì tính là đã xem khung đó; chạm vào ảnh nếu muốn phóng to.
 */
@Composable
private fun AnhVuotXem(
    photos: Map<String, String>,
    daXem: Set<String>,
    onXem: (String) -> Unit,
    onPhongTo: (Int) -> Unit,
) {
    val slots = PhotoSlot.BAT_BUOC
    val pager = rememberPagerState { slots.size }

    // settledPage chỉ đổi khi trang đã dừng hẳn: lướt nhanh ngang qua không tính là đã xem.
    LaunchedEffect(pager) {
        snapshotFlow { pager.settledPage }.collect { onXem(slots[it]) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = nhanKhungAnh(slots[pager.currentPage]),
                style = Chu.TieuDeThe.copy(color = Color111245),
            )
            Text(
                text = "${pager.currentPage + 1}/${slots.size}",
                style = Chu.Than.copy(color = Color78799C),
            )
        }

        HorizontalPager(
            state = pager,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 8.dp,
        ) { trang ->
            val slot = slots[trang]
            val url = photos[slot]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(1.dp, ColorCFD0E4, RoundedCornerShape(12.dp))
                    .clickable { onPhongTo(trang) },
            ) {
                if (url != null) {
                    AsyncImage(
                        model = url.urlAnh(),
                        contentDescription = nhanKhungAnh(slot),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = "Thiếu ảnh",
                        style = Chu.Phu.copy(color = ColorD54646),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }

        // Sáu chấm: xanh là đã xem, xám là chưa. Chạm chấm để nhảy tới khung đó.
        val scope = rememberCoroutineScope()
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            slots.forEachIndexed { i, slot ->
                val xong = slot in daXem
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .size(if (i == pager.currentPage) 12.dp else 9.dp)
                        .background(if (xong) Color464BD5 else ColorCFD0E4, RoundedCornerShape(50))
                        .clickable { scope.launch { pager.animateScrollToPage(i) } },
                )
            }
        }
    }
}

/** Xem to, vuốt ngang. Trang nào dừng lại là tính đã xem khung đó. */
@Composable
private fun XemAnhToanMan(
    slots: List<String>,
    photos: Map<String, String>,
    batDau: Int,
    onXem: (String) -> Unit,
    onDong: () -> Unit,
) {
    val pager = rememberPagerState(initialPage = batDau) { slots.size }

    LaunchedEffect(pager) {
        snapshotFlow { pager.currentPage }.collect { onXem(slots[it]) }
    }

    Dialog(
        onDismissRequest = onDong,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color(0xE6000000))) {
            HorizontalPager(state = pager, modifier = Modifier.fillMaxSize()) { trang ->
                val url = photos[slots[trang]]
                if (url != null) {
                    AsyncImage(
                        model = url.urlAnh(),
                        contentDescription = nhanKhungAnh(slots[trang]),
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = "Khung ${nhanKhungAnh(slots[trang])} chưa có ảnh",
                        style = Chu.Than.copy(color = Color.White),
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${nhanKhungAnh(slots[pager.currentPage])}  ${pager.currentPage + 1}/${slots.size}",
                    style = Chu.TieuDeThe.copy(color = Color.White),
                )
                TextButton(onClick = onDong) {
                    Text("Đóng", style = Chu.Nut.copy(color = Color.White))
                }
            }
        }
    }
}
