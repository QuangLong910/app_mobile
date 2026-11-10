package gtvt.ktpm.thuexe.ui.screens.renter

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import gtvt.ktpm.thuexe.data.model.HandoverKind
import gtvt.ktpm.thuexe.ui.screens.owner.HandoverGiaoScreen
import gtvt.ktpm.thuexe.ui.screens.owner.HandoverSoiScreen
import gtvt.ktpm.thuexe.ui.screens.owner.NewsScreen
import gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.Primary

object RenterRoute {
    const val HOME = "renter/home"
    const val XE = "renter/cars/{carId}"
    const val DAT_XE = "renter/cars/{carId}/book"
    const val DON = "renter/bookings/{maDon}"
    const val GIAY_TO = "renter/kyc"
    const val LAP_BIEN_BAN_TRA = "renter/bookings/{maDon}/handover/tra"
    const val SOI_BIEN_BAN_GIAO = "renter/bookings/{maDon}/handover/giao"

    fun xe(carId: Long) = "renter/cars/$carId"
    fun datXe(carId: Long) = "renter/cars/$carId/book"
    fun don(maDon: String) = "renter/bookings/$maDon"
    fun lapBienBanTra(maDon: String) = "renter/bookings/$maDon/handover/tra"
    fun soiBienBanGiao(maDon: String) = "renter/bookings/$maDon/handover/giao"
}

/** Gắn toàn bộ màn của khách thuê vào NavHost sẵn có: renterGraph(navController). */
fun NavGraphBuilder.renterGraph(navController: NavHostController) {
    composable(RenterRoute.HOME) {
        RenterHomeScreen(
            onMoXe = { navController.navigate(RenterRoute.xe(it)) },
            onMoDon = { navController.navigate(RenterRoute.don(it)) },
            onNopGiayTo = { navController.navigate(RenterRoute.GIAY_TO) },
            onDangXuat = { navController.navigate("screen2") { popUpTo(0) } },
        )
    }

    composable(
        route = RenterRoute.XE,
        arguments = listOf(navArgument("carId") { type = NavType.LongType }),
    ) { entry ->
        CarDetailScreen(
            carId = entry.arguments?.getLong("carId") ?: 0L,
            onBack = { navController.popBackStack() },
            onDatXe = { navController.navigate(RenterRoute.datXe(it)) },
        )
    }

    composable(
        route = RenterRoute.DAT_XE,
        arguments = listOf(navArgument("carId") { type = NavType.LongType }),
    ) { entry ->
        BookCarScreen(
            carId = entry.arguments?.getLong("carId") ?: 0L,
            onBack = { navController.popBackStack() },
            onDaDat = { ma ->
                navController.navigate(RenterRoute.don(ma)) {
                    popUpTo(RenterRoute.HOME)
                }
            },
            onNopGiayTo = { navController.navigate(RenterRoute.GIAY_TO) },
        )
    }

    composable(RenterRoute.DON) { entry ->
        val ma = entry.arguments?.getString("maDon").orEmpty()
        RenterBookingDetailScreen(
            maDon = ma,
            onBack = { navController.popBackStack() },
            onSoiBienBanGiao = { navController.navigate(RenterRoute.soiBienBanGiao(ma)) },
            onLapBienBanTra = { navController.navigate(RenterRoute.lapBienBanTra(ma)) },
            onNopGiayTo = { navController.navigate(RenterRoute.GIAY_TO) },
        )
    }

    composable(RenterRoute.GIAY_TO) {
        SubmitDocumentsScreen(
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() },
        )
    }

    // Khách soi biên bản GIAO do chủ xe lập, rồi ký thì đơn sang DANG_THUE.
    composable(RenterRoute.SOI_BIEN_BAN_GIAO) { entry ->
        HandoverSoiScreen(
            maDon = entry.arguments?.getString("maDon").orEmpty(),
            kind = HandoverKind.GIAO,
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() },
        )
    }

    // Khách lập biên bản TRA khi kết thúc chuyến.
    composable(RenterRoute.LAP_BIEN_BAN_TRA) { entry ->
        HandoverGiaoScreen(
            maDon = entry.arguments?.getString("maDon").orEmpty(),
            kind = HandoverKind.TRA,
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() },
        )
    }
}

private data class The(val nhan: String, val icon: ImageVector)

private val THE = listOf(
    The("Tìm xe", Icons.Default.Search),
    The("Chuyến của tôi", Icons.AutoMirrored.Filled.ListAlt),
    The("Tin tức", Icons.Default.Newspaper),
    The("Cá nhân", Icons.Default.Person),
)

@Composable
fun RenterHomeScreen(
    onMoXe: (Long) -> Unit = {},
    onMoDon: (String) -> Unit = {},
    onNopGiayTo: () -> Unit = {},
    onDangXuat: () -> Unit = {},
) {
    var the by rememberSaveable { mutableIntStateOf(0) }
    val badge: TabBadgeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
    val soCanLam by badge.chuyenCanLam.collectAsStateWithLifecycle()
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai(Unit) { badge.lamMoi(laChuXe = false) }
    gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiCoTinDay { badge.lamMoi(laChuXe = false) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                THE.forEachIndexed { i, t ->
                    NavigationBarItem(
                        selected = the == i,
                        onClick = { the = i },
                        icon = {
                            androidx.compose.material3.BadgedBox(badge = {
                                if (i == 1 && soCanLam > 0) androidx.compose.material3.Badge { Text(soCanLam.toString()) }
                            }) { Icon(t.icon, null) }
                        },
                        label = { Text(t.nhan, style = Chu.NhanThe) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
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
                0 -> SearchCarsScreen(onMoXe = onMoXe)
                1 -> MyBookingsScreen(onMoDon = onMoDon)
                2 -> NewsScreen()
                else -> RenterProfileScreen(
                    onNopGiayTo = onNopGiayTo,
                    onDangXuat = onDangXuat,
                )
            }
        }
    }
}
