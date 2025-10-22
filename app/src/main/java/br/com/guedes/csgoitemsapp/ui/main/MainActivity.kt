package br.com.guedes.csgoitemsapp.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityMainBinding
import br.com.guedes.csgoitemsapp.model.Item
import br.com.guedes.csgoitemsapp.network.RetrofitClient
import br.com.guedes.csgoitemsapp.ui.detail.DetailActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemAdapter
    private var allItems: List<Item> = emptyList()
    private var showingSkins = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        adapter = ItemAdapter(emptyList()) { item ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("item", item)
            startActivity(intent)
        }

        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        binding.btnToggle.setOnClickListener {
            showingSkins = !showingSkins
            binding.btnToggle.text = if (showingSkins) "Skins" else "Stickers"
            loadItems()
        }

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filter(newText)
                return true
            }
        })

        binding.btnToggle.text = "Skins"
        loadItems()
    }

    private fun filter(query: String?) {
        val q = query?.trim()?.lowercase() ?: ""
        val filtered = if (q.isEmpty()) allItems else allItems.filter {
            (it.name ?: it.market_hash_name ?: "").lowercase().contains(q)
        }
        adapter.updateList(filtered)
    }

    private fun loadItems() {
        lifecycleScope.launch {
            try {
                allItems = if (showingSkins) {
                    RetrofitClient.api.getSkins()
                } else {
                    RetrofitClient.api.getStickers()
                }
                adapter.updateList(allItems)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
