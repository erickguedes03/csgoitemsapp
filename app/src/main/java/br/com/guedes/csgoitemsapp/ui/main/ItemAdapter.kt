package br.com.guedes.csgoitemsapp.ui.main

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.guedes.csgoitemsapp.R
import br.com.guedes.csgoitemsapp.databinding.GridItemRowBinding
import br.com.guedes.csgoitemsapp.model.Item
import coil.load

class ItemAdapter(
    private var items: List<Item>,
    private val onClick: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    fun updateList(new: List<Item>) {
        items = new
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = GridItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.itemView.setOnClickListener { onClick(item) }
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: GridItemRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.tvName.text = item.name
            binding.imgItem.load(item.image) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
            }
            try {
                val color = Color.parseColor(item.rarityColor)
                binding.rarityIndicator.setBackgroundColor(color)
            } catch (e: Exception) {
                binding.rarityIndicator.setBackgroundColor(Color.GRAY)
            }
        }
    }
}
