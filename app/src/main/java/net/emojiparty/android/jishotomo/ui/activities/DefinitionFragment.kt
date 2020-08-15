package net.emojiparty.android.jishotomo.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_definition.no_entry_textview
import net.emojiparty.android.jishotomo.BR
import net.emojiparty.android.jishotomo.JishoTomoApp
import net.emojiparty.android.jishotomo.R
import net.emojiparty.android.jishotomo.R.layout
import net.emojiparty.android.jishotomo.analytics.AnalyticsLogger
import net.emojiparty.android.jishotomo.data.models.EntryWithAllSenses
import net.emojiparty.android.jishotomo.data.models.SenseWithCrossReferences
import net.emojiparty.android.jishotomo.ui.adapters.DataBindingAdapter
import net.emojiparty.android.jishotomo.ui.presentation.SensePresenter
import net.emojiparty.android.jishotomo.ui.viewmodels.EntryViewModel

class DefinitionFragment : Fragment() {
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val binding = DataBindingUtil.inflate<ViewDataBinding>(
        inflater, layout.fragment_definition, container, false
    )
    val root = binding.root
    binding.lifecycleOwner = activity
    val analyticsLogger = (requireActivity().application as JishoTomoApp).analyticsLogger
    setupViewModel(arguments, binding, root, analyticsLogger)
    return root
  }

  private fun setupViewModel(
    bundle: Bundle?,
    binding: ViewDataBinding,
    root: View,
    analyticsLogger: AnalyticsLogger
  ) {
    val sensesRecyclerView: RecyclerView = root.findViewById(R.id.senses_rv)
    val adapter = DataBindingAdapter(layout.list_item_sense)
    sensesRecyclerView.adapter = adapter

    val entryId = findEntryId(bundle)
    if (entryId != ENTRY_EMPTY) {

      val viewModel: EntryViewModel by viewModels()
      viewModel
          .entryLiveData(viewLifecycleOwner, entryId)
          .observe(viewLifecycleOwner, Observer { entry: EntryWithAllSenses ->
            binding.setVariable(BR.presenter, entry)
            adapter.setItems(getPresenters(entry.senses))
            analyticsLogger.logViewItem(entry.entry.id, entry.kanjiOrReading)
        }
      )
    } else {
      no_entry_textview.visibility = View.VISIBLE
    }
  }

  private fun getPresenters(senses: List<SenseWithCrossReferences>): List<SensePresenter?> {
    return  senses.map { SensePresenter(it) }
  }

  private fun findEntryId(bundle: Bundle?): Int {
    return bundle?.getInt(ENTRY_ID_EXTRA, ENTRY_EMPTY) ?: ENTRY_EMPTY
  }

  companion object {
    const val ENTRY_ID_EXTRA = "ENTRY_ID_EXTRA"
    const val ENTRY_EMPTY = -1

    @JvmStatic fun instance(entryId: Int): DefinitionFragment {
      val fragment = DefinitionFragment()
      val bundle = Bundle()
      bundle.putInt(ENTRY_ID_EXTRA, entryId)
      fragment.arguments = bundle
      return fragment
    }
  }
}
