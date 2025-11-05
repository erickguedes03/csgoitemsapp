package br.com.guedes.csgoitemsapp.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.model.CrateContain
import coil.load

class CrateContainAdapter(
    private val items: List<CrateContain>
) : RecyclerView.Adapter<CrateContainAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_crate_contain, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val img = itemView.findViewById<ImageView>(R.id.imgContain)
        private val tvName = itemView.findViewById<TextView>(R.id.tvContainName)
        private val tvRarity = itemView.findViewById<TextView>(R.id.tvContainRarity)

        fun bind(item: CrateContain) {
            tvName.text = item.name ?: ""
            tvRarity.text = item.rarity?.name ?: ""
            img.load(item.image) {
                placeholder(android.R.drawable.stat_sys_download)
            }
        }
    }
}
