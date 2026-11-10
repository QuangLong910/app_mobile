package gtvt.ktpm.thuexe.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryHover,
    onPrimary = Neutral10,
    secondary = Secondary2Hover,
    tertiary = Secondary1Hover,
    background = Neutral10,
    surface = Neutral9,
    onBackground = Neutral2,
    onSurface = Neutral2,
)

// Theo token: nền chính trắng, chữ tiêu đề Neutral 9, chữ nội dung Neutral 8, viền Neutral 4.
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Neutral9,
    primaryContainer = PrimaryTag,
    onPrimaryContainer = PrimaryPressed,
    secondary = Secondary2,
    onSecondary = Neutral1,
    tertiary = Secondary1,
    onTertiary = Neutral1,
    background = Neutral1,
    onBackground = Neutral9,
    surface = Neutral1,
    onSurface = Neutral9,
    surfaceVariant = Neutral3,
    onSurfaceVariant = Neutral7,
    outline = Neutral5,
    outlineVariant = Neutral4,
    error = Error,
    onError = Neutral1,
)

@Composable
fun UIEnglishEgaleTechTheme(
    // Giao diện đen-trắng cố định: không theo chế độ tối của máy, để mọi màn giống nhau.
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    // Primary giờ là vàng sáng nên biểu tượng thanh trạng thái phải tối màu mới đọc được.
    systemUiController.setStatusBarColor(color = PrimaryPressed, darkIcons = true)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}