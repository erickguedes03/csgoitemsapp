package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

// Models for crates and contained items

data class Crate(
    val id: String,
    val name: String,
    val description: String?,
    val type: String?,
    val first_sale_date: String?,
    val contains: List<CrateContain>?,
    val contains_rare: List<CrateContain>?,
    val market_hash_name: String?,
    val rental: Boolean?,
    val image: String?
) : Serializable

data class CrateContain(
    val id: String?,
    val name: String?,
    val rarity: CrateRarity?,
    val paint_index: String?,
    val phase: String?,
    val image: String?
) : Serializable

data class CrateRarity(
    val id: String?,
    val name: String?,
    val color: String?
) : Serializable

