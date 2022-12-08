package pl.szkolakotlina.workshops

import me.toptas.rssconverter.RssConverterFactory
import me.toptas.rssconverter.RssFeed
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.GET

object NetworkModule {
    fun createApi(): ArticlesApi = Retrofit.Builder()
        .addConverterFactory(RssConverterFactory.create())
        .client(OkHttpClient())
        .baseUrl("https://kotlintesting.com/")
        .build()
        .create(ArticlesApi::class.java)
}

//https://picsum.photos/500
interface ArticlesApi {
    @GET("/rss")
    suspend fun getRssFeed(): RssFeed
}