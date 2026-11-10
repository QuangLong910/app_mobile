package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3

/**
 * Khung chung cho mọi màn của chủ xe.
 *
 * Thanh trên tự dựng chứ không dùng TopAppBar: bản mặc định cao 64dp, cộng thêm vùng thanh
 * trạng thái thành một khối chiếm gần một phần mười màn hình chỉ để chứa một dòng chữ.
 *
 * Thanh trên PHẢI tự chừa vùng thanh trạng thái: Scaffold chỉ chừa vùng đó cho phần nội dung
 * khi KHÔNG có topBar. Có topBar tự dựng mà không chừa thì nó nằm dưới thanh trạng thái và
 * nuốt mất dòng đầu của nội dung. Dùng windowInsetsPadding chứ không tự cộng số dp, nên nếu
 * màn cha đã tiêu thụ phần inset đó thì không bị chừa hai lần.
 */
private val CAO_THANH_TREN = 44.dp

@Composable
fun OwnerScaffold(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = gtvt.ktpm.thuexe.ui.theme.Neutral2,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.statusBars),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(CAO_THANH_TREN),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack, modifier = Modifier.size(CAO_THANH_TREN)) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Quay lại",
                                tint = Color111245,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                        Spacer(Modifier.width(2.dp))
                    } else {
                        Spacer(Modifier.width(16.dp))
                    }
                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        style = Chu.TieuDeMan.copy(color = Color111245),
                    )
                    actions()
                    Spacer(Modifier.width(4.dp))
                }
                HorizontalDivider(color = ColorCFD0E4)
            }
        },
        bottomBar = {
            Box(Modifier.windowInsetsPadding(WindowInsets.navigationBars).padding(horizontal = 16.dp)) { bottomBar() }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            content(Modifier.padding(horizontal = 16.dp))
        }
    }
}

/**
 * Khung cho màn "một thứ có ảnh làm trung tâm" (xem HeroPhotoPanel): không có thanh trên
 * trắng — HeroPhotoPanel tự vẽ nút quay lại đè lên ảnh. Nội dung tràn hết chiều ngang tới
 * sát mép, phần padding hai bên do từng khối bên trong tự lo (Hero tràn viền, còn lại thì
 * không), khác với OwnerScaffold luôn chừa sẵn 16dp.
 */
@Composable
fun HeroScaffold(
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {},
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White,
        bottomBar = {
            Box(
                Modifier
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 16.dp),
            ) { bottomBar() }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(bottom = padding.calculateBottomPadding()).fillMaxSize()) {
            content(Modifier.fillMaxWidth())
        }
    }
}
