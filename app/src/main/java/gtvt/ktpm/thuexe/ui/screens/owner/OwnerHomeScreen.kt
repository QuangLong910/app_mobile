package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SpaceDashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorD54646

private data class The(val nhan: String, val icon: ImageVector)

private val THE = listOf(
    The("Tổng quan", Icons.Default.SpaceDashboard),
    The("Đơn", Icons.AutoMirrored.Filled.ListAlt),
    The("Xe", Icons.Default.DirectionsCar),
    The("Tin tức", Icons.Default.Newspaper),
    The("Hồ sơ", Icons.Default.Person),
)

@Composable
fun OwnerHomeScreen(
    onMoChiTiet: (String) -> Unit = {},
    onAddCar: () -> Unit = {},
    onTheoDoiXe: (Long) -> Unit = {},
    onNeedAgreement: () -> Unit = {},
    onMoLichSu: () -> Unit = {},
    onDangXuat: () -> Unit = {},
) {
    var the by rememberSaveable { mutableIntStateOf(0) }
    val badge: gtvt.ktpm.thuexe.ui.screens.renter.TabBadgeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val soDonCho by badge.donChoToiDuyet.collectAsStateWithLifecycle()
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai(Unit) { badge.lamMoi(laChuXe = true) }
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay { badge.lamMoi(laChuXe = true) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                THE.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = the == i,
                        onClick = { the = i },
                        icon = {
                            androidx.compose.material3.BadgedBox(badge = {
                                if (i == 1 && soDonCho > 0) androidx.compose.material3.Badge { Text(soDonCho.toString()) }
                            }) { Icon(t.icon, null) }
                        },
                        label = { Text(t.nhan, style = Chu.NhanThe) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = gtvt.ktpm.thuexe.ui.theme.Primary,
                            selectedTextColor = Color111245,
                            unselectedIconColor = Color78799C,
                            unselectedTextColor = Color78799C,
                        ),
                    )
                }
            }
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (the) {
                0 -> DashboardScreen(
                    onXemDon = { the = 1 },
                    onXemXe = { the = 2 },
                    onMoLichSu = onMoLichSu,
                    onLapBienBan = onMoChiTiet,
                )
                1 -> OwnerBookingsScreen(onMoChiTiet = onMoChiTiet)
                2 -> OwnerCarsScreen(
                    onAddCar = onAddCar,
                    onOpenCar = onTheoDoiXe,
                    onNeedAgreement = onNeedAgreement,
                )
                3 -> NewsScreen()
                else -> ProfileScreen(onKyCamKet = onNeedAgreement, onDangXuat = onDangXuat)
            }
        }
    }
}
