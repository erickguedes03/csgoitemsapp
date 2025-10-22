package br.com.guedes.csgoitemsapp.ui.detail

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityDetailBinding
import br.com.guedes.csgoitemsapp.model.Item
import coil.load

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("item", Item::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("item") as? Item
        }

        item?.let {
            binding.tvDetailName.text = it.name
            binding.tvDetailSub.text = it.subtext
            binding.tvDescription.text = it.description ?: "No description available."
            binding.imgDetail.load(it.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
        }
    }
}
