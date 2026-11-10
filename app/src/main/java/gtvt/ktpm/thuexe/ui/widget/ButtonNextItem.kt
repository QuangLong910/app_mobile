package gtvt.ktpm.thuexe.ui.widget
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.ColorFFFFFF

@Composable
fun ButtonNextItem(
    modifier: Modifier = Modifier,
    nameButton: String,
    textColor: Color,
    gradient: Brush,
    shadowColor: Color,
    paddingButton: Dp,
    shadowBottomOffset: Float,
    buttonHeight: Float = 50f,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .background(color = shadowColor, shape = shape)
            .height(buttonHeight.dp + shadowBottomOffset.dp)
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        // Button
        Box(
            modifier = modifier
                .padding(top = 1.dp, start = 1.dp, end = 1.dp, bottom = shadowBottomOffset.dp)
                .background(gradient, shape = shape)
                .fillMaxWidth()
                .height(buttonHeight.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nameButton,
                style = TextStyle(
                    fontFamily = FontFamily(Font(R.font.nunito_extrabold)),
                    fontSize = 14.sp
                ),
                color = textColor
            )
        }


    }

}