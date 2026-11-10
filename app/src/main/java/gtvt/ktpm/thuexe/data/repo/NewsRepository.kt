package gtvt.ktpm.thuexe.data.repo

import com.google.gson.Gson
import gtvt.ktpm.thuexe.data.api.NewsApi
import gtvt.ktpm.thuexe.data.model.NewsItem
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.apiCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepository @Inject constructor(
    private val api: NewsApi,
    private val gson: Gson,
) {
    suspend fun news(): ApiResult<List<NewsItem>> = apiCall(gson) { api.news() }
}
