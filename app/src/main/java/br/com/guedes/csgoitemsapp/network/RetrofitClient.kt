package br.com.guedes.csgoitemsapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://raw.githubusercontent.com/ByMykel/CSGO-API/main/public/api/"

    val api: CsgoApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CsgoApi::class.java)
    }
}
