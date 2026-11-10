//package gtvt.ktpm.thuexe.ui.widget
//
//import android.content.Context
//import com.google.gson.Gson
//import java.io.IOException
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.foundation.text.BasicText
//import androidx.compose.material3.Divider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.RadioButton
//import androidx.compose.material3.RadioButtonDefaults
//import androidx.compose.material3.Surface
//import androidx.compose.material3.Text
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.paint
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.lifecycleScope
//import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
//import gtvt.ktpm.thuexe.ui.theme.ColorF2564D
//import gtvt.ktpm.thuexe.ui.theme.ColorFFFFFF
//import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import com.google.gson.reflect.TypeToken
//import kotlinx.coroutines.delay
//
//data class Province(
//    val name: String,
//
//    )
//
//typealias Provinces = Map<String, Province>
//
//
//fun loadJSONFromAsset(context: Context, fileName: String): String? {
//    return try {
//        val inputStream = context.assets.open(fileName)
//        val size = inputStream.available()
//        val buffer = ByteArray(size)
//        inputStream.read(buffer)
//        inputStream.close()
//        String(buffer, Charsets.UTF_8)
//    } catch (ex: IOException) {
//        ex.printStackTrace()
//        null
//    }
//}
//
//
//fun parseJSON(context: Context): List<Province> {
//    val jsonFileString = loadJSONFromAsset(context, "tinh_tp.json")
//    return jsonFileString?.let {
//        val gson = Gson()
//        val type = object : TypeToken<Map<String, Province>>() {}.type
//        val provincesMap: Map<String, Province> = gson.fromJson(it, type)
//        provincesMap.values.toList()
//    } ?: emptyList()
//}
//
//
//@Composable
//fun ProvinceListScreen(
//    context: Context,
//    modifier: Modifier = Modifier,
//    onProvinceSelected: (Province) -> Unit
//) {
//    var provinceList by remember { mutableStateOf<List<Province>>(emptyList()) }
//    var selectedProvince by remember { mutableStateOf<Province?>(null) }
//    var filteredProvinces by remember { mutableStateOf<List<Province>>(emptyList()) }
//    var searchText by remember { mutableStateOf("") }
//    var selectedLetter by remember { mutableStateOf<Char?>(null) }
//    val listState = rememberLazyListState()
//    var isLoading by remember { mutableStateOf(true) }
//
//    LaunchedEffect(Unit) {
//        delay(1000)
//        withContext(Dispatchers.IO) {
//            val provinces = parseJSON(context).sortedBy { it.name }
//            provinceList = provinces
//            filteredProvinces = provinces
//            isLoading = false
//        }
//    }
//    LaunchedEffect(searchText) {
//        filteredProvinces = if (searchText.isEmpty()) {
//            provinceList
//        } else {
//            provinceList.filter { it.name.contains(searchText, ignoreCase = true) }
//        }
//    }
//    // Scroll to the selected letter
//    LaunchedEffect(selectedLetter) {
//        selectedLetter?.let { letter ->
//            val index =
//                filteredProvinces.indexOfFirst { it.name.startsWith(letter, ignoreCase = true) }
//            if (index != -1) {
//                listState.animateScrollToItem(index)
//            }
//        }
//    }
//    Box(modifier = modifier) {
//        if (isLoading) {
//            CircularProgressAnimated()
//        } else
//            Column(modifier = modifier) {
//                SearchWidget(
//                    text = searchText,
//                    onTextChange = { newText ->
//                        searchText = newText
//                    },
//                    onSearchClicked = { query ->
//                        // Bạn có thể thêm hành động tìm kiếm bổ sung nếu cần
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                )
//                Row {
//                    LazyColumn(
//                        state = listState,
//                        modifier = modifier.weight(9f),
//                        contentPadding = PaddingValues(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        items(filteredProvinces) { province ->
//                            ProvinceItem(modifier, province, selectedProvince) {
//                                selectedProvince = it
//                                onProvinceSelected(it)
//                            }
//                        }
//                    }
//                    Column(
//                        modifier = modifier
//                            .fillMaxHeight()
//                            .weight(1f),
//                    ) {
//                        val alphabet = ('A'..'Z').toList()
//                        alphabet.forEach { letter ->
//                            Text(
//                                text = letter.toString(),
//                                fontSize = 16.sp,
//                                color = Color.Gray,
//                                modifier = modifier.clickable {
//                                    selectedLetter = letter
//                                }
//                            )
//                        }
//                    }
//                }
//
//
//            }
//    }
//
//}
//
//
//@Composable
//fun ProvinceItem(
//    modifier: Modifier = Modifier,
//    province: Province,
//    selectedProvince: Province?,
//    onSelected: (Province) -> Unit
//) {
//    Row(
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier.fillMaxWidth()
//    ) {
//        RadioButton(
//            selected = selectedProvince == province,
//            onClick = { onSelected(province) },
//            colors = RadioButtonDefaults.colors(
//                selectedColor = ColorF2564D,
//                unselectedColor = Color.Gray,
//
//                ),
//            modifier = Modifier.padding(start = 2.dp) // Khoảng cách giữa RadioButton và nội dung
//        )
//        Column(
//            modifier = Modifier
//                .weight(1f) // Sử dụng weight để Column chiếm hết phần còn lại của Row
//                .padding(
//                    start = 10.dp,
//                    top = 8.dp,
//                    bottom = 8.dp
//                ) // Khoảng cách lề của Column và nội dung
//        ) {
//            BasicText(
//                text = province.name,
//                style = MaterialTheme.typography.labelMedium.copy(color = ColorFFFFFF)
//            )
//            Divider(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(top = 4.dp), // Khoảng cách giữa Divider và nội dung phía trên
//                color = Color.Gray
//            )
//        }
//    }
//}
