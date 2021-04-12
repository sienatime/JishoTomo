package net.emojiparty.android.jishotomo.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.emojiparty.android.jishotomo.R.id
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.databinding.ActivityDefinitionBinding
import net.emojiparty.android.jishotomo.ui.activities.DefinitionFragment.Companion.instance

class DefinitionActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_definition)
    val binding = ActivityDefinitionBinding.inflate(layoutInflater)
    setSupportActionBar(binding.definitionToolbar)

    val entryId = findEntryId(intent)
    val fragment = instance(entryId)
    supportFragmentManager.beginTransaction()
      .replace(id.definition_activity_fragment_container, fragment)
      .commit()
  }

  private fun findEntryId(intent: Intent?): Int {
    if (intent == null) {
      return DefinitionFragment.ENTRY_EMPTY
    }
    return if (intent.hasExtra(DefinitionFragment.ENTRY_ID_EXTRA)) {
      intent.getIntExtra(
        DefinitionFragment.ENTRY_ID_EXTRA,
        DefinitionFragment.ENTRY_EMPTY
      )
    } else {
      DefinitionFragment.ENTRY_EMPTY
    }
  }
}
