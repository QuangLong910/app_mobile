package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.data.model.PayoutStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064

private val XANH = gtvt.ktpm.thuexe.ui.theme.Success

@Composable
fun StatusChip(text: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(gtvt.ktpm.thuexe.ui.theme.nenNhan(color), RoundedCornerShape(gtvt.ktpm.thuexe.ui.theme.Radius.R1.dp + 2.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp),
        style = Chu.Nhan.copy(color = color),
    )
}

@Composable
fun BookingStatusChip(status: String, modifier: Modifier = Modifier) {
    val color = when (status) {
        BookingStatus.CHO_CHU_XE -> gtvt.ktpm.thuexe.ui.theme.Warning
        BookingStatus.CHO_THANH_TOAN -> gtvt.ktpm.thuexe.ui.theme.Warning
        BookingStatus.DA_XAC_NHAN, BookingStatus.DANG_THUE -> Color464BD5
        BookingStatus.CHO_QUYET_TOAN, BookingStatus.CHO_CHI_TRA -> Color111245
        BookingStatus.HOAN_TAT -> XANH
        BookingStatus.BI_TU_CHOI, BookingStatus.DA_HUY, BookingStatus.HET_HAN -> Color78799C
        BookingStatus.TRANH_CHAP -> ColorD54646
        else -> Color78799C
    }
    StatusChip(nhanTrangThaiDon(status), color, modifier)
}

@Composable
fun CarStatusChip(status: String, modifier: Modifier = Modifier) {
    val color = when (status) {
        CarStatus.DANG_BAN -> XANH
        CarStatus.CHO_DUYET -> gtvt.ktpm.thuexe.ui.theme.Warning
        CarStatus.NHAP -> Color78799C
        CarStatus.AN -> ColorD54646
        else -> Color78799C
    }
    StatusChip(nhanTrangThaiXe(status), color, modifier)
}

@Composable
fun PayoutStatusChip(status: String, modifier: Modifier = Modifier) {
    val color = when (status) {
        PayoutStatus.DA_CHI -> XANH
        PayoutStatus.CHO -> gtvt.ktpm.thuexe.ui.theme.Warning
        PayoutStatus.LOI -> ColorD54646
        else -> Color78799C
    }
    StatusChip(nhanTrangThaiChiTra(status), color, modifier)
}
