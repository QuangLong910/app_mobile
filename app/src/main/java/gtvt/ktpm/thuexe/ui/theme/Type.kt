package gtvt.ktpm.thuexe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.R

// Set of Material typography styles to start with
val Typography = Typography(
// displayLarge
    displayLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
        fontWeight = FontWeight.W800,
        fontSize = 34.sp,
        lineHeight = 37.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// displayMedium
    displayMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
        fontWeight = FontWeight.W700,
        fontSize = 24.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// displaySmall
    displaySmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_semibold)),
        fontWeight = FontWeight.W300,
        fontSize = 11.sp,
        lineHeight = 12.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),

// headlineLarge
// headlineMedium
    headlineMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_bold)),
        fontWeight = FontWeight.W600,
        fontSize = 17.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// headlineSmall

// titleLarge
    titleLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
        fontWeight = FontWeight.W800,
        fontSize = 17.sp,
        lineHeight = 19.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// titleMedium
    titleMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
        fontWeight = FontWeight.W600,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// titleSmall
    titleSmall = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 11.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),

// bodyLarge
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 17.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// bodyMedium
    bodyMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Center
    ),
// bodySmall

// labelLarge
// labelMedium
    labelMedium = TextStyle(
        fontFamily = FontFamily(Font(R.font.nunito_regular)),
        fontWeight = FontWeight.W400,
        fontSize = 17.sp,
        lineHeight = 21.sp,
        letterSpacing = 0.sp,
        textAlign = TextAlign.Start
    ),

// labelSmall

)