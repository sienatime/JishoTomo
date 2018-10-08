package net.emojiparty.android.jishotomo.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.emojiparty.android.jishotomo.BR;
import net.emojiparty.android.jishotomo.JishoTomoApp;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.ui.adapters.DataBindingAdapter;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModelFactory;

public class DefinitionFragment extends Fragment {
  private View root;
  public static final String ENTRY_ID_EXTRA = "ENTRY_ID_EXTRA";
  public static final int ENTRY_NOT_FOUND = -1;
  private AnalyticsLogger analyticsLogger;

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    ViewDataBinding binding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_definition, container, false);
    root = binding.getRoot();
    FragmentActivity activity = getActivity();
    binding.setLifecycleOwner(activity);
    setupViewModel(activity.getIntent(), binding);
    analyticsLogger = ((JishoTomoApp) activity.getApplication()).getAnalyticsLogger();
    return root;
  }

  private void setupViewModel(Intent intent, ViewDataBinding binding) {
    RecyclerView sensesRecyclerView = root.findViewById(R.id.senses_rv);
    final DataBindingAdapter adapter = new DataBindingAdapter(R.layout.list_item_sense);
    sensesRecyclerView.setAdapter(adapter);
    int entryId = findEntryId(intent);
    if (entryId != ENTRY_NOT_FOUND) {
      EntryViewModel viewModel = ViewModelProviders.of(this,
          new EntryViewModelFactory(getActivity().getApplication(), this, entryId))
          .get(EntryViewModel.class);
      viewModel.entry.observe(this, (@Nullable EntryWithAllSenses entry) -> {
        if (entry != null) {
          binding.setVariable(BR.presenter, entry);
          adapter.setItems(entry.getSenses());
          analyticsLogger.logViewItem(entry.entry.getId(), entry.getKanjiOrReading());
        }
      });
    }
  }

  private int findEntryId(Intent intent) {
    if (intent == null) {
      return ENTRY_NOT_FOUND;
    }
    if (intent.hasExtra(ENTRY_ID_EXTRA)) {
      return intent.getIntExtra(ENTRY_ID_EXTRA, ENTRY_NOT_FOUND);
    } else if (intent.getData() != null) {
      String id = intent.getData().getLastPathSegment();
      return Integer.parseInt(id);
    } else {
      return ENTRY_NOT_FOUND;
    }
  }
}
