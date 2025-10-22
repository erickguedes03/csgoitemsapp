package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

// --- Data classes for Stickers ---

data class Sticker(
    val id: String,
    val name: String,
    val description: String?,
    val rarity: StickerRarity?,
    val image: String
) : Serializable

data class StickerRarity(
    val id: String?,
    val name: String?,
    val color: String?
) : Serializable
