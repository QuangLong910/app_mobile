package gtvt.ktpm.thuexe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ThueXeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        gtvt.ktpm.thuexe.data.push.taoKenhThongBao(this)
    }
}
