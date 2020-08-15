package net.emojiparty.android.jishotomo.ui.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_about_app.about_text
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.R.string

class AboutAppActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(layout.activity_about_app)
    supportActionBar!!.setTitle(string.about)
    about_text.movementMethod = LinkMovementMethod.getInstance()
  }
}
