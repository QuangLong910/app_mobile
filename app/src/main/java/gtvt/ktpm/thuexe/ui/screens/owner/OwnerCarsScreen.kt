package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import gtvt.ktpm.thuexe.ui.widget.form.nhanHopSo
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.LocationOn
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.widget.form.CarStatusChip
import gtvt.ktpm.thuexe.ui.widget.form.EmptyBox
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBanner
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBox
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.tien
import gtvt.ktpm.thuexe.ui.widget.form.urlAnh

@Composable
fun OwnerCarsScreen(
    onBack: (() -> Unit)? = null,
    onAddCar: () -> Unit = {},
    onOpenCar: (Long) -> Unit = {},
    onNeedAgreement: () -> Unit = {},
    viewModel: OwnerCarsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.load()
        viewModel.loadAgreement()
    }

    // A4: chưa ký cam kết thì không có đường nào đăng được xe.
    val themXe = { if (state.mustSignAgreement) onNeedAgreement() else onAddCar() }

    OwnerScaffold(
        title = "Xe của tôi",
        onBack = onBack,
        actions = {
            IconButton(onClick = themXe) {
                Icon(Icons.Default.Add, "Đăng xe mới", tint = Color.White)
            }
        },
    ) { padding ->
        when {
            state.loading && state.cars.isEmpty() -> LoadingBox()

            state.cars.isEmpty() && state.errorCode != null ->
                ErrorBox(state.errorCode, message = state.errorMessage) { viewModel.load() }

            state.cars.isEmpty() -> EmptyBox(
                text = if (state.mustSignAgreement) {
                    "Bạn chưa ký cam kết chủ xe.\nKý xong mới đăng được xe lên sàn."
                } else {
                    "Chưa có xe nào.\nĐăng chiếc đầu tiên để bắt đầu nhận đơn."
                },
                actionText = if (state.mustSignAgreement) "Ký cam kết" else "Đăng xe",
                onAction = themXe,
            )

            else -> LazyColumn(
                modifier = padding.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                item { ErrorBanner(state.errorCode, state.errorMessage) }
                items(state.cars, key = { it.id }) { xe ->
                    CarCard(xe) { onOpenCar(xe.id) }
                }
            }
        }
    }
}

@Composable
private fun ThongSoNho(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(icon, null, tint = Color78799C, modifier = Modifier.size(13.dp))
        Text(text, style = Chu.Phu.copy(color = Color78799C))
    }
}

@Composable
internal fun CarCard(car: Car, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCFD0E4),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = car.photos.minByOrNull { it.sortOrder }?.url.urlAnh(),
                contentDescription = null,
                modifier = Modifier
                    .size(108.dp, 84.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorEAECF3),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.padding(start = 12.dp).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(3.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "${car.brand} ${car.model}",
                        modifier = Modifier.weight(1f),
                        style = Chu.TieuDeThe.copy(color = Color111245),
                    )
                    CarStatusChip(car.status)
                }
                Text(car.plate, style = Chu.ThanDam.copy(color = Color78799C))
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ThongSoNho(Icons.Default.Groups, "${car.seats} chỗ")
                    ThongSoNho(Icons.Default.Settings, nhanHopSo(car.transmission))
                    ThongSoNho(Icons.Default.LocationOn, car.district)
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = car.pricePerDay.tien(),
                        style = Chu.Tien.copy(color = ColorD54646),
                    )
                    Text(
                        text = " /ngày",
                        style = Chu.Phu.copy(color = Color78799C),
                    )
                }
                if (car.status == CarStatus.AN && car.rejectReason != null) {
                    Text(
                        text = car.rejectReason,
                        style = Chu.Phu.copy(color = ColorD54646),
                    )
                }
            }
        }
    }
}
