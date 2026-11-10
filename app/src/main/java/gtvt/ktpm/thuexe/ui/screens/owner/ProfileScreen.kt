package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.data.model.DocStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.heroGradient
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.FormSection
import gtvt.ktpm.thuexe.ui.widget.form.InfoRow
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.PayoutStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.SecondaryButton
import gtvt.ktpm.thuexe.ui.widget.form.StatusChip
import gtvt.ktpm.thuexe.ui.widget.form.gioNgay
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.nhanTrangThaiGiayTo
import gtvt.ktpm.thuexe.ui.widget.form.tien

private val XANH = gtvt.ktpm.thuexe.ui.theme.Success

@Composable
fun ProfileScreen(
    onKyCamKet: () -> Unit = {},
    onDangXuat: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) { viewModel.load() }

    OwnerScaffold(title = "Hồ sơ") { padding ->
        val me = state.me
        if (me == null) {
            if (state.loading) LoadingBox() else ErrorBanner(state.errorCode, state.errorMessage)
            return@OwnerScaffold
        }

        Column(modifier = padding.fillMaxWidth().verticalScroll(scroll)) {
            ErrorBanner(state.errorCode, state.errorMessage)

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .background(heroGradient, RoundedCornerShape(50)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = me.fullName.trim().split(" ").lastOrNull()?.take(1)?.uppercase() ?: "?",
                        style = Chu.SoLon.copy(color = Color.White),
                    )
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(
                        text = me.fullName,
                        style = Chu.TieuDeThe.copy(color = Color111245),
                    )
                    Text(
                        text = me.phone,
                        style = Chu.Than.copy(color = Color78799C),
                    )
                    me.email?.let {
                        Text(it, style = Chu.Phu.copy(color = Color78799C))
                    }
                }
            }

            FormSection("Giấy tờ") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Trạng thái", style = Chu.Than.copy(color = Color78799C))
                    StatusChip(
                        text = nhanTrangThaiGiayTo(me.documentStatus),
                        color = when (me.documentStatus) {
                            DocStatus.DAT -> XANH
                            DocStatus.CHO_DUYET -> ColorFF9064
                            else -> ColorD54646
                        },
                    )
                }
                // GET /me chỉ trả về một chuỗi trạng thái, không kèm số CCCD hay hạn GPLX.
                Text(
                    text = when (me.documentStatus) {
                        DocStatus.DAT -> "CCCD và GPLX đã được sàn duyệt."
                        DocStatus.CHO_DUYET -> "Sàn đang xem giấy tờ của bạn."
                        DocStatus.TU_CHOI -> "Giấy tờ bị từ chối, nộp lại giúp sàn."
                        else -> "Chưa nộp CCCD và GPLX. Chưa nộp thì không nhận được đơn."
                    },
                    modifier = Modifier.padding(top = 6.dp),
                    style = Chu.Phu.copy(color = Color78799C),
                )
            }

            FormSection("Cam kết chủ xe") {
                val ck = state.agreement
                when {
                    ck == null -> Text(
                        text = "Chưa đọc được bản điều khoản.",
                        style = Chu.Than.copy(color = Color78799C),
                    )
                    ck.signed -> {
                        InfoRow("Bản điều khoản", "v${ck.version}")
                        InfoRow("Đã ký lúc", ck.signedAt?.gioNgay() ?: "—")
                    }
                    else -> {
                        Text(
                            text = "Chưa ký. Chưa ký thì không đăng được xe lên sàn.",
                            style = Chu.Than.copy(color = ColorD54646),
                        )
                        SecondaryButton(
                            text = "Ký ngay",
                            onClick = onKyCamKet,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }
            }

            SecondaryButton(
                text = "Đăng xuất",
                onClick = { viewModel.logout(); onDangXuat() },
                modifier = Modifier.padding(top = 16.dp),
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}
