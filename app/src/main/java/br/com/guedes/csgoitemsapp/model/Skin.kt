package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

// --- Data classes for Skins ---

data class Skin(
    val id: String,
    val name: String,
    val description: String?,
    val weapon: SkinWeapon?,
    val rarity: SkinRarity?,
    val image: String
) : Serializable

data class SkinWeapon(
    val id: String?,
    val name: String?
) : Serializable

data class SkinRarity(
    val id: String?,
    val name: String?,
    val color: String?
) : Serializable
