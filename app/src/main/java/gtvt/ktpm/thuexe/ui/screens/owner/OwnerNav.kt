package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

object OwnerRoute {
    const val HOME = "owner/home"
    const val DON = "owner/bookings"
    const val XE = "owner/cars"
    const val CAM_KET = "owner/agreement"
    const val LICH_SU_GIAO_DICH = "owner/transactions"
    const val DANG_XE = "owner/cars/new"
    const val SUA_XE = "owner/cars/{carId}"
    const val THEO_DOI_XE = "owner/cars/{carId}/lich"
    const val BIEN_BAN_GIAO = "owner/bookings/{maDon}/handover"
    const val CHI_TIET_DON = "owner/bookings/{maDon}"
    const val SOI_BIEN_BAN_TRA = "owner/bookings/{maDon}/handover/tra"

    fun suaXe(carId: Long) = "owner/cars/$carId"
    fun theoDoiXe(carId: Long) = "owner/cars/$carId/lich"
    fun bienBanGiao(maDon: String) = "owner/bookings/$maDon/handover"
    fun chiTietDon(maDon: String) = "owner/bookings/$maDon"
    fun soiBienBanTra(maDon: String) = "owner/bookings/$maDon/handover/tra"
}

/** Gắn toàn bộ màn của chủ xe vào NavHost sẵn có: ownerGraph(navController). */
fun NavGraphBuilder.ownerGraph(navController: NavHostController) {
    composable(OwnerRoute.HOME) {
        OwnerHomeScreen(
            onMoChiTiet = { navController.navigate(OwnerRoute.chiTietDon(it)) },
            onAddCar = { navController.navigate(OwnerRoute.DANG_XE) },
            onTheoDoiXe = { navController.navigate(OwnerRoute.theoDoiXe(it)) },
            onNeedAgreement = { navController.navigate(OwnerRoute.CAM_KET) },
            onMoLichSu = { navController.navigate(OwnerRoute.LICH_SU_GIAO_DICH) },
            onDangXuat = { navController.navigate("screen2") { popUpTo(0) } },
        )
    }

    composable(OwnerRoute.LICH_SU_GIAO_DICH) {
        LichSuGiaoDichScreen(
            onBack = { navController.popBackStack() },
            onMoDon = { navController.navigate(OwnerRoute.chiTietDon(it)) },
        )
    }

    composable(OwnerRoute.DON) {
        OwnerBookingsScreen(
            onMoChiTiet = { navController.navigate(OwnerRoute.chiTietDon(it)) },
        )
    }

    composable(OwnerRoute.XE) {
        OwnerCarsScreen(
            onAddCar = { navController.navigate(OwnerRoute.DANG_XE) },
            onOpenCar = { navController.navigate(OwnerRoute.theoDoiXe(it)) },
            onNeedAgreement = { navController.navigate(OwnerRoute.CAM_KET) },
        )
    }

    composable(
        route = OwnerRoute.THEO_DOI_XE,
        arguments = listOf(navArgument("carId") { type = NavType.LongType }),
    ) { entry ->
        CarCalendarScreen(
            carId = entry.arguments?.getLong("carId") ?: 0L,
            onBack = { navController.popBackStack() },
            onSuaXe = { navController.navigate(OwnerRoute.suaXe(it)) },
        )
    }


    composable(
        route = OwnerRoute.CHI_TIET_DON,
    ) { entry ->
        BookingDetailScreen(
            maDon = entry.arguments?.getString("maDon").orEmpty(),
            onBack = { navController.popBackStack() },
            onLapBienBanGiao = { navController.navigate(OwnerRoute.bienBanGiao(it)) },
            onSoiBienBanTra = { navController.navigate(OwnerRoute.soiBienBanTra(it)) },
        )
    }

    composable(
        route = OwnerRoute.SOI_BIEN_BAN_TRA,
    ) { entry ->
        HandoverSoiScreen(
            maDon = entry.arguments?.getString("maDon").orEmpty(),
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() },
        )
    }

    composable(OwnerRoute.CAM_KET) {
        AgreementScreen(
            onBack = { navController.popBackStack() },
            onSigned = { navController.popBackStack() },
        )
    }

    composable(OwnerRoute.DANG_XE) {
        CarFormScreen(
            onBack = { navController.popBackStack() },
            onSaved = { navController.popBackStack() },
        )
    }

    composable(
        route = OwnerRoute.SUA_XE,
        arguments = listOf(navArgument("carId") { type = NavType.LongType }),
    ) { entry ->
        CarFormScreen(
            carId = entry.arguments?.getLong("carId"),
            onBack = { navController.popBackStack() },
            onSaved = { navController.popBackStack() },
        )
    }

    composable(
        route = OwnerRoute.BIEN_BAN_GIAO,
    ) { entry ->
        HandoverGiaoScreen(
            maDon = entry.arguments?.getString("maDon").orEmpty(),
            onBack = { navController.popBackStack() },
            onDone = { navController.popBackStack() },
        )
    }
}
