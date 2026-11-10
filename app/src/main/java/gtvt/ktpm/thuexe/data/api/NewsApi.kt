package gtvt.ktpm.thuexe.data.api

import gtvt.ktpm.thuexe.data.model.NewsItem
import retrofit2.http.GET

/** NGOÀI §04 — xem ghi chú ở [gtvt.ktpm.thuexe.data.model.NewsItem]. */
interface NewsApi {
    @GET("news")
    suspend fun news(): List<NewsItem>
}
