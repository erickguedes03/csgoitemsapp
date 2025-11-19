package br.com.guedes.csgoitemsapp.model

import java.io.Serializable

data class Rarity(
    val id: String?,
    val name: String?,
    val color: String?
) : Serializable

data class CollectionInfo(
    val id: String?,
    val name: String?,
    val image: String?
) : Serializable

data class TeamInfo(
    val id: String?,
    val name: String?
) : Serializable

// Agent model matching the API sample
data class Agent(
    val id: String,
    val name: String?,
    val description: String?,
    val rarity: Rarity? = null,
    val collections: List<CollectionInfo>? = null,
    val team: TeamInfo? = null,
    val market_hash_name: String? = null,
    val image: String? = null
) : Serializable
