package net.emojiparty.android.jishotomo.ui.adapters;

import androidx.paging.PagedListAdapter;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import net.emojiparty.android.jishotomo.BR;
import net.emojiparty.android.jishotomo.data.models.SearchResultEntry;

public class PagedEntriesAdapter
    extends PagedListAdapter<SearchResultEntry, PagedEntriesAdapter.DataBindingViewHolder> {
  private int layoutId;

  public PagedEntriesAdapter(int layoutId) {
    super(DIFF_CALLBACK);
    this.layoutId = layoutId;
  }

  // referenced PagedListAdapter documentation
  @Override public void onBindViewHolder(@NonNull DataBindingViewHolder holder, int position) {
    SearchResultEntry entry = getItem(position);
    if (entry != null) {
      holder.bind(entry);
    } else {
      holder.clear();
    }
  }

  @NonNull @Override
  public DataBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    Context context = parent.getContext();
    LayoutInflater layoutInflater = LayoutInflater.from(context);
    ViewDataBinding binding = DataBindingUtil.inflate(layoutInflater, layoutId, parent, false);
    return new DataBindingViewHolder(binding, context);
  }

  // https://medium.com/google-developers/android-data-binding-recyclerview-db7c40d9f0e4
  static class DataBindingViewHolder extends RecyclerView.ViewHolder {
    ViewDataBinding binding;
    Context context;

    DataBindingViewHolder(ViewDataBinding binding, Context context) {
      super(binding.getRoot());
      this.binding = binding;
      this.context = context;
    }

    void bind(SearchResultEntry presenter) {
      binding.setVariable(BR.presenter, presenter);
      binding.executePendingBindings();
    }

    void clear() {
      binding.unbind();
    }
  }

  private static DiffUtil.ItemCallback<SearchResultEntry> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<SearchResultEntry>() {
        @Override public boolean areItemsTheSame(SearchResultEntry oldSearchResultEntry,
            SearchResultEntry newSearchResultEntry) {
          return oldSearchResultEntry.getId() == newSearchResultEntry.getId();
        }

        @Override public boolean areContentsTheSame(SearchResultEntry oldSearchResultEntry,
            @NonNull SearchResultEntry newSearchResultEntry) {
          return oldSearchResultEntry.equals(newSearchResultEntry);
        }
      };
}
