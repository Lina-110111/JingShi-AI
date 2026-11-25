package cn.edu.sicnu.cs.hln.jingshiai.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cn.edu.sicnu.cs.hln.jingshiai.databinding.ItemGarbageCardBinding

/**
 * 底部待分类卡片列表的适配器。
 *
 * 卡片显示 emoji 图标 + 名称，长按回调外层启动拖拽。
 */
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
        holder.binding.tvGarbageIcon.text = item.emoji

        holder.binding.root.setOnLongClickListener { view ->
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                onLongPressCard(view, item, currentPosition)
            }
            true
        }
    }

    override fun getItemCount(): Int = data.size

    /** 换一个模式 / 再来一局时整体替换列表 */
    fun submit(newItems: List<GarbageItem>) {
        data.clear()
        data.addAll(newItems)
        notifyDataSetChanged()
    }

    fun remainingCount(): Int = data.size

    fun removeAt(position: Int) {
        if (position < 0 || position >= data.size) return
        data.removeAt(position)
        notifyItemRemoved(position)
    }
}
