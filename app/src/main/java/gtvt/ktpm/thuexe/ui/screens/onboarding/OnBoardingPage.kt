package gtvt.ktpm.thuexe.ui.screens.onboarding

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import gtvt.ktpm.thuexe.ui.theme.ColorF2564D
import kotlinx.coroutines.launch


data class OnBoardingPage(
    val imageBroad: String,
    val nameBoard: String,
    val contentBoard: String
)

val onBoardingPages = listOf(
    OnBoardingPage(
        imageBroad = "onboard_dang_xe",
        nameBoard = "Xe nhàn ở nhà\n"
                + "thì cho thuê",
        contentBoard = "Đăng xe lên sàn, tự đặt giá theo ngày và tự chọn ngày nào cho thuê, ngày nào giữ lại dùng."
    ),
    OnBoardingPage(
        imageBroad = "onboard_nhan_don",
        nameBoard = "Nhận đơn\n"
                + "trong 30 phút",
        contentBoard = "Khách đặt là bạn nhận được đơn ngay. Bấm nhận hoặc từ chối; lịch xe tự khoá, không lo trùng ngày."
    ),
    OnBoardingPage(
        imageBroad = "onboard_bien_ban",
        nameBoard = "Biên bản\n"
                + "hai chiều",
        contentBoard = "Sáu ảnh, số ODO và chữ ký của cả hai bên trước khi giao xe. Lúc trả xe không còn gì để cãi."
    ),
    OnBoardingPage(
        imageBroad = "onboard_tien_ve",
        nameBoard = "Tiền về\n"
                + "tài khoản bạn",
        contentBoard = "Sàn giữ tiền thuê và tiền cọc của khách cho tới khi trả xe xong, chốt đơn là chuyển khoản cho bạn."
    )
)


@SuppressLint("UnsafeOptInUsageError", "UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable

fun ListOnBoardScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = {
        onBoardingPages.size
    })

    val textState = remember {
        derivedStateOf {
            when (pagerState.currentPage) {
                0 -> listOf("BỎ QUA", "TIẾP")
                1 -> listOf("BỎ QUA", "TIẾP")
                2 -> listOf("BỎ QUA", "TIẾP")
                3 -> listOf("", "BẮT ĐẦU")
                else -> listOf("", "")
            }
        }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (textState.value[0].isNotEmpty()) {
                        TextUi(
                            text = textState.value[0],
                            backgroundColor = Color.Transparent,
                            textColor = ColorF2564D
                        ) {
                            scope.launch {
                                if (pagerState.currentPage > 0) {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            }
                        }
                    }
                }
//                Box(
//                    modifier = Modifier.weight(1f),
//                    contentAlignment = Alignment.Center
//                ) {
//                    IndicatorUi(
//                        pageSize = onBoardingPages.size,
//                        currentPage = pagerState.currentPage
//                    )
//                }
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TextUi(
                        text = textState.value[1],
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        textColor = ColorF2564D
                    ) {
                        scope.launch {
                            if (pagerState.currentPage < onBoardingPages.size - 1) {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            } else {
                                navController.navigate("screen2")
                            }
                        }
                    }
                }
            }
        }
    ) {
        HorizontalPager(state = pagerState) { page ->
            val currentPage = onBoardingPages[page]
            OnBoardingView(
                imageBroad = currentPage.imageBroad,
                nameBoard = currentPage.nameBoard,
                contentBoard = currentPage.contentBoard
            )
        }
    }


}



