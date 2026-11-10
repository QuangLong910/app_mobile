package gtvt.ktpm.thuexe

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import gtvt.ktpm.thuexe.data.net.PhienDangNhap
import gtvt.ktpm.thuexe.data.net.TokenStore
import javax.inject.Inject
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

import gtvt.ktpm.thuexe.ui.screens.onboarding.ListOnBoardScreen
import gtvt.ktpm.thuexe.ui.screens.owner.ownerGraph
import gtvt.ktpm.thuexe.ui.screens.renter.renterGraph
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
import gtvt.ktpm.thuexe.ui.theme.screens.LoginScreen
import gtvt.ktpm.thuexe.ui.theme.signup.SignUpScreen

@AndroidEntryPoint
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class MainActivity : ComponentActivity() {
    @Inject lateinit var tokenStore: TokenStore
    @Inject lateinit var push: gtvt.ktpm.thuexe.data.push.PushRegistrar

    private var daHoiQuyen = false

    /** Mã đơn của thông báo vừa bấm vào — đọc ở onCreate/onNewIntent, MyApp mở đúng đơn rồi bỏ qua. */
    private var maDonMoTuThongBao by androidx.compose.runtime.mutableStateOf<String?>(null)
    // Chỉ để chụp màn hình so sánh giao diện lúc phát triển — mở thẳng một xe qua adb, không phải luồng thật.
    private var maXeMoNhanh by androidx.compose.runtime.mutableStateOf<Long?>(null)

    private val xinQuyenThongBao =
        registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        anThanhDieuHuongHeThong()
        maDonMoTuThongBao = intent?.getStringExtra(EXTRA_MA_DON)
        maXeMoNhanh = intent?.getLongExtra(EXTRA_MA_XE, -1)?.takeIf { it > 0 }
        setContent {
            UIEnglishEgaleTechTheme {

                MyApp(
                    tokenStore, push,
                    maDonMoTuThongBao = maDonMoTuThongBao,
                    onDaMoDon = { maDonMoTuThongBao = null },
                    maXeMoNhanh = maXeMoNhanh,
                    xinQuyenThongBao = {
                        if (!daHoiQuyen && android.os.Build.VERSION.SDK_INT >= 33 &&
                            checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) !=
                            android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            daHoiQuyen = true
                            xinQuyenThongBao.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                        }
                    },
                )

            }
        }
    }

    // Ẩn thanh điều hướng của hệ thống (3 nút/vạch cử chỉ); vuốt từ mép dưới lên thì hiện tạm rồi tự ẩn lại.
    private fun anThanhDieuHuongHeThong() {
        val dieuKhien = androidx.core.view.WindowCompat.getInsetsController(window, window.decorView)
        dieuKhien.systemBarsBehavior =
            androidx.core.view.WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        dieuKhien.hide(androidx.core.view.WindowInsetsCompat.Type.navigationBars())
    }

    // Đóng bàn phím/hộp thoại có thể làm thanh hiện lại; lấy lại tiêu điểm thì ẩn tiếp.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) anThanhDieuHuongHeThong()
    }

    // App đang mở sẵn thì hệ thống gọi lại đây thay vì tạo activity mới (FLAG_ACTIVITY_SINGLE_TOP).
    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra(EXTRA_MA_DON)?.let { maDonMoTuThongBao = it }
        intent.getLongExtra(EXTRA_MA_XE, -1).takeIf { it > 0 }?.let { maXeMoNhanh = it }
    }

    companion object {
        const val EXTRA_MA_DON = "maDon"
        const val EXTRA_MA_XE = "maXe"
    }
}
@Composable
fun MyApp(
    tokenStore: TokenStore,
    push: gtvt.ktpm.thuexe.data.push.PushRegistrar,
    maDonMoTuThongBao: String? = null,
    onDaMoDon: () -> Unit = {},
    maXeMoNhanh: Long? = null,
    xinQuyenThongBao: () -> Unit,
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    // Token hết hạn ở bất kỳ lời gọi nào: xoá token, báo một câu, về màn đăng nhập.
    LaunchedEffect(Unit) {
        PhienDangNhap.hetHan.collect {
            tokenStore.clear()
            Toast.makeText(context, "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại", Toast.LENGTH_LONG).show()
            navController.navigate("screen2") { popUpTo(0) }
        }
    }
    // Đã có phiên (mở lại app hoặc vừa đăng nhập xong): xin quyền thông báo và đăng ký máy với
    // backend. Mỗi lần đổi màn kiểm tra lại, việc thừa thì PushRegistrar tự bỏ qua.
    LaunchedEffect(Unit) {
        navController.currentBackStackEntryFlow.collect {
            if (!tokenStore.token.isNullOrBlank()) {
                xinQuyenThongBao()
                runCatching { push.dangKy() }
            }
        }
    }
    // Bấm vào thông báo (kể cả lúc app đang mở sẵn): mở thẳng đơn liên quan thay vì chỉ về màn chính.
    // Xoá lại ngay sau khi điều hướng để xoay màn hay mở lại từ gần đây không lặp lại việc này.
    LaunchedEffect(maDonMoTuThongBao) {
        val ma = maDonMoTuThongBao ?: return@LaunchedEffect
        if (!tokenStore.token.isNullOrBlank()) {
            val dich = if (tokenStore.cheDoChuXe) gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute.chiTietDon(ma)
            else gtvt.ktpm.thuexe.ui.screens.renter.RenterRoute.don(ma)
            navController.navigate(dich)
        }
        onDaMoDon()
    }
    LaunchedEffect(maXeMoNhanh) {
        val id = maXeMoNhanh ?: return@LaunchedEffect
        if (!tokenStore.token.isNullOrBlank()) {
            val dich = if (tokenStore.cheDoChuXe) gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute.theoDoiXe(id)
            else gtvt.ktpm.thuexe.ui.screens.renter.RenterRoute.xe(id)
            navController.navigate(dich)
        }
    }
    // Chọn màn mở đầu đúng một lần: đã đăng nhập thì vào thẳng chế độ đang dùng; chưa đăng nhập
    // mà đã xem màn chào thì vào đăng nhập; lần đầu tiên mới xem màn chào.
    val manMoDau = remember {
        when {
            !tokenStore.token.isNullOrBlank() ->
                if (tokenStore.cheDoChuXe) gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute.HOME
                else gtvt.ktpm.thuexe.ui.screens.renter.RenterRoute.HOME
            tokenStore.daXemChao -> "screen2"
            else -> "screen1"
        }
    }
    NavHost(navController, startDestination = manMoDau) {
        composable("screen1") { ListOnBoardScreen(navController) }
        composable("screen2") {
            LaunchedEffect(Unit) { tokenStore.daXemChao = true }
            LoginScreen(modifier = Modifier,navController)
        }
        composable("screen3") { SignUpScreen(modifier = Modifier,navController) }
        composable("register") { gtvt.ktpm.thuexe.ui.screens.renter.RegisterScreen(navController) }
        ownerGraph(navController)
        renterGraph(navController)
    }
}


