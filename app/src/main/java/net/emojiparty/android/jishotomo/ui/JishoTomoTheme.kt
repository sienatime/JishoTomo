package net.emojiparty.android.jishotomo.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.sp

@Composable
fun JishoTomoTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colors = if (darkTheme) {
    DarkColorPalette
  } else {
    LightColorPalette
  }

  MaterialTheme(
    colors = colors,
    typography = typography,
    content = content
  )
}

val Green = Color(0xff2e7d32)
val DarkGreen = Color(0xff005005)
val LightGreen = Color(0xff60ad5e)
val Pink = Color(0xffef9a9a)
val LightPink = Color(0xffffcccb)
val DarkPink = Color(0xffba6b6c)
val LineGray = Color(0xffdfdfdf)
val BodyTextGray = Color(0xff757575)
val DarkGray = Color(0xff404040)

private val DarkColorPalette = darkColors(
  surface = DarkGray,
  onSurface = Color.White,
  onBackground = Color.White,
  primary = DarkGreen,
  onPrimary = Color.White,
  secondary = DarkPink,
  secondaryVariant = Pink,
  onSecondary = Color.Black
)

private val LightColorPalette = lightColors(
  surface = Color.White,
  onSurface = BodyTextGray,
  onBackground = BodyTextGray,
  primary = Green,
  onPrimary = Color.White,
  secondary = Pink,
  secondaryVariant = LightPink,
  onSecondary = Color.Black
)

val typography = Typography(
  body1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 20.sp,
    lineHeight = 30.sp,
    localeList = LocaleList(Locale("JP"), Locale.current)
  ),
  button = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 24.sp
  ),
  h1 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 36.sp,
  ),
  h2 = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Light,
    fontSize = 30.sp,
  ),
  caption = TextStyle(
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
  )
)
