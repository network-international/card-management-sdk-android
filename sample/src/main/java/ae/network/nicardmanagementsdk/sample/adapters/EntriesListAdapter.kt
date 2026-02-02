package ae.network.nicardmanagementsdk.sample.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ae.network.nicardmanagementsdk.sample.models.EntriesItemModel
import com.example.nicardmanagementapp.databinding.EntriesItemBinding

class EntriesListAdapter : RecyclerView.Adapter<EntriesListAdapter.EntriesListViewHolder>() {

    private var items: List<EntriesItemModel> = listOf()

    class EntriesListViewHolder(val binding: EntriesItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntriesListViewHolder {
        val binding = EntriesItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return EntriesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntriesListViewHolder, position: Int) {
        val currentItem = items[position]
        holder.binding.entryLabelTextView.text = currentItem.label
        holder.binding.entryValueEditText.hint = currentItem.placeHolder
        holder.binding.entryValueEditText.setText(currentItem.value)
    }

    override fun getItemCount(): Int {
        return items.count()
    }

    //simple all items update*
    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<EntriesItemModel>) {
        this.items = items
        notifyDataSetChanged()
    }
}