package ae.network.nicardmanagementsdk.presentation.adapters

import ae.network.nicardmanagementsdk.databinding.SetPinBulletItemBinding
import ae.network.nicardmanagementsdk.presentation.models.PinBulletModel
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class BulletListAdapter : RecyclerView.Adapter<BulletListAdapter.BulletListViewHolder>() {

    private var items: List<PinBulletModel> = listOf()

    inner class BulletListViewHolder(val binding: SetPinBulletItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BulletListViewHolder {
        val binding = SetPinBulletItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BulletListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BulletListViewHolder, position: Int) {
        val currentItem = items[position]
        holder.binding.itemModel = currentItem
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    //simple all items update*
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<PinBulletModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    //update at specified index
    fun notifyUpdate(position: Int) {
        notifyItemChanged(position)
    }

}