package gtvt.ktpm.thuexe.ui.widget

import androidx.compose.animation.*
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import gtvt.ktpm.thuexe.ui.theme.Color111245
import gtvt.ktpm.thuexe.ui.theme.ColorCFD0E4
// TODO: đổi thành tên theme thật của project bạn, ví dụ:
// import gtvt.ktpm.thuexe.ui.theme.ThueXeTheme

// Thay thế cho androidx.compose.material.ContentAlpha (Material 2) — M3 không có sẵn hằng số này
private object OtpContentAlpha {
    const val disabled = 0.38f
    const val medium = 0.6f
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedOtpTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    length: Int = 6,
    onFilled: () -> Unit = {},
    errorMessage: String? = null,
    helperText: String? = null,
    helperTextColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge.copy(
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    ),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    visualTransformation: VisualTransformation = PasswordVisualTransformation(),
    requestFocus: Boolean,
    clearFocusWhenFilled: Boolean
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val updatedOnValueChange by rememberUpdatedState(onValueChange)
    val updatedOnFilled by rememberUpdatedState(onFilled)

    val code by remember(value) {
        mutableStateOf(TextFieldValue(value, TextRange(value.length)))
    }

    DisposableEffect(requestFocus) {
        if (requestFocus) {
            focusRequester.requestFocus()
        }
        onDispose { }
    }

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = Color.Transparent,
                backgroundColor = Color.Transparent,
            )

            CompositionLocalProvider(
                LocalTextToolbar provides EmptyTextToolbar,
                LocalTextSelectionColors provides customTextSelectionColors
            ) {
                BasicTextField(
                    modifier = Modifier
                        .focusRequester(focusRequester = focusRequester)
                        .fillMaxWidth(),
                    value = code,
                    onValueChange = {
                        if (!it.text.isDigitsOnly() || it.text.length > length)
                            return@BasicTextField

                        updatedOnValueChange(it.text)

                        if (it.text.length == length) {
                            keyboardController?.hide()
                            if (clearFocusWhenFilled) {
                                focusRequester.freeFocus()
                                focusManager.clearFocus()
                            }
                            updatedOnFilled()
                        }
                    },
                    visualTransformation = visualTransformation,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.NumberPassword
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                        }
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    enabled = enabled,
                    readOnly = readOnly,
                    decorationBox = {
                        OtpInputDecoration(
                            code = code.text,
                            length = length,
                            textStyle = textStyle,
                            enabled = enabled,
                            isError = !errorMessage.isNullOrBlank(),
                            visualTransformation = visualTransformation
                        )
                    }
                )
            }
        }

        if (!errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
                modifier = Modifier.padding(top = 4.dp)
            )
        } else if (!helperText.isNullOrBlank()) {
            Text(
                text = helperText,
                style = MaterialTheme.typography.bodySmall.copy(color = helperTextColor),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun OtpInputDecoration(
    modifier: Modifier = Modifier,
    code: String,
    length: Int,
    textStyle: TextStyle,
    enabled: Boolean,
    isError: Boolean,
    visualTransformation: VisualTransformation
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            for (i in 0 until length) {
                val text = if (i < code.length) code[i].toString() else ""
                OtpEntry(
                    modifier = Modifier.weight(1f, fill = false),
                    text = text,
                    textStyle = textStyle,
                    enabled = enabled,
                    isError = isError,
                    visualTransformation = visualTransformation
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun OtpEntry(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle,
    enabled: Boolean,
    isError: Boolean,
    visualTransformation: VisualTransformation
) {
    val transformedText = remember(text, visualTransformation) {
        visualTransformation.filter(AnnotatedString(text))
    }.text.text

    val borderColor by animateColorAsState(
        targetValue = when {
            isError -> MaterialTheme.colorScheme.error
            !enabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = OtpContentAlpha.disabled)
            transformedText.isNotEmpty() -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = OtpContentAlpha.medium)
        },
        label = "borderColor"
    )

    Box(
        modifier = modifier
            .width(42.dp)
            .height(48.dp)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        val textColor by animateColorAsState(
            targetValue = when {
                isError -> MaterialTheme.colorScheme.error
                !enabled -> textStyle.color.copy(alpha = OtpContentAlpha.disabled)
                else -> textStyle.color
            },
            label = "textColor"
        )

        AnimatedContent(
            modifier = Modifier.fillMaxWidth(),
            targetState = transformedText,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                    initialContentExit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
                    sizeTransform = null
                )
            },
            contentAlignment = Alignment.Center,
            label = "textVisibility"
        ) { text ->
            if (text.isNotBlank()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = text,
                    color = textColor.takeIf { false } ?: Color111245,
                    style = textStyle
                )
            }
        }
    }
}

private object EmptyTextToolbar : TextToolbar {
    override val status: TextToolbarStatus = TextToolbarStatus.Hidden

    override fun hide() {}

    override fun showMenu(
        rect: Rect,
        onCopyRequested: (() -> Unit)?,
        onPasteRequested: (() -> Unit)?,
        onCutRequested: (() -> Unit)?,
        onSelectAllRequested: (() -> Unit)?,
    ) {
    }
}

@Preview
@Composable
private fun OutlinedOtpTextFieldPreview() {
    // TODO: đổi MaterialTheme thành theme thật của project, ví dụ ThueXeTheme { ... }
    MaterialTheme {
        Surface {
            Column {
                var text by remember { mutableStateOf("") }

                OutlinedOtpTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    requestFocus = true,
                    clearFocusWhenFilled = true
                )
            }
        }
    }
}