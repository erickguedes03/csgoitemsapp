package br.com.guedes.csgoitemsapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityMainBinding
import br.com.guedes.csgoitemsapp.model.Crate
import br.com.guedes.csgoitemsapp.model.Item
import br.com.guedes.csgoitemsapp.network.RetrofitClient
import br.com.guedes.csgoitemsapp.ui.detail.CrateDetailActivity
import br.com.guedes.csgoitemsapp.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemAdapter
    private var allItems: List<Item> = emptyList()
    private var cratesById: Map<String, Crate> = emptyMap()
    private var currentCategory = R.id.nav_skins // Default to skins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Adapter and LayoutManager setup
        adapter = ItemAdapter(emptyList()) { item ->
            handleItemClick(item)
        }
        binding.recycler.layoutManager = GridLayoutManager(this, 2) // 2 columns grid
        binding.recycler.adapter = adapter

        // Search functionality
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        // Bottom navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { menuItem ->
            currentCategory = menuItem.itemId
            loadItemsForCategory()
            true
        }

        // Load initial data
        loadItemsForCategory()
    }

    private fun handleItemClick(item: Item) {
        if (currentCategory == R.id.nav_crates) {
            val crate = cratesById[item.id]
            if (crate != null) {
                val intent = Intent(this, CrateDetailActivity::class.java).apply {
                    putExtra("crate", crate)
                }
                startActivity(intent)
                return
            }
        }
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("item", item)
        }
        startActivity(intent)
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        val filtered = if (q.isEmpty()) {
            allItems
        } else {
            allItems.filter { item ->
                val nameMatch = item.name?.lowercase()?.contains(q) ?: false
                val subtextMatch = item.subtext?.lowercase()?.contains(q) ?: false
                val extraMatch = item.extraSummary?.lowercase()?.contains(q) ?: false
                nameMatch || subtextMatch || extraMatch
            }
        }
        adapter.updateList(filtered)
    }

    private fun loadItemsForCategory() {
        lifecycleScope.launch {
            try {
                val items = when (currentCategory) {
                    R.id.nav_skins -> fetchSkins()
                    R.id.nav_stickers -> fetchStickers()
                    R.id.nav_highlights -> fetchHighlights()
                    R.id.nav_crates -> fetchCrates()
                    else -> emptyList()
                }
                allItems = items
                adapter.updateList(allItems)
                binding.searchView.setQuery("", false) // Clear search on category change
            } catch (e: Exception) {
                e.printStackTrace()
                // Handle error (e.g., show a toast)
            }
        }
    }

    private suspend fun fetchSkins(): List<Item> {
        return RetrofitClient.api.getSkins().map { skin ->
            Item(
                id = skin.id,
                name = skin.name,
                description = skin.description,
                image = skin.image,
                subtext = skin.weapon?.name ?: skin.rarity?.name ?: "",
                rarityColor = skin.rarity?.color
            )
        }
    }

    private suspend fun fetchStickers(): List<Item> {
        return RetrofitClient.api.getStickers().map { sticker ->
            Item(
                id = sticker.id,
                name = sticker.name,
                description = sticker.description,
                image = sticker.image,
                subtext = sticker.rarity?.name ?: "",
                rarityColor = sticker.rarity?.color
            )
        }
    }

    private suspend fun fetchHighlights(): List<Item> {
        return RetrofitClient.api.getHighlights().map { h ->
            Item(
                id = h.id,
                name = h.name,
                description = h.description,
                image = h.image,
                subtext = h.tournament_event ?: "",
                rarityColor = null,
                videoUrl = h.video,
                extraSummary = "${h.team0 ?: ""} vs ${h.team1 ?: ""} • ${h.map ?: ""}"
            )
        }
    }

    private suspend fun fetchCrates(): List<Item> {
        val crates = RetrofitClient.api.getCrates()
        // store map for later detail view
        cratesById = crates.associateBy { it.id }
        return crates.map { c ->
            val containsSummary = (c.contains ?: emptyList()).take(3).mapNotNull { it.name }.joinToString(", ")
            Item(
                id = c.id,
                name = c.name,
                description = c.description,
                image = c.image ?: "",
                subtext = c.type ?: "",
                rarityColor = null,
                videoUrl = null,
                extraSummary = if (containsSummary.isNotEmpty()) containsSummary else null
            )
        }
    }
}
