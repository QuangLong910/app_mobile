package gtvt.ktpm.thuexe.ui.widget.form

import gtvt.ktpm.thuexe.BuildConfig

/**
 * Backend trả url ảnh dạng tương đối ("/files/abc.jpg"). Coil cần url đầy đủ,
 * nên mọi chỗ hiện ảnh phải đi qua đây — không thì ô ảnh trống trơn mà chẳng báo lỗi gì.
 */
fun String?.urlAnh(): String? = when {
    this.isNullOrBlank() -> null
    // file:// là ảnh vừa chọn trên máy, chưa tải lên.
    startsWith("http://") || startsWith("https://") || startsWith("file://") -> this
    else -> BuildConfig.BASE_URL.trimEnd('/') + "/" + trimStart('/')
}
