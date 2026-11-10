package gtvt.ktpm.thuexe.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.R

private val Nunito = FontFamily(Font(R.font.nunito_regular))
private val NunitoMedium = FontFamily(Font(R.font.nunito_medium))
private val NunitoSemiBold = FontFamily(Font(R.font.nunito_semibold))
private val NunitoBold = FontFamily(Font(R.font.nunito_bold))
private val NunitoExtraBold = FontFamily(Font(R.font.nunito_extrabold))

/**
 * Thang chữ riêng cho app chủ xe.
 *
 * Typography của theme là của bản mẫu app học tiếng Anh: labelMedium 17sp, displayMedium 24sp
 * căn giữa, displaySmall 11sp W300 căn giữa. Dùng chúng cho danh sách và biểu mẫu thì chữ to
 * như sách chữ lớn, thẻ dài lê thê và chữ căn giữa lạc chỗ. Mọi màn của chủ xe dùng bộ này.
 */
object Chu {
    /** Tiêu đề trên thanh trên cùng. */
    val TieuDeMan = TextStyle(
        fontFamily = NunitoExtraBold, fontWeight = FontWeight.W800,
        fontSize = 18.sp, lineHeight = 22.sp, textAlign = TextAlign.Start,
    )

    /** Tiêu đề một thẻ: mã đơn, tên xe, tiêu đề tin. */
    val TieuDeThe = TextStyle(
        fontFamily = NunitoExtraBold, fontWeight = FontWeight.W800,
        fontSize = 16.sp, lineHeight = 21.sp, textAlign = TextAlign.Start,
    )

    /** Chữ thân: nhãn và giá trị trong biểu mẫu, danh sách. */
    val Than = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.W400,
        fontSize = 14.sp, lineHeight = 19.sp, textAlign = TextAlign.Start,
    )

    /** Giá trị cần nhấn trong một dòng nhãn–giá trị. */
    val ThanDam = TextStyle(
        fontFamily = NunitoSemiBold, fontWeight = FontWeight.W600,
        fontSize = 14.sp, lineHeight = 19.sp, textAlign = TextAlign.Start,
    )

    /** Chữ phụ: gợi ý dưới ô nhập, ngày đăng, dòng mô tả. */
    val Phu = TextStyle(
        fontFamily = Nunito, fontWeight = FontWeight.W400,
        fontSize = 12.5.sp, lineHeight = 17.sp, textAlign = TextAlign.Start,
    )

    /** Nhãn nhóm và chữ trong chip: nhỏ, đậm, giãn chữ. */
    val Nhan = TextStyle(
        fontFamily = NunitoBold, fontWeight = FontWeight.W700,
        fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.4.sp, textAlign = TextAlign.Start,
    )

    /** Con số to trên thẻ số liệu. */
    val SoLon = TextStyle(
        fontFamily = NunitoExtraBold, fontWeight = FontWeight.W800,
        fontSize = 22.sp, lineHeight = 26.sp, textAlign = TextAlign.Start,
    )

    /** Số tiền cần nhấn mạnh. */
    val Tien = TextStyle(
        fontFamily = NunitoExtraBold, fontWeight = FontWeight.W800,
        fontSize = 17.sp, lineHeight = 22.sp, textAlign = TextAlign.Start,
    )

    /** Chữ trên nút. */
    val Nut = TextStyle(
        fontFamily = NunitoBold, fontWeight = FontWeight.W700,
        fontSize = 15.sp, lineHeight = 19.sp, textAlign = TextAlign.Center,
    )

    /** Nhãn thẻ ở thanh dưới. */
    val NhanThe = TextStyle(
        fontFamily = NunitoMedium, fontWeight = FontWeight.W500,
        fontSize = 11.sp, lineHeight = 13.sp, textAlign = TextAlign.Center,
    )
}
