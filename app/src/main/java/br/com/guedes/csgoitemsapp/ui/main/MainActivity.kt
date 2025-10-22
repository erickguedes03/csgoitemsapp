package br.com.guedes.csgoitemsapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityMainBinding
import br.com.guedes.csgoitemsapp.model.Item
import br.com.guedes.csgoitemsapp.network.RetrofitClient
import br.com.guedes.csgoitemsapp.ui.detail.DetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemAdapter
    private var allItems: List<Item> = emptyList()
    private var currentCategory = R.id.nav_skins // Default to skins

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        // Adapter and LayoutManager setup
        adapter = ItemAdapter(emptyList()) { item ->
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("item", item)
            }
            startActivity(intent)
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

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        val filtered = if (q.isEmpty()) {
            allItems
        } else {
            allItems.filter { it.name.lowercase().contains(q) }
        }
        adapter.updateList(filtered)
    }

    private fun loadItemsForCategory() {
        lifecycleScope.launch {
            try {
                val items = when (currentCategory) {
                    R.id.nav_skins -> fetchSkins()
                    R.id.nav_stickers -> fetchStickers()
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
}
