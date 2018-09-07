package net.emojiparty.android.jishotomo.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.databinding.ActivityDefinitionBinding;
import net.emojiparty.android.jishotomo.ui.adapters.DataBindingAdapter;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModelFactory;

public class DefinitionActivity extends AppCompatActivity {
  public static final String ENTRY_ID_EXTRA = "ENTRY_ID_EXTRA";
  public static final int ENTRY_NOT_FOUND = -1;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_definition);
    setupViewModel(getIntent());
    setupToolbar();
    setupFab();
  }

  private void setupViewModel(Intent intent) {
    ActivityDefinitionBinding binding =
        DataBindingUtil.setContentView(this, R.layout.activity_definition);
    binding.setLifecycleOwner(DefinitionActivity.this);
    RecyclerView sensesRecyclerView = findViewById(R.id.senses_rv);
    final DataBindingAdapter adapter = new DataBindingAdapter(R.layout.list_item_sense);
    sensesRecyclerView.setAdapter(adapter);
    int entryId = findEntryId(intent);
    if (entryId != ENTRY_NOT_FOUND) {
      EntryViewModel viewModel = ViewModelProviders.of(this,
          new EntryViewModelFactory(getApplication(), this, entryId))
          .get(EntryViewModel.class);
      viewModel.entry.observe(this, (@Nullable EntryWithAllSenses entry) -> {
        if (entry != null) {
          binding.setPresenter(entry);
          adapter.setItems(entry.getSenses());
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

  private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  private void setupFab() {
    FloatingActionButton fab = findViewById(R.id.fab);
    fab.setOnClickListener((View view) -> {
      Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
          .setAction("Action", null)
          .show();
    });
  }
}
