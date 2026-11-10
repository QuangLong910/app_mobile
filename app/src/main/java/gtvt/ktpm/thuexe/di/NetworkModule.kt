package gtvt.ktpm.thuexe.di

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import gtvt.ktpm.thuexe.BuildConfig
import gtvt.ktpm.thuexe.data.api.AuthApi
import gtvt.ktpm.thuexe.data.api.BookingApi
import gtvt.ktpm.thuexe.data.api.FileApi
import gtvt.ktpm.thuexe.data.api.HandoverApi
import gtvt.ktpm.thuexe.data.api.NewsApi
import gtvt.ktpm.thuexe.data.api.OwnerApi
import gtvt.ktpm.thuexe.data.net.BocConverterFactory
import gtvt.ktpm.thuexe.data.net.AuthInterceptor
import gtvt.ktpm.thuexe.data.net.LocalDateAdapter
import gtvt.ktpm.thuexe.data.net.OffsetDateTimeAdapter
import gtvt.ktpm.thuexe.data.net.TokenStore
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter)
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter)
        .create()

    @Provides
    @Singleton
    fun tokenStore(@ApplicationContext context: Context): TokenStore = TokenStore(context)

    @Provides
    @Singleton
    fun okHttpClient(tokenStore: TokenStore): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(tokenStore))
        .apply {
            if (BuildConfig.DEBUG) {
                addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            }
        }
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        // Ảnh biên bản có thể nặng, cho rộng tay hơn.
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun retrofit(client: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(client)
        // Đặt trước Gson: gỡ lớp bọc { status, data, code, message } rồi mới giao xuống.
        .addConverterFactory(BocConverterFactory(gson))
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    @Singleton
    fun authApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun fileApi(retrofit: Retrofit): FileApi = retrofit.create(FileApi::class.java)

    @Provides
    @Singleton
    fun ownerApi(retrofit: Retrofit): OwnerApi = retrofit.create(OwnerApi::class.java)

    @Provides
    @Singleton
    fun bookingApi(retrofit: Retrofit): BookingApi = retrofit.create(BookingApi::class.java)

    @Provides
    @Singleton
    fun handoverApi(retrofit: Retrofit): HandoverApi = retrofit.create(HandoverApi::class.java)

    @Provides
    @Singleton
    fun newsApi(retrofit: Retrofit): NewsApi = retrofit.create(NewsApi::class.java)
}
