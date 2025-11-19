package br.com.guedes.csgoitemsapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityMainBinding
import br.com.guedes.csgoitemsapp.model.Crate
import br.com.guedes.csgoitemsapp.model.Item
import br.com.guedes.csgoitemsapp.model.Agent
import br.com.guedes.csgoitemsapp.network.RetrofitClient
import br.com.guedes.csgoitemsapp.data.DataCache
import br.com.guedes.csgoitemsapp.ui.detail.CrateDetailActivity
import br.com.guedes.csgoitemsapp.ui.detail.DetailActivity
import br.com.guedes.csgoitemsapp.ui.detail.AgentDetailActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemAdapter
    private var allItems: List<Item> = emptyList()
    private var cratesById: Map<String, Crate> = emptyMap()
    private var agentsById: Map<String, Agent> = emptyMap()
    private var currentCategory = R.id.nav_skins // Default to skins
    private var searchView: SearchView? = null
    private var currentFilter: String? = null
    private val rarityNameTranslations = mapOf(
        "Consumer Grade" to "Padrão",
        "Industrial Grade" to "Industrial",
        "Mil-Spec Grade" to "Militar",
        "Restricted" to "Restrito",
        "Classified" to "Classificado",
        "Covert" to "Secreto",
        "Exotic" to "Exótico",
        "Extraordinary" to "Extraordinário",
        "Contraband" to "Contrabandeado",
        "High Grade" to "Alta Qualidade",
        "Remarkable" to "Notável"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""

        // Adapter and LayoutManager setup
        adapter = ItemAdapter(emptyList()) { item ->
            handleItemClick(item)
        }
        binding.recycler.layoutManager = GridLayoutManager(this, 2) // 2 columns grid
        binding.recycler.adapter = adapter

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as? SearchView

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })
        updateSearchHint()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                showFilterDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showFilterDialog() {
        val filterOptions = getFilterOptionsForCurrentCategory()
        if (filterOptions.isNotEmpty()) {
            FilterDialogFragment(filterOptions, { selectedOption ->
                applyAdvancedFilter(selectedOption)
            }, {
                clearAdvancedFilter()
            }).show(supportFragmentManager, "FilterDialog")
        } else {
            Toast.makeText(this, "Sem opções de filtro disponíveis", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getFilterOptionsForCurrentCategory(): List<String> {
        return when (currentCategory) {
            R.id.nav_agents -> allItems.mapNotNull { it.subtext }.distinct()
            R.id.nav_skins, R.id.nav_stickers -> allItems.mapNotNull { it.rarityName }
                .distinct()
                .map { rarityNameTranslations[it] ?: it }
            else -> emptyList()
        }
    }

    private fun applyAdvancedFilter(filter: String) {
        currentFilter = filter
        filter(searchView?.query?.toString())
    }

    private fun clearAdvancedFilter() {
        currentFilter = null
        filter(searchView?.query?.toString())
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        var filteredList = allItems

        // Apply advanced filter first
        currentFilter?.let { filter ->
            filteredList = when (currentCategory) {
                R.id.nav_agents -> filteredList.filter { it.subtext == filter }
                R.id.nav_skins, R.id.nav_stickers -> filteredList.filter { (rarityNameTranslations[it.rarityName] ?: it.rarityName) == filter }
                else -> filteredList
            }
        }

        // Then apply text search
        if (q.isNotEmpty()) {
            filteredList = filteredList.filter { item ->
                val nameMatch = item.name?.lowercase()?.contains(q) ?: false
                val subtextMatch = item.subtext?.lowercase()?.contains(q) ?: false
                val extraMatch = item.extraSummary?.lowercase()?.contains(q) ?: false
                nameMatch || subtextMatch || extraMatch
            }
        }

        adapter.updateList(filteredList)
    }

    private fun loadItemsForCategory() {
        lifecycleScope.launch {
            try {
                val items = when (currentCategory) {
                    R.id.nav_skins -> fetchSkins()
                    R.id.nav_stickers -> fetchStickers()
                    R.id.nav_highlights -> fetchHighlights()
                    R.id.nav_crates -> fetchCrates()
                    R.id.nav_agents -> fetchAgents()
                    else -> emptyList()
                }
                allItems = items
                currentFilter = null // Reset filter on category change
                adapter.updateList(allItems)
                updateSearchHint()
                searchView?.setQuery("", false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateSearchHint() {
        val hint = when (currentCategory) {
            R.id.nav_skins -> getString(R.string.search_hint_skins)
            R.id.nav_stickers -> getString(R.string.search_hint_stickers)
            R.id.nav_highlights -> getString(R.string.search_hint_highlights)
            R.id.nav_crates -> getString(R.string.search_hint_crates)
            R.id.nav_agents -> getString(R.string.search_hint_agents)
            else -> "Pesquisar..."
        }
        searchView?.queryHint = hint
    }

    private fun handleItemClick(item: Item) {
        if (currentCategory == R.id.nav_crates) {
            val crate = cratesById[item.id]
            if (crate != null) {
                val intent = Intent(this, CrateDetailActivity::class.java).apply {
                    putExtra("crate", crate)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                return
            }
        }
        if (currentCategory == R.id.nav_agents) {
            val agent = agentsById[item.id]
            if (agent != null) {
                val intent = Intent(this, AgentDetailActivity::class.java).apply {
                    putExtra("agent", agent)
                }
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                return
            }
        }
        val intent = Intent(this, DetailActivity::class.java).apply {
            putExtra("item", item)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private suspend fun fetchSkins(): List<Item> {
        return RetrofitClient.api.getSkins().map { skin ->
            Item(
                id = skin.id,
                name = skin.name,
                description = skin.description,
                image = skin.image,
                subtext = skin.weapon?.name ?: skin.rarity?.name ?: "",
                rarityColor = skin.rarity?.color,
                rarityName = skin.rarity?.name // Populate rarityName
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
                rarityColor = sticker.rarity?.color,
                rarityName = sticker.rarity?.name // Populate rarityName
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

    private suspend fun fetchAgents(): List<Item> {
        try {
            val agents = RetrofitClient.api.getAgents()
            // store map for agent details
            agentsById = agents.associateBy { it.id }
            // cache
            DataCache.putAgents(agents)
            return agents.map { a ->
                Item(
                    id = a.id,
                    name = a.name ?: "Unknown Agent",
                    description = a.description,
                    image = a.image ?: "",
                    subtext = a.team?.name ?: a.collections?.firstOrNull()?.name ?: "",
                    rarityColor = a.rarity?.color,
                    videoUrl = null,
                    extraSummary = a.market_hash_name
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // fallback to cache
            if (DataCache.hasAgents()) {
                val cached: List<Agent> = DataCache.getAgents()
                agentsById = cached.associateBy { it.id }
                Snackbar.make(binding.root, "Carregando agentes do cache", Snackbar.LENGTH_LONG).show()
                return cached.map { a ->
                    Item(
                        id = a.id,
                        name = a.name ?: "Unknown Agent",
                        description = a.description,
                        image = a.image ?: "",
                        subtext = a.team?.name ?: a.collections?.firstOrNull()?.name ?: "",
                        rarityColor = a.rarity?.color,
                        videoUrl = null,
                        extraSummary = a.market_hash_name
                    )
                }
            }
            // no cache, rethrow
            throw e
        }
    }
}
