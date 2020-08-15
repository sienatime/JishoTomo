package net.emojiparty.android.jishotomo.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.emojiparty.android.jishotomo.BR
import java.util.ArrayList

class DataBindingAdapter(private val layoutId: Int) : RecyclerView.Adapter<ViewHolder>() {
  private var items: List<*> = ArrayList<Any>()

  fun setItems(items: List<*>) {
    this.items = items
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(
    parent: ViewGroup,
    viewType: Int
  ): ViewHolder {
    val context = parent.context
    val layoutInflater = LayoutInflater.from(context)
    val binding =
      DataBindingUtil.inflate<ViewDataBinding>(layoutInflater, layoutId, parent, false)
    return DataBindingViewHolder(binding, context)
  }

  override fun onBindViewHolder(
    holder: ViewHolder,
    position: Int
  ) {
    val viewHolder = holder as DataBindingViewHolder
    viewHolder.bind(items[position])
  }

  override fun getItemCount(): Int {
    return items.size
  }

  // https://medium.com/google-developers/android-data-binding-recyclerview-db7c40d9f0e4
  internal class DataBindingViewHolder(
    var binding: ViewDataBinding,
    var context: Context
  ) : ViewHolder(binding.root) {
    fun bind(presenter: Any?) {
      binding.setVariable(BR.presenter, presenter)
      binding.executePendingBindings()
    }

  }

}