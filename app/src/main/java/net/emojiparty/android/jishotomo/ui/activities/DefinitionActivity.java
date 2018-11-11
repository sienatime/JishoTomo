package net.emojiparty.android.jishotomo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import net.emojiparty.android.jishotomo.R;

import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_ID_EXTRA;
import static net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.ENTRY_EMPTY;

public class DefinitionActivity extends AppCompatActivity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_definition);
    setupToolbar();
    int entryId = findEntryId(getIntent());
    DefinitionFragment fragment = DefinitionFragment.instance(entryId);
    getSupportFragmentManager().beginTransaction()
        .replace(R.id.definition_activity_fragment_container, fragment)
        .commit();
  }

  private void setupToolbar() {
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
  }

  private int findEntryId(Intent intent) {
    if (intent == null) {
      return ENTRY_EMPTY;
    }
    if (intent.hasExtra(ENTRY_ID_EXTRA)) {
      return intent.getIntExtra(ENTRY_ID_EXTRA, ENTRY_EMPTY);
    } else {
      return ENTRY_EMPTY;
    }
  }
}
