package gtvt.ktpm.thuexe.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.fillMaxHeight
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import gtvt.ktpm.thuexe.R
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.screens.owner.OwnerRoute
import gtvt.ktpm.thuexe.ui.screens.owner.SessionViewModel
import gtvt.ktpm.thuexe.ui.widget.form.nhanLoi
import gtvt.ktpm.thuexe.ui.theme.Color1D2145
import gtvt.ktpm.thuexe.ui.theme.Color242956
import gtvt.ktpm.thuexe.ui.theme.Color9E3332
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorF2564D
import gtvt.ktpm.thuexe.ui.theme.ColorFF9064
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
import gtvt.ktpm.thuexe.ui.widget.InputOutlineText

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    sessionViewModel: SessionViewModel = hiltViewModel(),
) {
    val phien by sessionViewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(phien.loggedIn) {
        if (phien.loggedIn) {
            val dich = if (sessionViewModel.cheDoChuXe) OwnerRoute.HOME
            else gtvt.ktpm.thuexe.ui.screens.renter.RenterRoute.HOME
            navController.navigate(dich) { popUpTo(0) }
        }
    }
    var soDienThoai by rememberSaveable { mutableStateOf("") }
    var matKhau by rememberSaveable { mutableStateOf("") }
    var hienMatKhau by rememberSaveable { mutableStateOf(false) }
    val dangNhap = { sessionViewModel.login(soDienThoai.trim(), matKhau) }
    val nen = androidx.compose.ui.graphics.Color(0xFF14142B)

    Box(modifier = Modifier.fillMaxSize().background(nen)) {
        // Ảnh xe ở nửa trên, mờ dần vào nền tối để chữ và khung đăng nhập đọc rõ.
        Image(
            painterResource(id = R.drawable.login_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).align(Alignment.TopCenter),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.35f),
                    0.4f to nen.copy(alpha = 0.75f),
                    0.65f to nen,
                )
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding(),
        ) {
            // Phần trên để trống cho ảnh xe làm nền; khung đăng nhập bị đẩy xuống đáy.
            Spacer(Modifier.weight(1f))

            // Khung đăng nhập dính sát đáy màn hình, bo tròn hai góc trên.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
            ) {
                Text(
                    "Đăng nhập",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = Chu.TieuDeMan.copy(color = gtvt.ktpm.thuexe.ui.theme.Color111245),
                )
                Spacer(Modifier.height(16.dp))
                TruongDangNhap(
                    value = soDienThoai,
                    onValueChange = { soDienThoai = it },
                    hint = "Số điện thoại",
                    leading = Icons.Default.Call,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = androidx.compose.ui.text.input.ImeAction.Next),
                )
                Spacer(Modifier.height(12.dp))
                TruongDangNhap(
                    value = matKhau,
                    onValueChange = { matKhau = it },
                    hint = "Mật khẩu",
                    leading = Icons.Default.Lock,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = androidx.compose.ui.text.input.ImeAction.Done),
                    onDone = { dangNhap() },
                    visualTransformation = if (hienMatKhau) androidx.compose.ui.text.input.VisualTransformation.None
                    else androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    trailing = {
                        androidx.compose.material3.IconButton(onClick = { hienMatKhau = !hienMatKhau }) {
                            Icon(
                                if (hienMatKhau) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (hienMatKhau) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                tint = Color78799C,
                            )
                        }
                    },
                )
                Spacer(Modifier.height(16.dp))
                gtvt.ktpm.thuexe.ui.widget.form.PrimaryButton(
                    text = if (phien.loading) "Đang đăng nhập…" else "Đăng nhập",
                    onClick = { dangNhap() },
                    enabled = !phien.loading && soDienThoai.isNotBlank() && matKhau.isNotEmpty(),
                )
                phien.errorCode?.let {
                    Text(
                        nhanLoi(it, phien.errorMessage),
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                        textAlign = TextAlign.Center,
                        style = Chu.Phu.copy(color = ColorD54646),
                    )
                }
                TextButton(onClick = {}, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Quên mật khẩu?", style = Chu.Than.copy(color = gtvt.ktpm.thuexe.ui.theme.Primary))
                }
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Bạn chưa có tài khoản? ", style = Chu.Than.copy(color = Color78799C))
                    Text(
                        "Đăng ký ngay",
                        modifier = Modifier.clickable { navController.navigate("register") },
                        style = Chu.Nhan.copy(color = gtvt.ktpm.thuexe.ui.theme.Primary),
                    )
                }
            }
        }
    }
}

@Composable
private fun TruongDangNhap(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    leading: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardOptions: KeyboardOptions,
    modifier: Modifier = Modifier,
    onDone: () -> Unit = {},
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    trailing: (@Composable () -> Unit)? = null,
) {
    androidx.compose.material3.OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        placeholder = { Text(hint, style = MaterialTheme.typography.labelMedium.copy(color = Color78799C)) },
        leadingIcon = { Icon(leading, contentDescription = null, tint = gtvt.ktpm.thuexe.ui.theme.Primary) },
        trailingIcon = trailing,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onDone = { onDone() }),
        textStyle = MaterialTheme.typography.labelMedium.copy(color = gtvt.ktpm.thuexe.ui.theme.Color111245),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = gtvt.ktpm.thuexe.ui.theme.Primary,
            unfocusedBorderColor = ColorCFD0E4,
            cursorColor = gtvt.ktpm.thuexe.ui.theme.Primary,
        ),
    )
}

@Composable
fun RowTextAction(
    modifier: Modifier = Modifier,
    textForgetPass: String,
    textSearchResult: String,
    lineText1: Int,
    lineText2: Int,
    textStyle: TextStyle,
    onClickTextForgetPass: () -> Unit,
    onClickTextSearchResult: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = { onClickTextForgetPass() }) {
            Text(text = textForgetPass, style = textStyle, maxLines = lineText1)
        }
        TextButton(onClick = { onClickTextSearchResult() }) {
            Text(text = textSearchResult, style = textStyle, maxLines = lineText2)
        }
    }
}

@Composable
fun RowTextInfo(
    modifier: Modifier = Modifier,
    textForgetPass: String,
    textSearchResult: String,
    lineText1: Int,
    lineText2: Int,
    textStyle: TextStyle,
    onClickTextForgetPass: () -> Unit,
    onClickTextSearchResult: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = { onClickTextForgetPass() }) {
            Text(
                text = textForgetPass,
                style = textStyle,
                maxLines = lineText1,
                modifier = Modifier.width(120.dp)
            )
        }
        TextButton(onClick = { onClickTextSearchResult() }) {
            Text(text = textSearchResult, style = textStyle, maxLines = lineText2)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RowTextActionPreview(modifier: Modifier = Modifier) {
    UIEnglishEgaleTechTheme {
        RowTextAction(
            textForgetPass = "Hỗ trợ chủ xe: 1900 1234",
            textSearchResult = "bonbon.vn/chu-xe",
            lineText1 = 2,
            lineText2 = 1,
            textStyle = MaterialTheme.typography.titleSmall.copy(color = Color.Black),
            onClickTextForgetPass = {},
            onClickTextSearchResult = {}
        )
    }
}

@Composable
fun ButtonItem(
    modifier: Modifier = Modifier,
    nameButton: String,
    textColor: Color,
    gradient: Brush,
    shadowColor: Color,
    shadowBottomOffset: Float,
    buttonHeight: Float = 50f,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(buttonHeight.dp)
    ) {
        // Button phủ kín Box nên nó nuốt hết chạm; clickable đặt ở Box ngoài không bao giờ nổ.
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = textColor
            ),
            modifier = modifier
                .padding(top = 1.dp, start = 1.dp, end = 1.dp)
                .background(gradient, shape = shape)
                .fillMaxWidth()
                .padding(top = 2.dp, start = 2.dp, end = 2.dp)
                .height(buttonHeight.dp)
        ) {
            Text(
                text = nameButton,
                style = MaterialTheme.typography.displayMedium.copy(color = Color.White)
            )
        }
    }
}

@Composable
fun ButtonShadowOnlyBottom(
    modifier: Modifier = Modifier,
    nameButton: String,
    textColor: Color,
    gradient: Brush,
    shadowColor: Color,
    shadowBottomOffset: Float,
    buttonHeight: Float = 50f,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .height(buttonHeight.dp + shadowBottomOffset.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(buttonHeight.dp + shadowBottomOffset.dp),
            color = shadowColor,
            shape = shape
        ) {}

        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = textColor
            ),
            modifier = modifier
                .padding(top = 1.dp, start = 1.dp, end = 1.dp)
                .background(gradient, shape = shape)
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 2.dp, start = 2.dp, end = 2.dp)
                .height(buttonHeight.dp)
        ) {
            Text(
                text = nameButton,
                style = MaterialTheme.typography.displayMedium.copy(color = Color.White)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ButtonItemPreview(modifier: Modifier = Modifier) {
    val gradient = Brush.horizontalGradient(colors = listOf(gtvt.ktpm.thuexe.ui.theme.Primary, gtvt.ktpm.thuexe.ui.theme.PrimaryHover))
    ButtonItem(
        nameButton = "Login",
        textColor = Color.White,
        gradient = gradient,
        shadowColor = Color9E3332,
        shadowBottomOffset = 8f,
        onClick = { }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewButtonLoginWithShadow2() {
    val gradient = Brush.horizontalGradient(colors = listOf(gtvt.ktpm.thuexe.ui.theme.Primary, gtvt.ktpm.thuexe.ui.theme.PrimaryHover))
    ButtonShadowOnlyBottom(
        nameButton = "Login",
        textColor = Color.White,
        gradient = gradient,
        shadowColor = Color9E3332,
        shadowBottomOffset = 8f,
        onClick = { }
    )
}