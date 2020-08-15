package net.emojiparty.android.jishotomo

import net.emojiparty.android.jishotomo.ui.presentation.ResourceFetcher

class TestResources : ResourceFetcher {
  private val keys = arrayOf(
    "adj_f", "adj_i", "adj_ix", "adj_ku", "adj_na", "adj_nari", "adj_no", "adj_pn", "adj_shiku",
    "adj_t", "adv_to", "adv", "aux_adj", "aux_v", "aux", "conj", "cop_da", "ctr", "exp", "intj",
    "n_adv", "n_pr", "n_pref", "n_suf", "n_t", "n", "num", "pn", "pref", "prt", "suf", "unc",
    "v1_s", "v1", "v2a_s", "v2b_k", "v2d_s", "v2g_k", "v2g_s", "v2h_k", "v2h_s", "v2k_k",
    "v2k_s", "v2m_s", "v2n_s", "v2r_k", "v2r_s", "v2s_s", "v2t_k", "v2t_s", "v2w_s", "v2y_k",
    "v2y_s", "v2z_s", "v4b", "v4g", "v4h", "v4k", "v4m", "v4r", "v4s", "v4t", "v5aru", "v5b",
    "v5g", "v5k_s", "v5k", "v5m", "v5n", "v5r_i", "v5r", "v5s", "v5t", "v5u_s", "v5u", "vi",
    "vk", "vn", "vr", "vs_c", "vs_i", "vs_s", "vs", "vt", "vz"
  )
  private val partsOfSpeech = arrayOf(
    "Adjective", "Adjective", "Adjective", "Adjective", "Adjective", "Adjective", "Adjective",
    "Pre-Noun Adjectival", "Adjective", "Adjective", "Adverb", "Adverb", "Auxiliary Adjective",
    "Auxiliary Verb", "Auxiliary", "Conjunction", "Copula", "Counter", "Expression",
    "Interjection", "Adverbial Noun", "Proper Noun", "Noun (Prefix)", "Noun (Suffix)",
    "Noun (Temporal)", "Noun", "Numeric", "Pronoun", "Prefix", "Particle", "Suffix",
    "Unclassified", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb",
    "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb",
    "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb",
    "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb",
    "Verb", "Verb (Intransitive)", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb", "Verb",
    "Verb (Transitive)", "Verb"
  )

  override fun getIdentifier(name: String, defType: String, defPackage: String): Int {
    return keys.indexOf(name)
  }

  override fun getString(id: Int): String {
    return partsOfSpeech[id]
  }
}
