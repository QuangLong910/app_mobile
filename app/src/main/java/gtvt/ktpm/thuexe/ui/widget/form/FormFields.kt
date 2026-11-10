package gtvt.ktpm.thuexe.ui.widget.form

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.ui.theme.Chu
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.Color78799C
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
import gtvt.ktpm.thuexe.ui.theme.ColorD54646
import gtvt.ktpm.thuexe.ui.theme.ColorF2564D
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Nhãn + ô nhập + dòng lỗi. Mọi ô trong app chủ xe đi qua đây để cùng một dáng. */
@Composable
fun FormField(
    label: String,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    hint: String? = null,
    field: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row {
            Text(
                text = label,
                style = Chu.Than.copy(color = Color111245),
            )
            if (required) {
                Text(
                    text = " *",
                    style = Chu.Than.copy(color = ColorD54646),
                )
            }
        }
        Box(modifier = Modifier.padding(top = 4.dp)) { field() }
        val duoi = error ?: hint
        if (duoi != null) {
            Text(
                text = duoi,
                modifier = Modifier.padding(top = 2.dp),
                style = Chu.Phu.copy(
                    color = if (error != null) ColorD54646 else Color78799C,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    hintText: String,
    keyboardType: KeyboardType,
    singleLine: Boolean,
    minLines: Int,
    enabled: Boolean,
    isError: Boolean,
    trailing: (@Composable () -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        isError = isError,
        singleLine = singleLine,
        minLines = minLines,
        textStyle = Chu.Than.copy(color = Color111245),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(8.dp),
        trailingIcon = trailing,
        placeholder = {
            Text(hintText, style = Chu.Than.copy(color = Color78799C))
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            cursorColor = ColorF2564D,
            focusedBorderColor = Color111245,
            unfocusedBorderColor = ColorCFD0E4,
            errorBorderColor = ColorD54646,
        ),
    )
}

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "",
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    FormField(label, modifier, required, error, hint) {
        BaseTextField(value, onValueChange, hintText, keyboardType, singleLine, minLines, enabled, error != null)
    }
}

/**
 * Tiền giữ nguyên Long, chỉ hiện ra có dấu chấm. Gõ chữ gì không phải số thì bỏ qua —
 * không có đường nào để một số thập phân lọt vào.
 */
@Composable
fun FormMoneyField(
    label: String,
    value: Long,
    onValueChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
) {
    FormField(label, modifier, required, error, hint) {
        BaseTextField(
            value = if (value == 0L) "" else value.tienKhongDonVi(),
            onValueChange = { raw ->
                val digits = raw.filter { it.isDigit() }.take(12)
                onValueChange(digits.toLongOrNull() ?: 0L)
            },
            hintText = "0",
            keyboardType = KeyboardType.Number,
            singleLine = true,
            minLines = 1,
            enabled = enabled,
            isError = error != null,
            trailing = {
                Text("đ", style = Chu.Than.copy(color = Color78799C))
            },
        )
    }
}

@Composable
fun FormIntField(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    suffix: String? = null,
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
) {
    FormField(label, modifier, required, error, hint) {
        BaseTextField(
            value = if (value == 0) "" else value.toString(),
            onValueChange = { raw ->
                onValueChange(raw.filter { it.isDigit() }.take(9).toIntOrNull() ?: 0)
            },
            hintText = "0",
            keyboardType = KeyboardType.Number,
            singleLine = true,
            minLines = 1,
            enabled = enabled,
            isError = error != null,
            trailing = suffix?.let {
                { Text(it, style = Chu.Than.copy(color = Color78799C)) }
            },
        )
    }
}

/** options: cặp (giá trị gửi lên backend, chữ hiện cho người dùng). */
@Composable
fun FormDropdownField(
    label: String,
    value: String?,
    options: List<Pair<String, String>>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    hintText: String = "Chọn",
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
) {
    var mo by remember { mutableStateOf(false) }
    FormField(label, modifier, required, error, hint) {
        Box {
            BaseTextField(
                value = options.firstOrNull { it.first == value }?.second.orEmpty(),
                onValueChange = {},
                hintText = hintText,
                keyboardType = KeyboardType.Text,
                singleLine = true,
                minLines = 1,
                enabled = false,
                isError = error != null,
                trailing = { Icon(Icons.Default.ArrowDropDown, null, tint = Color111245) },
            )
            // OutlinedTextField tắt rồi thì không nhận chạm, nên phủ một lớp bắt chạm lên trên.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = enabled) { mo = true }
            )
            DropdownMenu(expanded = mo, onDismissRequest = { mo = false }) {
                options.forEach { (giaTri, chu) ->
                    DropdownMenuItem(
                        text = { Text(chu, style = Chu.Than) },
                        onClick = {
                            onSelected(giaTri)
                            mo = false
                        },
                    )
                }
            }
        }
    }
}

/**
 * Ngày thuê là LocalDate suốt tuyến. DatePicker trả millis theo UTC nên đổi bằng ZoneOffset.UTC,
 * đổi bằng múi giờ máy là lệch đúng một hôm.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDateField(
    label: String,
    value: LocalDate?,
    onValueChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    required: Boolean = false,
    enabled: Boolean = true,
) {
    var mo by remember { mutableStateOf(false) }
    FormField(label, modifier, required, error, hint) {
        Box {
            BaseTextField(
                value = value?.ngay().orEmpty(),
                onValueChange = {},
                hintText = "dd/mm/yyyy",
                keyboardType = KeyboardType.Text,
                singleLine = true,
                minLines = 1,
                enabled = false,
                isError = error != null,
                trailing = { Icon(Icons.Default.CalendarMonth, null, tint = Color111245) },
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = enabled) { mo = true }
            )
        }
    }
    if (mo) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = value?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mo = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onValueChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    mo = false
                }) { Text("Chọn") }
            },
            dismissButton = { TextButton(onClick = { mo = false }) { Text("Bỏ qua") } },
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth().padding(top = 12.dp)) {
        Text(
            text = title.uppercase(),
            style = Chu.Nhan.copy(color = Color78799C),
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, ColorCFD0E4),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) { content() }
        }
    }
}

/** Một dòng "nhãn — giá trị" cho các màn chỉ đọc. */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color111245,
) {
    Row(
        modifier = modifier.fillMaxWidth().heightIn(min = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, style = Chu.Than.copy(color = Color78799C))
        Text(value, style = Chu.Than.copy(color = valueColor))
    }
}
