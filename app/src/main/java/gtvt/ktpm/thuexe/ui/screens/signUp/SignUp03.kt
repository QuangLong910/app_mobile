package gtvt.ktpm.thuexe.ui.screens.signUp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorF2564D
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
import gtvt.ktpm.thuexe.ui.widget.OutlinedOtpTextField
import gtvt.ktpm.thuexe.ui.widget.TopAppBarItem

import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp03(
    modifier: Modifier = Modifier,
    numberPhone: String
) {

    Scaffold(
        topBar = {
            TopAppBarItem(
                titleTopBar = "Xác thực OTP",
                isBorderBottom = true,
                isShowButtonNext = true,
                onClickNextButton = {},
                onClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(top = 60.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
            Text(
                text = "Mã 6 số đã được gửi đến $numberPhone. Hãy điền chúng vào đây",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily(Font(R.font.nunito_extrabold))
                ),
                modifier = modifier.padding(horizontal = 24.dp)
            )
            var valueOTP by rememberSaveable {
                mutableStateOf("")
            }
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedOtpTextField(
                value = valueOTP,
                onValueChange = { it ->
                    valueOTP = it
                },
                requestFocus = true,
                clearFocusWhenFilled = true
            )
            Spacer(modifier = Modifier.height(24.dp))
            var secondsLeft by remember { mutableStateOf(59) }
            var showResendButton by remember { mutableStateOf(false) }

            LaunchedEffect(key1 = secondsLeft) {
                if (secondsLeft > 0) {
                    delay(1000L)
                    secondsLeft--
                } else {
                    showResendButton = true
                }
            }
            if (!showResendButton) {
                Text(
                    modifier = modifier.padding(horizontal = 18.dp),
                    text = "Nếu không nhận được bạn có thể yêu cầu gửi lại trong vòng: 00:${
                        secondsLeft.toString().padStart(2, '0')
                    } giây",
                    style = MaterialTheme
                        .typography.titleMedium.copy(
                            lineHeight = 17.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_medium))
                        ),
                    color = Color78799C
                )
            } else {
                Text(
                    modifier = modifier.padding(horizontal = 18.dp),
                    text = "Bạn có thể yêu cầu gửi lại mã OTP",
                    style = MaterialTheme
                        .typography.titleMedium.copy(
                            lineHeight = 17.sp,
                            fontFamily = FontFamily(Font(R.font.nunito_medium))
                        ),
                    color = Color78799C
                )
                Spacer(modifier = Modifier.height(8.dp))
                RowRefreshItem {
                    // Gửi lại OTP
                }
            }
        }
    }
}

@Composable
fun RowRefreshItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier
            .wrapContentSize()
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Gửi lại", style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily(Font(R.font.nunito_bold)),
                lineHeight = 15.sp,
            ),
            color = ColorF2564D
        )
        Spacer(modifier = Modifier.width(4.dp))
        Image(
            painter = painterResource(id = R.drawable.refresh),
            contentDescription = "Image Refresh",
            modifier = modifier.size(12.dp)
        )
    }

}

@Preview(name = "RowRefreshItemPreview", showBackground = true, showSystemUi = true)
@Composable
fun RowRefreshItemPreview(modifier: Modifier = Modifier) {
    UIEnglishEgaleTechTheme {
        RowRefreshItem {

        }
    }
}

@Preview(name = "TopAppBarItemPreview", showBackground = true, showSystemUi = true)
@Composable
fun TopAppBarItemPreview(modifier: Modifier = Modifier) {
    UIEnglishEgaleTechTheme {
        TopAppBarItem(
            titleTopBar = "Hello",
            isBorderBottom = true,
            isShowButtonNext = true,
            onClickNextButton = {

            },
            onClick = {})
    }
}


@Preview(name = "SignUp03Preview", showBackground = true, showSystemUi = true)
@Composable
fun SignUp03Preview(modifier: Modifier = Modifier) {
    UIEnglishEgaleTechTheme {
        SignUp03(numberPhone = "0983655632")
    }

}