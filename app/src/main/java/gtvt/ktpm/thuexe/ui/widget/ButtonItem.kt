package gtvt.ktpm.thuexe.ui.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ButtonItem(
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
            .padding(horizontal = paddingButton)
            .fillMaxWidth()
            .height(buttonHeight.dp)

    ) {
        // Button
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = textColor
            ),
            modifier = modifier
                .padding(top = 1.dp, start = 1.dp, end = 1.dp)
                .background(gradient, shape = shape)
                .fillMaxWidth()
                .padding(top = 2.dp, start = 2.dp, end = 2.dp)
                .height(buttonHeight.dp)
        ) {
            Text(
                text = nameButton,
                style = MaterialTheme.typography.displayMedium.copy(
                    color = textColor
                )
            )
        }


    }

}