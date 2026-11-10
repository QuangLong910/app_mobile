package gtvt.ktpm.thuexe.data.net

import android.content.Context
import androidx.core.content.edit

/** JWT sống qua lần mở app sau. Đổi sang EncryptedSharedPreferences khi nào cần. */
class TokenStore(context: Context) {
    private val prefs = context.getSharedPreferences("thuexe_auth", Context.MODE_PRIVATE)

    var token: String?
        get() = prefs.getString(KEY_TOKEN, null)
        set(value) = prefs.edit { if (value == null) remove(KEY_TOKEN) else putString(KEY_TOKEN, value) }

    var userId: Long
        get() = prefs.getLong(KEY_USER_ID, 0L)
        set(value) = prefs.edit { putLong(KEY_USER_ID, value) }

    /** Đang dùng app ở chế độ chủ xe hay khách thuê; mở lại app vào đúng chế độ cũ. */
    var cheDoChuXe: Boolean
        get() = prefs.getBoolean(KEY_CHE_DO, false)
        set(value) = prefs.edit { putBoolean(KEY_CHE_DO, value) }

    /** Đã qua màn chào một lần: các lần mở app sau không hiện lại, kể cả sau khi đăng xuất. */
    var daXemChao: Boolean
        get() = prefs.getBoolean(KEY_DA_XEM_CHAO, false)
        set(value) = prefs.edit { putBoolean(KEY_DA_XEM_CHAO, value) }

    /** Đăng xuất: xoá phiên nhưng giữ cờ đã xem màn chào. */
    fun clear() = prefs.edit {
        remove(KEY_TOKEN)
        remove(KEY_USER_ID)
        remove(KEY_CHE_DO)
    }

    private companion object {
        const val KEY_TOKEN = "token"
        const val KEY_USER_ID = "user_id"
        const val KEY_CHE_DO = "che_do_chu_xe"
        const val KEY_DA_XEM_CHAO = "da_xem_chao"
    }
}
