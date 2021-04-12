package net.emojiparty.android.jishotomo.ui.activities

import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.appcompat.app.AppCompatActivity
import net.emojiparty.android.jishotomo.R.string
import net.emojiparty.android.jishotomo.databinding.ActivityAboutAppBinding

class AboutAppActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    supportActionBar!!.setTitle(string.about)
    val binding = ActivityAboutAppBinding.inflate(layoutInflater)
    binding.aboutText.movementMethod = LinkMovementMethod.getInstance()
    val view = binding.root
    setContentView(view)
  }
}
