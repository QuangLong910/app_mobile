package gtvt.ktpm.thuexe.ui.theme.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.widget.ConstraintLayout

import androidx.navigation.NavController
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color9E3332
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.ColorFFFFFF
import gtvt.ktpm.thuexe.ui.theme.screens.ButtonItem
import gtvt.ktpm.thuexe.ui.widget.InputOutlineText
import gtvt.ktpm.thuexe.ui.widget.TopAppBarItem


@Composable
fun SignUpScreen(modifier: Modifier = Modifier,navController: NavController) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.background),
                contentScale = ContentScale.FillBounds
            )
    ) {
        val (buttonBack, titleSignup, desSdt, takeNote, buttonNext) = createRefs()
        val guideline = createGuidelineFromTop(0.5f)
        var isButtonVisible by remember { mutableStateOf(false) }
        Box(
            modifier = modifier
            .constrainAs(buttonBack) {
                top.linkTo(parent.top)
                bottom.linkTo(titleSignup.top)
                start.linkTo(parent.start)


            }
            .fillMaxWidth()
            .fillMaxHeight(0.15f)) {

            TopAppBarItem(
                titleTopBar = "",
                isBorderBottom = false,
                isShowButtonBack = true,
                isShowButtonNext = false,
                onClick = { navController.navigate("screen1") },
                onClickNextButton = {}

            )
        }


        Text(
            modifier = modifier
                .constrainAs(titleSignup) {
                    top.linkTo(buttonBack.bottom, margin = 200.dp)
                    end.linkTo(parent.end, margin = 20.dp)
                    start.linkTo(parent.start, margin = 20.dp)
                    width = Dimension.fillToConstraints
                },
            textAlign = TextAlign.Center,
            text = "Để đăng ký, hãy điền số điện thoại của bạn.",
            style = MaterialTheme.typography.displayMedium.copy(fontSize = 30.sp),
            color = ColorFFFFFF,
            maxLines = 2
        )
        var value by remember {
            mutableStateOf("")
        }
        InputOutlineText(
            value = value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isCentered = true,
            hintText = " Số điện thoại",
            cursorColor = Color111245,
            textAlign = TextAlign.Center,

            modifier = modifier.constrainAs(desSdt) {
                top.linkTo(titleSignup.bottom, margin = 20.dp)
                start.linkTo(parent.start, margin = 20.dp)
                end.linkTo(parent.end, margin = 20.dp)
                width = Dimension.fillToConstraints

            }, onValueChange = {
                value = it
                isButtonVisible = it.isNotBlank()

            })

        Text(
            modifier = modifier
                .padding(20.dp)
                .constrainAs(takeNote) {
                    top.linkTo(desSdt.bottom, margin = 16.dp)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start, margin = 16.dp)

                },
            textAlign = TextAlign.Center,
            text = "Nhớ rằng - không bao giờ đăng ký bằng số điện thoại của người khác.",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 15.sp),
            color = ColorCFD0E4,
            maxLines = 2
        )
        val gradientButtonLogin = Brush.horizontalGradient(
            colors = listOf(
                gtvt.ktpm.thuexe.ui.theme.Primary,
                gtvt.ktpm.thuexe.ui.theme.PrimaryHover
            )
        )
        if (isButtonVisible) {
            ButtonItem(
                nameButton = "Tiếp tục",
                textColor = gtvt.ktpm.thuexe.ui.theme.Color111245,
                gradient = gradientButtonLogin,
                shadowColor = Color9E3332,
                shadowBottomOffset = 8f,

                modifier = Modifier.constrainAs(buttonNext) {
                    top.linkTo(takeNote.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }, onClick = {}
            )
        }

    }
}
