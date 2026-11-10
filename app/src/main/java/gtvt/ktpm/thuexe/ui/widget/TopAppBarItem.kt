package gtvt.ktpm.thuexe.ui.widget

import gtvt.ktpm.thuexe.ui.theme.Primary
import gtvt.ktpm.thuexe.ui.theme.PrimaryPressed
import gtvt.ktpm.thuexe.ui.theme.PrimaryHover

import android.R.attr.top
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester.Companion.createRefs
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.Color282BA0
import gtvt.ktpm.thuexe.ui.theme.Color4553B7
import gtvt.ktpm.thuexe.ui.theme.Color9E3332
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.ColorFFFFFF
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme

@Composable
fun TopAppBarItem(
    modifier: Modifier = Modifier,
    titleTopBar: String? = null,
    isBorderBottom: Boolean = false,
    isShowButtonBack: Boolean = false,
    isShowButtonNext: Boolean = false,
    onClickNextButton: () -> Unit,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .background(color = Color4553B7)
            .padding(top = 20.dp)
    ) {
        val (buttonBack, title, borderBottom, buttonNext) = createRefs()
        if(isShowButtonBack){
            IconButton(
                onClick = { onClick },
                modifier = modifier.constrainAs(buttonBack) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.button_back),
                    modifier = modifier
                        .padding(4.dp)
                        .fillMaxSize(),
                    contentDescription = "Button Back"
                )
            }
        }

        titleTopBar?.let {
            Text(
                text = titleTopBar,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    lineHeight = 19.sp
                ),
                color = ColorFFFFFF,
                modifier = modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
            )
        }
        if (isShowButtonNext){
            Box(modifier = modifier
                .height(30.dp)
                .width(48.dp)
                .constrainAs(buttonNext){
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, margin = 4.dp)
                    bottom.linkTo(parent.bottom)
                }
            )
            {
                val gradientButtonNext = Brush.horizontalGradient(
                    colors = listOf(
                        Primary,
                        PrimaryHover
                    )
                )
                ButtonNextItem(
                    modifier = modifier.fillMaxSize(),
                    buttonHeight = 28f,
                    nameButton = "Tiếp",
                    textColor = Color111245,
                    gradient = gradientButtonNext,
                    shadowColor = PrimaryPressed,
                    paddingButton = 1.dp,
                    shadowBottomOffset = 2f
                ) {

                }
            }

        }
        if (isBorderBottom)
            Spacer(modifier = modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(color = Color282BA0)
                .constrainAs(borderBottom) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )

    }

}

//@Preview(name = "ButtonNextItemPreview", showBackground = true, showSystemUi = true)
//@Composable
//fun ButtonNextItemPreview(modifier: Modifier = Modifier) {
//    UIEnglishEgaleTechTheme {
//        Box(modifier = modifier
//            .padding(30.dp)
//            .height(34.dp)
//            .width(45.dp)){
//            val gradientButtonNext = Brush.horizontalGradient(
//                colors = listOf(
//                    ColorD54646,
//                    ColorFF9064
//                )
//            )
//            ButtonNextItem(
//                nameButton = "Tiếp",
//                textColor = ColorFFFFFF,
//                gradient = gradientButtonNext,
//                shadowColor = PrimaryPressed,
//                paddingButton = 1.dp,
//                shadowBottomOffset = 2f
//            ) {
//
//            }
//        }
//
//    }
//
//}