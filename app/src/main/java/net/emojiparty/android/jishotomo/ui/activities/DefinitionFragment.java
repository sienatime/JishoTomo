package net.emojiparty.android.jishotomo.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
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
  public static final int ENTRY_EMPTY = -1;
  private AnalyticsLogger analyticsLogger;

  public static void replaceInContainer(FragmentManager fragmentManager, int entryId,
      int containerId) {
    DefinitionFragment fragment = new DefinitionFragment();
    Bundle bundle = new Bundle();
    bundle.putInt(ENTRY_ID_EXTRA, entryId);
    fragment.setArguments(bundle);
    fragmentManager.beginTransaction().replace(containerId, fragment).commit();
  }

  @Nullable @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    ViewDataBinding binding =
        DataBindingUtil.inflate(inflater, R.layout.fragment_definition, container, false);
    root = binding.getRoot();
    FragmentActivity activity = getActivity();
    binding.setLifecycleOwner(activity);
    setupViewModel(getArguments(), binding);
    analyticsLogger = ((JishoTomoApp) activity.getApplication()).getAnalyticsLogger();
    return root;
  }

  private void setupViewModel(Bundle bundle, ViewDataBinding binding) {
    RecyclerView sensesRecyclerView = root.findViewById(R.id.senses_rv);
    final DataBindingAdapter adapter = new DataBindingAdapter(R.layout.list_item_sense);
    sensesRecyclerView.setAdapter(adapter);
    int entryId = findEntryId(bundle);
    if (entryId != ENTRY_EMPTY) {
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
    } else {
      root.findViewById(R.id.empty).setVisibility(View.VISIBLE);
    }
  }

  private int findEntryId(Bundle bundle) {
    if (bundle == null) {
      return ENTRY_EMPTY;
    }
    return bundle.getInt(ENTRY_ID_EXTRA, ENTRY_EMPTY);
  }
}
