package ae.network.nicardmanagementsdk.presentation.adapters

import ae.network.nicardmanagementsdk.R
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

        // Replace DataBinding assignment with programmatic view updates
        // (removes: holder.binding.itemModel = currentItem)
        updateBulletView(holder.binding, currentItem)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<PinBulletModel>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun notifyUpdate(position: Int) {
        notifyItemChanged(position)
    }

    private fun updateBulletView(binding: SetPinBulletItemBinding, bulletModel: PinBulletModel) {
        // Replace custom binding adapter: @{itemModel.checked ? @drawable/circle_solid : @drawable/circle_empty}
        val drawable = if (bulletModel.checked) {
            R.drawable.circle_solid
        } else {
            R.drawable.circle_empty
        }
        binding.imageView.setImageResource(drawable)
    }
}
