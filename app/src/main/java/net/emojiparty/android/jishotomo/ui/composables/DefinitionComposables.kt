package net.emojiparty.android.jishotomo.ui.composables

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ui.JishoTomoTheme
import net.emojiparty.android.jishotomo.ui.presentation.SensePresenter
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel

@Composable
fun DefinitionScreen(viewModel: EntryViewModel) {
  val entry by viewModel.entryLiveData().observeAsState()

  entry?.let { it ->
    DefinitionContent(
      entry = it,
      crossReferences = viewModel.getCrossReferencesForSense(it.id)
    )
  }
}

@Composable
private fun DefinitionContent(entry: EntryWithAllSenses, crossReferences: List<CrossReferencedEntry>) {
  val context = LocalContext.current
  Surface {
    Box(
      modifier = Modifier.fillMaxWidth(),
      contentAlignment = Alignment.TopEnd
    ) {
      JlptLevelPill(
        jlptLevel = entry.jlptLevel,
        context = context
      )
    }

    Column {
      if (entry.primaryKanji != null) {
        Reading(
          reading = entry.primaryReading
        )
        Entry(entry.primaryKanji!!)
      } else {
        Entry(entry.primaryReading)
      }

      SensesList(
        presenters = entry.senses.map { sense ->
          SensePresenter(sense, crossReferences)
        }
      )

      if (entry.hasAlternates) {
        Divider()
        SubSection(header = context.getString(R.string.alt_kanji), text = entry.alternateKanji)
        SubSection(header = context.getString(R.string.alt_readings), text = entry.alternateReadings)
      }
    }
  }
}

@Composable
fun NoEntries() {
  Text(
    text = LocalContext.current.getString(R.string.select_something),
    style = MaterialTheme.typography.body1
  )
}

@Composable
private fun Reading(reading: String) {
  Text(
    text = reading,
    style = MaterialTheme.typography.body1
  )
}

@Composable
private fun Entry(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.h1
  )
}

@Composable
private fun SubSection(header: String, text: String?) {
  text?.let {
    Text(
      text = header.uppercase(),
      modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
      style = MaterialTheme.typography.caption
    )
    Text(
      text = text,
      style = MaterialTheme.typography.body1
    )
  }
}

@Preview(
  name = "Kanji and reading",
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DefinitionScreenKanjiPreview() {
  JishoTomoTheme {
    DefinitionContent(
      entry = makeEntry(
        "ねこ",
        "猫",
        "ねこ",
        "猫猫",
        5,
        makeSense("cat", "n")
      ),
      crossReferences = listOf(
        CrossReferencedEntry(
          id = 4,
          "半片",
          "",
          senseId = 9
        ),
        CrossReferencedEntry(
          id = 4,
          "半片",
          "",
          senseId = 9
        )
      )
    )
  }
}

@Preview(
  name = "Just reading",
  uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun DefinitionScreenReadingPreview() {
  JishoTomoTheme {
    DefinitionContent(
      entry = makeEntry(
        "ねこ",
        null,
        senses = arrayOf(makeSense("cat", "n"))
      ),
      crossReferences = emptyList()
    )
  }
}

private fun makeSense(
  gloss: String,
  partsOfSpeech: String? = null
): Sense {
  return Sense(
    0,
    0,
    partsOfSpeech,
    gloss
  )
}

private fun makeEntry(
  reading: String,
  kanji: String? = null,
  otherReadings: String? = null,
  otherKanji: String? = null,
  jlptLevel: Int? = null,
  vararg senses: Sense
): EntryWithAllSenses {
  val finalSenses = if (senses.isNotEmpty()) {
    senses.toList()
  } else {
    emptyList()
  }

  return EntryWithAllSenses(
    net.emojiparty.android.jishotomo.data.room.Entry(
      0,
      kanji,
      reading,
      otherReadings = otherReadings,
      otherKanji = otherKanji,
      jlptLevel = jlptLevel
    ),
    finalSenses
  )
}
