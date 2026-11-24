package cn.edu.sicnu.cs.hln.jingshiai.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.hln.jingshiai.databinding.ItemGarbageCardBinding

class GameAdapter(
    items: List<GarbageItem>,
    private val onLongPressCard: (View, GarbageItem, Int) -> Unit
) : RecyclerView.Adapter<GameAdapter.GarbageViewHolder>() {


    private val data: MutableList<GarbageItem> = items.toMutableList()

    inner class GarbageViewHolder(val binding: ItemGarbageCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GarbageViewHolder {
        val binding = ItemGarbageCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GarbageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GarbageViewHolder, position: Int) {
        val item = data[position]

        holder.binding.tvGarbageName.text = item.name

        if (item.drawableRes != 0) {
            holder.binding.ivGarbage.setImageResource(item.drawableRes)
        } else {
            holder.binding.ivGarbage.setImageDrawable(null)
        }

        holder.binding.root.setOnLongClickListener { view ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onLongPressCard(view, item, currentPosition)
            }
            true
        }
    }

    override fun getItemCount(): Int = data.size


    fun removeAt(position: Int) {
        if (position < 0 || position >= data.size) return
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remainingCount(): Int = data.size
}
