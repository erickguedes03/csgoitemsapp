package br.com.guedes.csgoitemsapp.ui.detail

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.ActivityAgentDetailBinding
import br.com.guedes.csgoitemsapp.model.Agent
import coil.load

class AgentDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAgentDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // get agent
        val agent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("agent", Agent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("agent") as? Agent
        }

        agent?.let {
            supportActionBar?.title = it.name ?: "Agent"
            binding.tvAgentName.text = it.name
            binding.tvAgentDescription.text = it.description ?: ""
            binding.tvAgentMarket.text = it.market_hash_name ?: ""
            binding.tvAgentTeam.text = it.team?.name ?: ""

            // rarity
            binding.tvRarityName.text = it.rarity?.name ?: ""
            val colorStr = it.rarity?.color
            try {
                if (!colorStr.isNullOrBlank()) {
                    binding.viewRarityColor.setBackgroundColor(Color.parseColor(colorStr))
                }
            } catch (e: Exception) {
                // ignore parse error
            }

            // collections - show first collection image + name
            val ll = findViewById<LinearLayout>(R.id.llCollections)
            it.collections?.firstOrNull()?.let { col ->
                val iv = ImageView(this)
                val lp = LinearLayout.LayoutParams(64, 64)
                lp.marginEnd = 12
                iv.layoutParams = lp
                iv.scaleType = ImageView.ScaleType.CENTER_CROP
                iv.load(col.image) { placeholder(R.drawable.ic_launcher_background) }
                ll.addView(iv)

                val tv = TextView(this)
                tv.text = col.name ?: ""
                tv.gravity = Gravity.CENTER_VERTICAL
                ll.addView(tv)
            }

            // main image
            binding.ivAgent.load(it.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
