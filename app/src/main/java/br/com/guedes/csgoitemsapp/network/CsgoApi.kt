package br.com.guedes.csgoitemsapp.network

import br.com.guedes.csgoitemsapp.model.Item
import retrofit2.http.GET

interface CsgoApi {
    @GET("en/skins.json")
    suspend fun getSkins(): List<Item>

    @GET("en/stickers.json")
    suspend fun getStickers(): List<Item>
}
