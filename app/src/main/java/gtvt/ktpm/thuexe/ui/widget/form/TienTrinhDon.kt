package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.Primary

private data class Buoc(val nhan: String, val trangThai: Set<String>)

private val CAC_BUOC = listOf(
    Buoc("Chủ xe nhận", setOf(BookingStatus.CHO_CHU_XE)),
    Buoc("Thanh toán", setOf(BookingStatus.CHO_THANH_TOAN)),
    Buoc("Nhận xe", setOf(BookingStatus.DA_XAC_NHAN)),
    Buoc("Đang thuê", setOf(BookingStatus.DANG_THUE)),
    Buoc("Quyết toán", setOf(BookingStatus.CHO_QUYET_TOAN, BookingStatus.CHO_CHI_TRA)),
    Buoc("Hoàn tất", setOf(BookingStatus.HOAN_TAT)),
)

/**
 * Thanh tiến trình của một đơn: khách nhìn là biết đang ở bước nào và đang chờ ai.
 * Đơn đã bị từ chối, huỷ hoặc hết hạn thì không còn tiến trình, chỉ nói rõ kết cục.
 */
@Composable
fun TienTrinhDon(status: String, modifier: Modifier = Modifier) {
    val ketThuc = when (status) {
        BookingStatus.BI_TU_CHOI -> "Chủ xe đã từ chối đơn này"
        BookingStatus.DA_HUY -> "Đơn đã huỷ"
        BookingStatus.HET_HAN -> "Đơn đã hết hạn vì không kịp phản hồi hoặc thanh toán"
        else -> null
    }
    if (ketThuc != null) {
        Text(
            text = ketThuc,
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
            style = Chu.ThanDam.copy(color = ColorD54646),
        )
        return
    }

    // Tranh chấp không phải một bước trong tiến trình — nó dừng tiến trình lại, chưa xong
    // hẳn như huỷ/hết hạn. Không vẽ thanh bước để khỏi ngộ nhận là đang ở bước nào đó.
    if (status == BookingStatus.TRANH_CHAP) {
        Text(
            text = "Đơn đang tranh chấp, chờ sàn xử lý",
            modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
            style = Chu.ThanDam.copy(color = ColorD54646),
        )
        return
    }

    val hienTai = CAC_BUOC.indexOfFirst { status in it.trangThai }.coerceAtLeast(0)
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        CAC_BUOC.forEachIndexed { i, buoc ->
            val xong = i < hienTai || status == BookingStatus.HOAN_TAT
            val dangO = i == hienTai && status != BookingStatus.HOAN_TAT
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        Modifier.weight(1f).height(2.dp)
                            .background(if (i == 0) Color.Transparent else if (i <= hienTai || xong) Primary else ColorCFD0E4),
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .background(if (xong) Primary else Color.White, CircleShape)
                            .border(2.dp, if (xong || dangO) Primary else ColorCFD0E4, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (xong) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        else if (dangO) Box(Modifier.size(8.dp).background(Primary, CircleShape))
                    }
                    Box(
                        Modifier.weight(1f).height(2.dp)
                            .background(if (i == CAC_BUOC.lastIndex) Color.Transparent else if (i < hienTai || xong) Primary else ColorCFD0E4),
                    )
                }
                Text(
                    text = buoc.nhan,
                    modifier = Modifier.padding(top = 4.dp),
                    style = Chu.Phu.copy(
                        color = if (dangO) Color111245 else Color78799C,
                        textAlign = TextAlign.Center,
                    ),
                )
            }
        }
    }
}
