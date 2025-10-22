package br.com.guedes.csgoitemsapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.guedes.csgoitemsapp.databinding.ActivityDetailBinding
import br.com.guedes.csgoitemsapp.model.Item
import coil.load
import br.com.guedes.csgoitemsapp.R

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra("item") as? Item

        item?.let {
            binding.tvDetailName.text = it.name ?: it.market_hash_name ?: "Sem nome"
            binding.tvDetailSub.text = it.weapon ?: ""
            binding.tvDescription.text = it.description ?: "Sem descrição disponível."
            binding.imgDetail.load(it.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
        }
    }
}
