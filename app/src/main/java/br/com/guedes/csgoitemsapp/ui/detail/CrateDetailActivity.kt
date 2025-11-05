package br.com.guedes.csgoitemsapp.ui.detail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.widget.ImageView
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.model.Crate
import coil.load

class CrateDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crate_detail)

        val btnBack = findViewById<Button>(R.id.btnCrateBack)
        btnBack.setOnClickListener { onBackPressed() }

        val crate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("crate", Crate::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("crate") as? Crate
        }

        crate?.let { c ->
            val imgHeader = findViewById<ImageView>(R.id.imgCrateHeader)
            val tvName = findViewById<TextView>(R.id.tvCrateName)
            val tvType = findViewById<TextView>(R.id.tvCrateType)
            val tvDesc = findViewById<TextView>(R.id.tvCrateDescription)
            val recyclerContains = findViewById<RecyclerView>(R.id.recyclerContains)
            val recyclerRare = findViewById<RecyclerView>(R.id.recyclerRare)

            imgHeader.load(c.image) {
                placeholder(R.drawable.ic_launcher_background)
                crossfade(true)
            }

            tvName.text = c.name
            tvType.text = c.type ?: ""
            tvDesc.text = c.description ?: ""

            val adapter = CrateContainAdapter((c.contains ?: emptyList()))
            recyclerContains.layoutManager = LinearLayoutManager(this)
            recyclerContains.adapter = adapter

            val rareAdapter = CrateContainAdapter((c.contains_rare ?: emptyList()))
            recyclerRare.layoutManager = LinearLayoutManager(this)
            recyclerRare.adapter = rareAdapter
        }
    }
}
