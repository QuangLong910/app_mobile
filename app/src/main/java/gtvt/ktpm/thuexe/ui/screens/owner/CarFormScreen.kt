package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.data.model.CarUpsertRequest
import gtvt.ktpm.thuexe.data.model.Fuel
import gtvt.ktpm.thuexe.data.model.PhotoSlot
import gtvt.ktpm.thuexe.data.model.Transmission
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormDropdownField
import gtvt.ktpm.thuexe.ui.widget.form.FormIntField
import gtvt.ktpm.thuexe.ui.widget.form.FormMoneyField
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.FormTextField
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PhotoSlotBox
import gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton
import gtvt.ktpm.thuexe.ui.widget.form.nhanKhungAnh
import gtvt.ktpm.thuexe.ui.widget.form.nhanNhienLieu
import gtvt.ktpm.thuexe.ui.widget.form.nhanHopSo
import kotlinx.coroutines.launch
import java.time.LocalDate

private val HOP_SO = listOf(Transmission.SO_SAN, Transmission.SO_TU_DONG).map { it to nhanHopSo(it) }
private val NHIEN_LIEU = listOf(Fuel.XANG, Fuel.DAU, Fuel.DIEN).map { it to nhanNhienLieu(it) }

/** carId = null là đăng xe mới; khác null là sửa xe đã có. */
@Composable
fun CarFormScreen(
    carId: Long? = null,
    onBack: () -> Unit = {},
    onSaved: () -> Unit = {},
    viewModel: OwnerCarsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val scroll = rememberScrollState()

    var bienSo by remember { mutableStateOf("") }
    var hang by remember { mutableStateOf("") }
    var dong by remember { mutableStateOf("") }
    var namSx by remember { mutableStateOf(0) }
    var soCho by remember { mutableStateOf(0) }
    var hopSo by remember { mutableStateOf<String?>(null) }
    var nhienLieu by remember { mutableStateOf<String?>(null) }
    var odo by remember { mutableStateOf(0) }
    var quanHuyen by remember { mutableStateOf("") }
    var diaChiGiao by remember { mutableStateOf("") }
    var giaNgay by remember { mutableStateOf(0L) }
    var kmToiDa by remember { mutableStateOf(300) }
    var coc by remember { mutableStateOf(15_000_000L) }
    var moTa by remember { mutableStateOf("") }
    var anh by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var daBam by remember { mutableStateOf(false) }

    // Nạp sẵn khi sửa xe.
    LaunchedEffect(carId, state.cars) {
        val xe = state.cars.firstOrNull { it.id == carId } ?: return@LaunchedEffect
        bienSo = xe.plate; hang = xe.brand; dong = xe.model
        namSx = xe.year; soCho = xe.seats; hopSo = xe.transmission; nhienLieu = xe.fuel
        odo = xe.odo; quanHuyen = xe.district; diaChiGiao = xe.pickupAddress.orEmpty()
        giaNgay = xe.pricePerDay; kmToiDa = xe.maxKmDay; coc = xe.deposit
        moTa = xe.description.orEmpty()
        anh = xe.photos.sortedBy { it.sortOrder }.mapIndexed { i, p ->
            PhotoSlot.BAT_BUOC.getOrElse(i) { PhotoSlot.KHAC } to p.url
        }.toMap()
    }

    val namNay = LocalDate.now().year
    fun loiBienSo() = if (daBam && bienSo.isBlank()) "Chưa nhập biển số" else null
    fun loiNam() = when {
        !daBam -> null
        namSx == 0 -> "Chưa nhập năm sản xuất"
        namSx !in 1980..namNay -> "Năm phải trong khoảng 1980–$namNay"
        else -> null
    }
    fun loiGia() = when {
        !daBam -> null
        giaNgay <= 0 -> "Chưa nhập giá thuê"
        giaNgay < 100_000 -> "Giá thấp bất thường, kiểm tra lại"
        else -> null
    }

    val hopLe = bienSo.isNotBlank() && hang.isNotBlank() && dong.isNotBlank() &&
        namSx in 1980..namNay && soCho > 0 && hopSo != null && nhienLieu != null &&
        quanHuyen.isNotBlank() && diaChiGiao.isNotBlank() && giaNgay >= 100_000 && anh.isNotEmpty()

    OwnerScaffold(
        title = if (carId == null) "Đăng xe" else "Sửa xe",
        onBack = onBack,
        bottomBar = {
            PrimaryButton(
                text = if (carId == null) "Gửi duyệt" else "Lưu thay đổi",
                enabled = !state.loading,
                modifier = Modifier.padding(vertical = 8.dp),
                onClick = {
                    daBam = true
                    if (!hopLe) return@PrimaryButton
                    val body = CarUpsertRequest(
                        plate = bienSo.trim().uppercase(),
                        brand = hang.trim(),
                        model = dong.trim(),
                        year = namSx,
                        seats = soCho,
                        transmission = hopSo!!,
                        fuel = nhienLieu!!,
                        odo = odo,
                        district = quanHuyen.trim(),
                        pickupAddress = diaChiGiao.trim(),
                        pricePerDay = giaNgay,
                        maxKmDay = kmToiDa,
                        deposit = coc,
                        description = moTa.trim().ifBlank { null },
                        photoUrls = PhotoSlot.BAT_BUOC.mapNotNull { anh[it] },
                    )
                    if (carId == null) viewModel.createCar(body) else viewModel.updateCar(carId, body)
                    onSaved()
                },
            )
        },
    ) { padding ->
        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            FormSection("Nhận dạng xe") {
                FormTextField(
                    label = "Biển số", value = bienSo, onValueChange = { bienSo = it },
                    hintText = "51A-123.45", required = true, error = loiBienSo(),
                )
                Row(modifier = Modifier.fillMaxWidth()) {
                    FormTextField(
                        label = "Hãng", value = hang, onValueChange = { hang = it },
                        hintText = "Toyota", required = true, modifier = Modifier.weight(1f),
                    )
                    Spacer(Modifier.width(8.dp))
                    FormTextField(
                        label = "Dòng xe", value = dong, onValueChange = { dong = it },
                        hintText = "Vios", required = true, modifier = Modifier.weight(1f),
                    )
                }
                FormIntField(
                    label = "Năm sản xuất", value = namSx, onValueChange = { namSx = it },
                    required = true, error = loiNam(),
                )
                FormIntField(
                    label = "Số chỗ", value = soCho, onValueChange = { soCho = it },
                    suffix = "chỗ", required = true,
                )
                FormDropdownField(
                    label = "Hộp số", value = hopSo, options = HOP_SO,
                    onSelected = { hopSo = it }, required = true,
                )
                FormDropdownField(
                    label = "Nhiên liệu", value = nhienLieu, options = NHIEN_LIEU,
                    onSelected = { nhienLieu = it }, required = true,
                )
                FormIntField(
                    label = "Số ODO hiện tại", value = odo, onValueChange = { odo = it }, suffix = "km",
                )
            }

            FormSection("Giao xe ở đâu") {
                FormTextField(
                    label = "Quận / huyện", value = quanHuyen, onValueChange = { quanHuyen = it },
                    hintText = "Quận 1", required = true,
                )
                FormTextField(
                    label = "Địa chỉ giao xe", value = diaChiGiao, onValueChange = { diaChiGiao = it },
                    hintText = "Số nhà, đường, phường", required = true,
                    hint = "Chỉ mở cho khách sau khi đơn đã xác nhận",
                    singleLine = false, minLines = 2,
                )
            }

            FormSection("Giá và giới hạn") {
                FormMoneyField(
                    label = "Giá thuê một ngày", value = giaNgay, onValueChange = { giaNgay = it },
                    required = true, error = loiGia(),
                )
                FormIntField(
                    label = "Số km tối đa mỗi ngày", value = kmToiDa, onValueChange = { kmToiDa = it },
                    suffix = "km", hint = "Vượt mức này khách trả thêm 5.000 đ mỗi km",
                )
                FormMoneyField(
                    label = "Tiền cọc", value = coc, onValueChange = { coc = it },
                    hint = "Bạn tự đặt (gợi ý 15.000.000 đ). Khách chuyển cọc cùng tiền thuê, sàn giữ hộ và hoàn sau khi trả xe. Đổi cọc chỉ áp dụng cho đơn mới.",
                )
                FormTextField(
                    label = "Mô tả", value = moTa, onValueChange = { moTa = it },
                    hintText = "Xe gia đình, mới bảo dưỡng…",
                    singleLine = false, minLines = 3, keyboardType = KeyboardType.Text,
                )
            }

            FormSection("Ảnh xe") {
                PhotoSlot.BAT_BUOC.chunked(2).forEach { hangAnh ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        hangAnh.forEachIndexed { i, slot ->
                            if (i > 0) Spacer(Modifier.width(8.dp))
                            PhotoSlotBox(
                                label = nhanKhungAnh(slot),
                                url = anh[slot],
                                scope = scope,
                                modifier = Modifier.weight(1f),
                                onPicked = { bytes ->
                                    scope.launch {
                                        viewModel.uploadPhoto(bytes, "$slot.jpg")?.let { url ->
                                            anh = anh + (slot to url)
                                        }
                                    }
                                },
                            )
                        }
                    }
                }
                if (daBam && anh.isEmpty()) {
                    FormTextField(
                        label = "", value = "", onValueChange = {},
                        error = "Cần ít nhất một ảnh xe", enabled = false,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
