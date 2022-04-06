package net.emojiparty.android.jishotomo.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import net.emojiparty.android.jishotomo.R.color

@Composable
fun Divider() {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(Color(color.lineGray))
  )
}
