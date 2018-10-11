package net.emojiparty.android.jishotomo.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import net.emojiparty.android.jishotomo.R;

public class AboutAppActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_about_app);
    getSupportActionBar().setTitle(R.string.about);
    TextView aboutText = findViewById(R.id.about_text);
    aboutText.setMovementMethod(LinkMovementMethod.getInstance());
  }
}
