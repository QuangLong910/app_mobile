package gtvt.ktpm.thuexe.ui.widget

import gtvt.ktpm.thuexe.ui.theme.Primary
import gtvt.ktpm.thuexe.ui.theme.PrimaryPressed
import gtvt.ktpm.thuexe.ui.theme.PrimaryHover


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color1D2145
import gtvt.ktpm.thuexe.ui.theme.Color353A75
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.Color9E3332
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.ColorFFFFFF
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme

/*

This example demonstrates how to make custom dialog in android jetpack compose in android.

*  Button        : https://www.boltuix.com/2021/12/button_25.html
*  Clip Modifier : https://www.boltuix.com/2021/12/clip-modifier_24.html
*  Alert Dialog  : https://www.boltuix.com/2021/12/alert-dialog_25.html
*  Column        : https://www.boltuix.com/2021/12/column-layout_25.html
*  Box           : https://www.boltuix.com/2021/12/box-layout_25.html
*  Type.kt       : https://www.boltuix.com/2021/12/typography_27.html
*  Color.kt      : https://www.boltuix.com/2022/05/google-material-design-color.html
* */

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DialogCustomItemPreview(modifier: Modifier = Modifier) {
    UIEnglishEgaleTechTheme {
        Column {
//            DialogCustomItem(
//                titleDialog = "Có lỗi",
//                textButtonFirst = "Thử lại",
//                icon = painterResource(id = R.drawable.exit),
//                contentDialog = "Thông tin đăng nhập của bạn không đúng. Vui lòng kiểm tra số điện thoại / mật khẩu và thử lại.",
//                textStyleTitle = MaterialTheme.typography.displayMedium.copy(color = Color111245),
//                shape = RoundedCornerShape(20.dp),
//                marginTop = 24.dp,
//                onDismiss = {},
//                shadowBottomOffSet = 8.dp,
//                onClickButtonFirst = { },
//                onClickButtonSecond = {},
//                onClickButtonCancel = {})
//            Spacer(modifier = Modifier.height(24.dp))
            DialogCustomItem(
                titleDialog = "Ảnh đại diện",
                textButtonFirst = "Chụp ảnh Camera",
                textButtonSecond = "Upload từ máy",
                textStyleTitle = MaterialTheme.typography.displaySmall.copy(color = Color111245),
                shape = RoundedCornerShape(10.dp),
                marginTop = 8.dp,
                textButtonCancel = "Hủy",
                onDismiss = {},
                shadowBottomOffSet = 8.dp,
                onClickButtonFirst = {},
                onClickButtonSecond = {},
                onClickButtonCancel = {})
        }

    }

}


@Composable
fun DialogCustomItem(
    modifier: Modifier = Modifier,
    titleDialog: String,
    contentDialog: String? = null,
    textButtonFirst: String,
    textButtonSecond: String? = null,
    textButtonCancel: String? = null,
    icon: Painter? = null,
    textStyleTitle: TextStyle,
    marginTop: Dp,
    shape: Shape,
    shadowBottomOffSet: Dp,
    onDismiss: () -> Unit,
    onClickButtonFirst: () -> Unit,
    onClickButtonSecond: () -> Unit?,
    onClickButtonCancel: () -> Unit?
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Dialog(onDismissRequest = onDismiss) {
        ConstraintLayout(modifier = modifier.fillMaxWidth()) {
            val (boxContent, buttonCancel) = createRefs()
            Box(modifier = modifier.wrapContentSize().constrainAs(boxContent){
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(buttonCancel.top)
            }) {
                Box(
                    modifier = modifier
                        .wrapContentSize()
//                        .height((screenHeight)/2)
//                        .padding(5.dp)
                ) {
                    Column(verticalArrangement = Arrangement.Bottom,
                        modifier = modifier.wrapContentSize()
                    ) {
                        Spacer(modifier = modifier.height(screenHeight / 6))
                        Box(modifier = modifier
//                            .padding(horizontal = 10.dp)
                            .wrapContentSize()
//                            .height(((screenHeight * 3) / 5) + shadowBottomOffSet)
//                            .width(((screenHeight * 3) / 5) + shadowBottomOffSet)
//                                .height(((screenHeight * 3) / 5))
                            .background(
                                color = Color78799C,
                                shape = shape //RoundedCornerShape(25.dp, 25.dp, 25.dp, 25.dp)
                            )){
                            Box(
                                modifier = modifier
                                    .padding(top = 2.dp, start = 2.dp, end = 2.dp, bottom = 5.dp)
//                                    .padding(horizontal = 10.dp)
                                    .wrapContentSize()
//                                    .fillMaxHeight()
//                                    .fillMaxWidth()
//                                .height(((screenHeight * 3) / 5))
                                    .background(
                                        color = ColorFFFFFF,
                                        shape = shape //RoundedCornerShape(25.dp, 25.dp, 25.dp, 25.dp)
                                    )
                            ) {

//                            Box(modifier = modifier){
//
//                            }
                                icon?.let { it ->
                                    Box(
                                        modifier = modifier
                                            .size(50.dp)
                                            .align(alignment = Alignment.TopEnd)
                                            .offset(x = 25.dp, y = (-25).dp)
                                    ) {
                                        Image(
                                            modifier = modifier.fillMaxSize(),
                                            painter = it,
                                            contentDescription = "Icon close"
                                        )
                                    }
                                }

                                Column(
                                    modifier = Modifier.padding(10.dp).wrapContentSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(marginTop))
                                    Text(
                                        text = titleDialog,
                                        textAlign = TextAlign.Center,
                                        modifier = modifier
                                            .padding(top = 10.dp)
                                            .fillMaxWidth(),
                                        style = textStyleTitle
                                        //MaterialTheme.typography.displayMedium ,   color = Color111245,
                                    )

                                    contentDialog?.let { it ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            // "Thông tin đăng nhập của bạn \n  không đúng. Vui lòng kiểm tra số \n điện thoại / mật khẩu và thử lại."
                                            text = it,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(top = 10.dp, start = 0.dp, end = 0.dp)
                                                .fillMaxWidth(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = Color111245,
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    val gradientButton = Brush.horizontalGradient(
                                        colors = listOf(
                                            Primary,
                                            PrimaryHover
                                        )
                                    )
                                    ButtonItem(
                                        nameButton = textButtonFirst,
                                        textColor = Color111245,
                                        gradient = gradientButton,
                                        shadowColor = PrimaryPressed,
                                        shadowBottomOffset = 8f,
                                        paddingButton = 4.dp,
                                        onClick = { onClickButtonFirst }
                                    )
                                    textButtonSecond?.let { it ->
                                        Spacer(modifier = Modifier.height(8.dp))
                                        ButtonItem(
                                            nameButton = it,
                                            textColor = Color111245,
                                            gradient = gradientButton,
                                            shadowColor = PrimaryPressed,
                                            paddingButton = 4.dp,
                                            shadowBottomOffset = 8f,
                                            onClick = { onClickButtonSecond }
                                        )
                                    }
                                }
                            }
                        }

                    }
                }

            }
            textButtonCancel?.let { it ->
                val gradientButtonCancel = Brush.horizontalGradient(
                    colors = listOf(
                        Color353A75,
                        Color353A75
                    )
                )
                ButtonItem(
                    modifier.constrainAs(buttonCancel){
                        start.linkTo(boxContent.start)
                        end.linkTo(boxContent.end)
                        top.linkTo(boxContent.bottom, margin = 12.dp)
                    },
                    nameButton = it,
                    textColor = Color.White,
                    gradient = gradientButtonCancel,
                    shadowColor = Color1D2145,
                    shadowBottomOffset = 8f,
                    paddingButton = 0.dp,
                    onClick = { onClickButtonCancel }
                )
            }
        }


    }


}

@Composable
fun InfoDialog(
    title: String? = "Message",
    desc: String? = "Your Message",
    onDismiss: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    Dialog(
        onDismissRequest = onDismiss
    ) {

        Box(
            modifier = Modifier
                .height(screenHeight / 2)
                .padding(5.dp)
        ) {

            Column(
                modifier = Modifier
            ) {
                Spacer(modifier = Modifier.height(screenHeight / 6))
                Box(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 10.dp, 0.dp)
                        .height((screenHeight * 3) / 5)
                        .background(
                            color = ColorFFFFFF,
                            shape = RoundedCornerShape(25.dp, 25.dp, 25.dp, 25.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .align(alignment = Alignment.TopEnd)
                            .offset(x = 25.dp, y = -25.dp)

                    ) {
                        HeaderImage(modifier = Modifier.fillMaxSize())
                    }
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {


                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Có lỗi",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.displayMedium,
                            color = Color111245,
                        )
                        Spacer(modifier = Modifier.height(8.dp))


                        Text(
                            text = "Thông tin đăng nhập của bạn \n  không đúng. Vui lòng kiểm tra số \n điện thoại / mật khẩu và thử lại.",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 10.dp, start = 0.dp, end = 0.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color111245,
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        val gradientButtonLogin = Brush.horizontalGradient(
                            colors = listOf(
                                Primary,
                                PrimaryHover
                            )
                        )
                        ButtonItem(
                            nameButton = "Thử lại",
                            textColor = Color111245,
                            gradient = gradientButtonLogin,
                            shadowColor = PrimaryPressed,
                            shadowBottomOffset = 8f,
                            paddingButton = 12.dp,
                            onClick = {}
                        )



                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }

            }

        }
    }
}

@Composable
fun HeaderImage(modifier: Modifier) {
    Image(
        painter = painterResource(id = R.drawable.exit),
        contentDescription = "Back",
        modifier = modifier
    )
}

@Preview
@Composable
fun ViewDialog() {
    UIEnglishEgaleTechTheme {
        InfoDialog {

        }
    }
}
