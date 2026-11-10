package gtvt.ktpm.thuexe.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Bảng màu theo design token "KYN" (Figma). Sửa màu ở đây, đừng gõ mã màu ở nơi khác.

// ── Màu chính (vàng gold) ────────────────────────────────────────────
val Primary = Color(0xFFF9C216)
val PrimaryPressed = Color(0xFFF6B30A)
val PrimaryHover = Color(0xFFFDD947)
val PrimaryBorder = Color(0xFFF9C216)
val PrimaryTag = Color(0xFFFAF5D3)

// ── Màu phụ ──────────────────────────────────────────────────────────
val Secondary1 = Color(0xFF1F3789)          // xanh dương đậm
val Secondary1Pressed = Color(0xFF1E2D6B)
val Secondary1Hover = Color(0xFF1F3CAE)
val Secondary1Border = Color(0xFF1E48D7)
val Secondary1Tag = Color(0xFFDBE9FE)
val Secondary2 = Color(0xFF344258)          // xanh than
val Secondary2Pressed = Color(0xFF2D3748)
val Secondary2Hover = Color(0xFF3B4D69)
val Secondary2Border = Color(0xFF485F81)
val Secondary2Tag = Color(0xFFEBEEF3)
val Secondary3 = Color(0xFF583F34)          // nâu
val Secondary3Pressed = Color(0xFF513A32)
val Secondary3Hover = Color(0xFF6C4E3C)
val Secondary3Border = Color(0xFF9D7B55)
val Secondary3Tag = Color(0xFFEAE4D7)

// ── Trạng thái ───────────────────────────────────────────────────────
val Info = Primary
val InfoTag = PrimaryTag
val Success = Color(0xFF16A34A)
val SuccessPressed = Color(0xFF039732)
val SuccessHover = Color(0xFF4DD077)
val SuccessBorder = Color(0xFFB3EBC5)
val SuccessTag = Color(0xFFEBFAEF)
val Warning = Color(0xFFF59E0C)
val WarningPressed = Color(0xFFD97706)
val WarningHover = Color(0xFFFBBF24)
val WarningBorder = Color(0xFFFCD34D)
val WarningTag = Color(0xFFFFFBEB)
val Error = Color(0xFFEF4444)
val ErrorPressed = Color(0xFFDC2626)
val ErrorHover = Color(0xFFF87171)
val ErrorBorder = Color(0xFFFECACA)
val ErrorTag = Color(0xFFFEF2F2)

// ── Thang xám trung tính ─────────────────────────────────────────────
val Neutral1 = Color(0xFFFFFFFF)   // nền chính
val Neutral2 = Color(0xFFF9FAF9)   // nền phụ 1, tiêu đề bảng
val Neutral3 = Color(0xFFF4F5F4)   // nền phụ 2, đường kẻ 1
val Neutral4 = Color(0xFFE7E7E4)   // nền phụ 3, đường kẻ 2
val Neutral5 = Color(0xFFD6D6D1)   // viền, nền vô hiệu
val Neutral6 = Color(0xFF8D8D8A)   // chữ phụ, vô hiệu
val Neutral7 = Color(0xFF56584D)   // chữ thứ cấp, chú thích
val Neutral8 = Color(0xFF44453B)   // chữ chính, nội dung
val Neutral9 = Color(0xFF24231F)   // tiêu đề
val Neutral10 = Color(0xFF1C1C17)

// ── Khoảng cách và bo góc (dp) ───────────────────────────────────────
object Spacing {
    const val S1 = 4; const val S2 = 8; const val S3 = 12; const val S4 = 16; const val S5 = 24
    const val S6 = 32; const val S7 = 40; const val S8 = 48; const val S9 = 64; const val S10 = 80
}

object Radius {
    const val R1 = 4; const val R2 = 8; const val R3 = 12; const val R4 = 16; const val R5 = 24
    const val R6 = 32; const val R7 = 40; const val PILL = 999
}

// ── Tên cũ: giữ để khỏi sửa hàng chục tệp, nay trỏ về token ─────────────
// Chữ
val Color111245 = Neutral9        // tiêu đề, chữ chính
val Color78799C = Neutral7        // chữ phụ
val ColorFFFFFF = Neutral1
val Color000000 = Color(0xFF000000)
// Nền và viền
val ColorCFD0E4 = Neutral4        // viền, đường kẻ
val ColorEAECF3 = Neutral3        // nền xám nhạt
val ColorE8E9F5 = PrimaryTag      // nền được chọn
val ColorB8C9D3 = Neutral5
// Màu nhấn
val Color464BD5 = Primary
val Color4553B7 = PrimaryPressed  // thanh trạng thái
val ColorF2564D = Primary         // con trỏ ô nhập
val Color282BA0 = PrimaryPressed
val Color20227E = PrimaryPressed
val Color242956 = Neutral9
val Color1D2145 = Neutral9
val Color353A75 = Neutral8
// Trạng thái
val ColorD54646 = Error
val Color9E3332 = ErrorPressed
val ColorFF9064 = Warning
// Bảng màu Material
val Color6650a4 = Primary
val Color625b71 = Secondary2
val Color7D5260 = Neutral7
val ColorD0BCFF = PrimaryHover
val ColorCCC2DC = PrimaryBorder
val ColorEFB8C8 = PrimaryTag

/** Nút chính: màu chính đặc, đúng token (không dùng dải màu). */
val gradient = Brush.horizontalGradient(colors = listOf(Primary, Primary))

/** Nền cho khối lớn có chữ trắng (banner, ảnh đại diện): Primary giờ là vàng sáng, chữ trắng
 * lên đó không đọc được, nên các mặt này dùng Secondary 1 (xanh dương đậm) thay vì gradient. */
val heroGradient = Brush.horizontalGradient(colors = listOf(Secondary1, Secondary1))

/** Nền nhãn (Tag Background) đi cùng một màu chữ theo token; màu lạ thì dùng nền xám nhạt. */
fun nenNhan(mau: Color): Color = when (mau) {
    Primary -> PrimaryTag
    Success -> SuccessTag
    Warning -> WarningTag
    Error, ErrorPressed -> ErrorTag
    Secondary1 -> Secondary1Tag
    Secondary2 -> Secondary2Tag
    Secondary3 -> Secondary3Tag
    else -> Neutral3
}
