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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import net.emojiparty.android.jishotomo.R;
import net.emojiparty.android.jishotomo.data.di.AppModule;
import net.emojiparty.android.jishotomo.data.di.DaggerAppComponent;
import net.emojiparty.android.jishotomo.data.di.RoomModule;
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry;
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses;
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences;
import net.emojiparty.android.jishotomo.data.room.EntryDao;
import net.emojiparty.android.jishotomo.data.room.SenseDao;
import net.emojiparty.android.jishotomo.databinding.ActivityDefinitionBinding;
import net.emojiparty.android.jishotomo.ui.adapters.DataBindingAdapter;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel;
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModelFactory;

public class DefinitionActivity extends AppCompatActivity {
  public static final String ENTRY_ID_EXTRA = "ENTRY_ID_EXTRA";
  public static final int ENTRY_NOT_FOUND = -1;
  @Inject public EntryDao entryDao;
  @Inject public SenseDao senseDao;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_definition);
    setupDagger();
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
    if (intent != null && intent.hasExtra(ENTRY_ID_EXTRA)) {
      int entryId = intent.getIntExtra(ENTRY_ID_EXTRA, ENTRY_NOT_FOUND);
      EntryViewModel viewModel = ViewModelProviders.of(this,
          new EntryViewModelFactory(getApplication(), entryDao, senseDao, entryId))
          .get(EntryViewModel.class);
      viewModel.entry.observe(this, (@Nullable EntryWithAllSenses entry) -> {
        if (entry != null) {
          binding.setPresenter(entry);
          adapter.setItems(entry.getSenses());
        }
      });
    }
  }

  private void setupDagger() {
    DaggerAppComponent.builder()
        .appModule(new AppModule(getApplication()))
        .roomModule(new RoomModule(getApplication()))
        .build()
        .inject(DefinitionActivity.this);
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
