package br.com.guedes.csgoitemsapp.network

import br.com.guedes.csgoitemsapp.model.Crate
import br.com.guedes.csgoitemsapp.model.Highlight
import br.com.guedes.csgoitemsapp.model.Skin
import br.com.guedes.csgoitemsapp.model.Sticker
import retrofit2.http.GET

interface CsgoApi {
    @GET("en/skins.json")
    suspend fun getSkins(): List<Skin>

    @GET("en/stickers.json")
    suspend fun getStickers(): List<Sticker>

    @GET("en/highlights.json")
    suspend fun getHighlights(): List<Highlight>

    @GET("en/crates.json")
    suspend fun getCrates(): List<Crate>
}
