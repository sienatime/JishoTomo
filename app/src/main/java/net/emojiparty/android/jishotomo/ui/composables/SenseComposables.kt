package net.emojiparty.android.jishotomo.ui.composables

import android.content.Context
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.appcompattheme.AppCompatTheme
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.data.models.CrossReferencedEntry
import net.emojiparty.android.jishotomo.data.room.Sense
import net.emojiparty.android.jishotomo.ui.presentation.EntryClickHandler
import net.emojiparty.android.jishotomo.ui.presentation.SensePresenter

@Composable
fun SensesList(presenters: List<SensePresenter>) {
  Column {
    for (presenter in presenters) {
      SenseItem(presenter)
    }
  }
}

@Composable
private fun SenseItem(presenter: SensePresenter) {
  val context = LocalContext.current
  Surface {
    Column(
      modifier = Modifier.padding(bottom = 24.dp)
    ) {
      PartsOfSpeech(
        isVisible = presenter.partsOfSpeechIsVisible(),
        partsOfSpeech = presenter.partsOfSpeech(context).uppercase()
      )
      Divider()
      Gloss(presenter.gloss())
      AppliesTo(appliesTo = presenter.appliesToText(context))
      CrossReferences(presenter.crossReferenceLinks())
    }
  }
}

@Composable
private fun PartsOfSpeech(isVisible: Boolean, partsOfSpeech: String) {
  if (isVisible) {
    Text(
      text = partsOfSpeech,
      modifier = Modifier.padding(top = 24.dp, bottom = 8.dp),
      style = TextStyle(fontWeight = FontWeight.Bold)
    )
  }
}

@Composable
private fun Gloss(gloss: String) {
  Text(
    text = gloss,
    modifier = Modifier.padding(top = 20.dp),
    style = TextStyle(
      fontSize = 20.sp,
      lineHeight = 26.sp,
      fontFamily = FontFamily.SansSerif
    )
  )
}

@Composable
private fun AppliesTo(
  appliesTo: String?
) {
  appliesTo?.let {
    Text(
      text = appliesTo,
      modifier = Modifier
        .padding(top = 20.dp)
        .focusable(),
      style = TextStyle(
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontFamily = FontFamily.SansSerif,
        localeList = LocaleList(Locale("JP"), Locale.current)
      )
    )
  }
}

@Composable
private fun CrossReferences(crossReferences: List<CrossReferencedEntry>) {
  if (crossReferences.isNotEmpty()) {
    val context = LocalContext.current
    Text(
      text = context.getString(string.see_also),
      modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
      style = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontFamily.SansSerif
      )
    )
    Row {
      for (crossReference in crossReferences) {
        CrossReferenceButton(crossReference, context)
      }
    }
  }
}

@Composable
private fun CrossReferenceButton(
  crossReference: CrossReferencedEntry,
  context: Context
) {
  Button(
    onClick = {
      EntryClickHandler.open(
        context, crossReference.id
      )
    },
    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, end = 10.dp),
  ) {
    Text(
      text = crossReference.kanjiOrReading,
      style = TextStyle(fontSize = 24.sp)
    )
  }
}

@Preview
@Composable
fun SensePreview() {
  val presenters = listOf(
    SensePresenter(
      Sense(
        1, 3, "n", "cat;good cat"
      ),
      emptyList()
    ),
    SensePresenter(
      Sense(
        1,
        3,
        null,
        "stuff\nand other things\nlots of words on this one, wow",
        "半片"
      ),
      listOf(
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
  )
  AppCompatTheme {
    SensesList(presenters)
  }
}
