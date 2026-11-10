package gtvt.ktpm.thuexe.ui.screens.onboarding

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.R

import gtvt.ktpm.thuexe.ui.theme.ColorF2564D


@Composable
fun TextUi(
    text: String = "Next",
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = ColorF2564D,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium,
    fontSize: Int = 14,
    onClick: () -> Unit

) {
    TextButton(
        onClick = onClick, colors = ButtonDefaults.textButtonColors(

            contentColor = textColor

        ), shape = RoundedCornerShape(10.dp),
    ) {
        Text(text = text, fontSize = fontSize.sp, style = textStyle.copy(fontFamily = FontFamily(Font(
            R.font.nunito_extrabold))), fontWeight = FontWeight.Bold)
    }
}


@Preview(showBackground = true)
@Composable
fun TextNext(modifier: Modifier = Modifier) {
    TextUi(text = "Tiep") {

    }
}

@Preview(showBackground = true)
@Composable
fun TextBack(modifier: Modifier = Modifier) {
    TextUi(text = "Bo qua") {

    }
}
