package gtvt.ktpm.thuexe.ui.widget


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.ui.theme.Color000000
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color20227E
import gtvt.ktpm.thuexe.ui.theme.Color78799C



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputOutlineText(
    modifier: Modifier = Modifier,
    value: String,
    keyboardOptions: KeyboardOptions,
    hintText: String,
    icon: ImageVector? = null,
    cursorColor: Color,
    isCentered: Boolean,
    textAlign: TextAlign,
    onValueChange: (String) -> Unit

) {
    OutlinedTextField(
        modifier = modifier
            .border(
                border = BorderStroke(1.dp, color = Color000000.copy(0.2f)),
                shape = RoundedCornerShape(8.dp)
            ),
        textStyle = MaterialTheme.typography.labelMedium.copy(
            textAlign = TextAlign.Start,
            lineHeight = 17.sp,
            color = Color111245,
        ),
        singleLine = true,

        keyboardOptions = keyboardOptions,
        value = value,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = cursorColor,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        ),
        onValueChange = onValueChange,
        shape = RoundedCornerShape(8.dp),
        trailingIcon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "",
                    tint = Color20227E
                )
            }

        },

        placeholder = {
            Text(
                text = hintText,
                style = MaterialTheme.typography.labelMedium.copy(color = Color78799C)
            )
        }
    )
}