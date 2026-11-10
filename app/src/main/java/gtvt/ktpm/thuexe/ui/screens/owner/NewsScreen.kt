package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import gtvt.ktpm.thuexe.data.model.NewsItem
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color464BD5
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.widget.form.TaiLaiKhiQuayLai
import gtvt.ktpm.thuexe.ui.widget.form.EmptyBox
import gtvt.ktpm.thuexe.ui.widget.form.ErrorBox
import gtvt.ktpm.thuexe.ui.widget.form.LoadingBox
import gtvt.ktpm.thuexe.ui.widget.form.OwnerScaffold
import gtvt.ktpm.thuexe.ui.widget.form.StatusChip
import gtvt.ktpm.thuexe.ui.widget.form.ngay
import gtvt.ktpm.thuexe.ui.widget.form.urlAnh

@Composable
fun NewsScreen(viewModel: NewsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    TaiLaiKhiQuayLai(Unit) { viewModel.load() }

    OwnerScaffold(title = "Tin từ sàn") { padding ->
        when {
            state.loading && state.items.isEmpty() -> LoadingBox()

            state.items.isEmpty() && state.errorCode != null ->
                ErrorBox(state.errorCode, message = state.errorMessage) { viewModel.load() }

            state.items.isEmpty() -> EmptyBox("Chưa có tin nào.")

            else -> LazyColumn(
                modifier = padding.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                items(state.items, key = { it.id }) { NewsCard(it) }
            }
        }
    }
}

@Composable
private fun NewsCard(tin: NewsItem) {
    val uriHandler = LocalUriHandler.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .let { m -> if (tin.link != null) m.clickable { uriHandler.openUri(tin.link) } else m },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, ColorCFD0E4),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            AsyncImage(
                model = tin.imageUrl.urlAnh(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusChip(
                        text = tin.tag,
                        color = when (tin.tag) {
                            "CHÍNH SÁCH" -> ColorD54646
                            "NHẮC VIỆC" -> Color111245
                            else -> Color464BD5
                        },
                    )
                    tin.publishedAt?.let {
                        Text(
                            text = it.ngay(),
                            modifier = Modifier.padding(start = 8.dp),
                            style = Chu.Phu.copy(color = Color78799C),
                        )
                    }
                }
                Text(
                    text = tin.title,
                    modifier = Modifier.padding(top = 6.dp),
                    style = Chu.TieuDeThe.copy(color = Color111245),
                )
                Text(
                    text = tin.summary,
                    modifier = Modifier.padding(top = 4.dp),
                    style = Chu.Than.copy(color = Color78799C),
                )
                // RSS chỉ mượn tiêu đề và ảnh để giới thiệu; ghi rõ nguồn, đọc trọn bài ở trang gốc.
                tin.source?.let {
                    Text(
                        text = if (tin.link != null) "Nguồn: $it — bấm để đọc trọn bài" else "Nguồn: $it",
                        modifier = Modifier.padding(top = 6.dp),
                        style = Chu.Phu.copy(color = Color464BD5),
                    )
                }
            }
        }
    }
}
