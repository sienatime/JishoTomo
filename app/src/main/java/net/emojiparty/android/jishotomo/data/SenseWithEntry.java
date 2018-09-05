package net.emojiparty.android.jishotomo.data;

public class SenseWithEntry {
  private int id;
  private int entryId;
  private String primaryKanji;
  private String primaryReading;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getEntryId() {
    return entryId;
  }

  public void setEntryId(int entryId) {
    this.entryId = entryId;
  }

  public String getPrimaryKanji() {
    return primaryKanji;
  }

  public void setPrimaryKanji(String primaryKanji) {
    this.primaryKanji = primaryKanji;
  }

  public String getPrimaryReading() {
    return primaryReading;
  }

  public void setPrimaryReading(String primaryReading) {
    this.primaryReading = primaryReading;
  }
}
