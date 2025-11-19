package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

/**
 * A unified data class for displaying any item in the UI.
 * This allows the adapter and detail view to handle different item types (skins, stickers, highlights, crates)
 * in a consistent way.
 */
data class Item(
    val id: String,
    val name: String?,
    val description: String?,
    val image: String,
    val subtext: String?,
    val rarityColor: String?,
    val rarityName: String? = null, // Added for filtering by name
    val videoUrl: String? = null, // optional video for highlights
    val extraSummary: String? = null // optional summary (e.g., crate contains brief)
) : Serializable
