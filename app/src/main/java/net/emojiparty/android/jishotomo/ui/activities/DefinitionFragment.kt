package net.emojiparty.android.jishotomo.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.emojiparty.android.jishotomo.BR
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.databinding.FragmentDefinitionBinding
import net.emojiparty.android.jishotomo.ui.adapters.DataBindingAdapter
import net.emojiparty.android.jishotomo.ui.presentation.SensePresenter
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
    _binding = DataBindingUtil.inflate<FragmentDefinitionBinding>(
      inflater, layout.fragment_definition, container, false
    )
    val root = binding.root
    binding.lifecycleOwner = activity
    setupViewModel(arguments)
    return root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  private fun setupViewModel(bundle: Bundle?) {
    val sensesRecyclerView: RecyclerView = binding.sensesRv
    val adapter = DataBindingAdapter(layout.list_item_sense)
    sensesRecyclerView.adapter = adapter

    val entryId = findEntryId(bundle)

    if (entryId != ENTRY_EMPTY) {
      val viewModel: EntryViewModel by viewModels { EntryViewModelFactory(entryId) }

      viewModel
        .entryLiveData()
        .observe(
          viewLifecycleOwner,
          { entry: EntryWithAllSenses ->
            entryObserver(entry, viewModel, adapter)
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
      binding.noEntryTextview.visibility = View.VISIBLE
    }
  }

  private fun findEntryId(bundle: Bundle?): Int {
    return bundle?.getInt(ENTRY_ID_EXTRA, ENTRY_EMPTY) ?: ENTRY_EMPTY
  }

  private fun entryObserver(
    entry: EntryWithAllSenses,
    viewModel: EntryViewModel,
    adapter: DataBindingAdapter
  ) {
    binding.setVariable(BR.presenter, entry)

    val presenters = entry.senses.map { SensePresenter(it, viewModel.getCrossReferencesForSense(it.id)) }
    adapter.setItems(presenters)

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
