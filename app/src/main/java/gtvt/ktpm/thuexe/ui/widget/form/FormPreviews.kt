package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.data.model.Fuel
import gtvt.ktpm.thuexe.data.model.Transmission
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
import java.time.LocalDate

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun FormWidgetsPreview() {
    UIEnglishEgaleTechTheme {
        var chu by remember { mutableStateOf("") }
        var tien by remember { mutableStateOf(600_000L) }
        var so by remember { mutableStateOf(5) }
        var hopSo by remember { mutableStateOf<String?>(Transmission.SO_TU_DONG) }
        var ngay by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorEAECF3)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            FormSection("Ô nhập") {
                FormTextField("Biển số", chu, { chu = it }, hintText = "51A-123.45", required = true)
                FormTextField("Có lỗi", "", {}, error = "Chưa nhập biển số")
                FormMoneyField("Giá thuê một ngày", tien, { tien = it }, required = true)
                FormIntField("Số chỗ", so, { so = it }, suffix = "chỗ")
                FormDropdownField(
                    label = "Hộp số",
                    value = hopSo,
                    options = listOf(
                        Transmission.SO_SAN to nhanHopSo(Transmission.SO_SAN),
                        Transmission.SO_TU_DONG to nhanHopSo(Transmission.SO_TU_DONG),
                    ),
                    onSelected = { hopSo = it },
                )
                FormDateField("Ngày hết hạn GPLX", ngay, { ngay = it })
            }

            FormSection("Chỉ đọc") {
                InfoRow("Tiền thuê", 1_800_000L.tien())
                InfoRow("Hoa hồng sàn", "− " + 270_000L.tien())
                InfoRow("Nhiên liệu", nhanNhienLieu(Fuel.XANG))
            }

            FormSection("Trạng thái") {
                BookingStatusChip(BookingStatus.CHO_CHU_XE)
                BookingStatusChip(BookingStatus.DANG_THUE, Modifier.padding(top = 6.dp))
                CarStatusChip(CarStatus.DANG_BAN, Modifier.padding(top = 6.dp))
            }

            ErrorBanner("SLOT_TAKEN")
            PrimaryButton("Nhận đơn", {}, Modifier.padding(top = 8.dp))
            SecondaryButton("Từ chối", {}, Modifier.padding(top = 8.dp))
            PrimaryButton("Đang tắt", {}, Modifier.padding(top = 8.dp), enabled = false)
        }
    }
}
