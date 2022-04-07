package net.emojiparty.android.jishotomo.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.databinding.FragmentDefinitionBinding
import net.emojiparty.android.jishotomo.ui.JishoTomoTheme
import net.emojiparty.android.jishotomo.ui.composables.DefinitionScreen
import net.emojiparty.android.jishotomo.ui.composables.NoEntries
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModelFactory

class DefinitionFragment : Fragment() {

  private var _binding: FragmentDefinitionBinding? = null
  private val binding: FragmentDefinitionBinding
    get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentDefinitionBinding.inflate(layoutInflater)
    val root = binding.root
    setupViewModel(arguments)
    return root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun setupViewModel(bundle: Bundle?) {
    val entryId = findEntryId(bundle)

    if (entryId != ENTRY_EMPTY) {
      val viewModel: EntryViewModel by viewModels { EntryViewModelFactory(entryId) }

      viewModel
        .entryLiveData()
        .observe(
          viewLifecycleOwner,
          { entry: EntryWithAllSenses ->
            entryObserver(entry, viewModel)
          }
        )

      viewModel
        .isFavoritedLiveData()
        .observe(
          viewLifecycleOwner,
          { isFavorited: Boolean ->
            isFavoritedObserver(isFavorited, binding.fab)
          }
        )
    } else {
      binding.entryParent.setContent {
        JishoTomoTheme {
          NoEntries()
        }
      }
      binding.fab.visibility = View.GONE
    }
  }

  private fun findEntryId(bundle: Bundle?): Int {
    return bundle?.getInt(ENTRY_ID_EXTRA, ENTRY_EMPTY) ?: ENTRY_EMPTY
  }

  private fun entryObserver(
    entry: EntryWithAllSenses,
    viewModel: EntryViewModel
  ) {
    binding.entryParent.setContent {
      JishoTomoTheme {
        DefinitionScreen(viewModel)
      }
    }

    val analyticsLogger = (requireActivity().application as JishoTomoApp).analyticsLogger

    binding.fab.setOnClickListener {
      viewModel.toggleFavorite(analyticsLogger)
    }

    analyticsLogger.logViewItem(entry.id, entry.kanjiOrReading)
  }

  private fun isFavoritedObserver(isFavorited: Boolean, fab: FloatingActionButton) {
    if (isFavorited) {
      fab.contentDescription = getString(R.string.remove_from_favorites)
      fab.setImageResource(R.drawable.ic_star)
    } else {
      fab.contentDescription = getString(R.string.add_to_favorites)
      fab.setImageResource(R.drawable.ic_star_border)
    }
  }

  companion object {
    const val ENTRY_ID_EXTRA = "ENTRY_ID_EXTRA"
    const val ENTRY_EMPTY = -1

    fun instance(entryId: Int): DefinitionFragment {
      val fragment = DefinitionFragment()
      val bundle = Bundle()
      bundle.putInt(ENTRY_ID_EXTRA, entryId)
      fragment.arguments = bundle
      return fragment
    }
  }
}
