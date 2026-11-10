package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import java.time.LocalDate
import java.time.YearMonth

enum class TrangThaiNgay { TRONG, CO_DON, KHOA, QUA_KHU }

/**
 * Lịch một tháng của một xe. Ngày có đơn thì không cho đụng vào — nhả lịch phải đi qua huỷ đơn,
 * không phải bằng cách mở khoá ở đây.
 */
@Composable
fun LichThang(
    thang: YearMonth,
    trangThai: (LocalDate) -> TrangThaiNgay,
    onDoiThang: (YearMonth) -> Unit,
    onChonNgay: (LocalDate) -> Unit,
    dangChon: Set<LocalDate>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { onDoiThang(thang.minusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBackIos, "Tháng trước", tint = Color111245)
            }
            Text(
                text = "Tháng %d / %d".format(thang.monthValue, thang.year),
                style = Chu.TieuDeThe.copy(color = Color111245),
            )
            IconButton(onClick = { onDoiThang(thang.plusMonths(1)) }) {
                Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, "Tháng sau", tint = Color111245)
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)) {
            listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN").forEach {
                Text(
                    text = it,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = Chu.Phu.copy(color = Color78799C),
                )
            }
        }

        val ngayDau = thang.atDay(1)
        val oTrong = ngayDau.dayOfWeek.value - 1      // thứ hai là cột đầu
        val soNgay = thang.lengthOfMonth()
        val tongO = oTrong + soNgay
        val soHang = (tongO + 6) / 7

        repeat(soHang) { hang ->
            Row(modifier = Modifier.fillMaxWidth()) {
                repeat(7) { cot ->
                    val thu = hang * 7 + cot - oTrong + 1
                    if (thu < 1 || thu > soNgay) {
                        Box(Modifier.weight(1f).aspectRatio(1f))
                    } else {
                        val ngay = thang.atDay(thu)
                        ONgay(
                            ngay = ngay,
                            trangThai = trangThai(ngay),
                            dangChon = ngay in dangChon,
                            onClick = { onChonNgay(ngay) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ChuThich("Trống", Color.White)
            ChuThich("Có đơn", Color464BD5)
            ChuThich("Đã khoá", ColorD54646)
        }
    }
}

@Composable
private fun ONgay(
    ngay: LocalDate,
    trangThai: TrangThaiNgay,
    dangChon: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val nen = when (trangThai) {
        TrangThaiNgay.CO_DON -> Color464BD5.copy(alpha = 0.18f)
        TrangThaiNgay.KHOA -> ColorD54646.copy(alpha = 0.15f)
        TrangThaiNgay.QUA_KHU -> ColorCFD0E4.copy(alpha = 0.35f)
        TrangThaiNgay.TRONG -> Color.White
    }
    val chu = when (trangThai) {
        TrangThaiNgay.CO_DON -> Color464BD5
        TrangThaiNgay.KHOA -> ColorD54646
        TrangThaiNgay.QUA_KHU -> Color78799C
        TrangThaiNgay.TRONG -> Color111245
    }
    val doiDuoc = trangThai == TrangThaiNgay.TRONG || trangThai == TrangThaiNgay.KHOA

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(nen)
            .border(
                width = if (dangChon) 2.dp else 1.dp,
                color = if (dangChon) Color111245 else ColorCFD0E4,
                shape = RoundedCornerShape(8.dp),
            )
            .clickable(enabled = doiDuoc, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = ngay.dayOfMonth.toString(),
            style = Chu.Than.copy(color = chu),
        )
    }
}

@Composable
private fun ChuThich(text: String, mau: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Ô màu phải có kích thước cố định: chỉ aspectRatio thôi thì trong Row cao tự do
        // nó phình ra chiếm hết chỗ và đẩy chữ văng đi.
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (mau == Color.White) Color.White else mau.copy(alpha = 0.18f))
                .border(
                    width = 1.dp,
                    color = if (mau == Color.White) ColorCFD0E4 else mau,
                    shape = RoundedCornerShape(3.dp),
                ),
        )
        Text(
            text = text,
            modifier = Modifier.padding(start = 6.dp),
            style = Chu.Phu.copy(color = Color78799C),
        )
    }
}
