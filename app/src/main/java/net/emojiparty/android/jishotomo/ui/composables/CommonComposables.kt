package net.emojiparty.android.jishotomo.ui.composables

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.emojiparty.android.jishotomo.ui.JishoTomoTheme
import net.emojiparty.android.jishotomo.ui.LineGray
import net.emojiparty.android.jishotomo.ui.presentation.AndroidResourceFetcher

@Composable
fun Divider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(LineGray)
  )
}

@Composable
fun SecondaryButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit
) {
  Button(
    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondaryVariant),
    onClick = onClick,
    modifier = modifier,
    content = content
  )
}

@Composable
fun JlptLevelPill(jlptLevel: Int?, context: Context) {
  jlptLevel?.let {
    Box(
      modifier = Modifier
        .clip(RoundedCornerShape(4.dp))
        .background(MaterialTheme.colors.onSurface)
    ) {
      val stringId = AndroidResourceFetcher(context.resources, context.packageName)
        .stringForJlptLevel(jlptLevel)

      Text(
        text = context.getString(stringId),
        modifier = Modifier.padding(4.dp),
        style = TextStyle(
          color = MaterialTheme.colors.surface,
          fontSize = 14.sp
        )
      )
    }
  }
}

@Preview
@Composable
private fun JlptPreview() {
  JishoTomoTheme {
    JlptLevelPill(jlptLevel = 5, context = LocalContext.current)
  }
}
