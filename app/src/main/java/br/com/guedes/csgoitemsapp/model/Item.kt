package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

data class Item(
    val id: String? = null,
    val name: String? = null,
    val market_hash_name: String? = null,
    val image: String? = null,
    val weapon: String? = null,
    val description: String? = null
) : Serializable
