package cu.lexz451.rentmanager.ui.settings.blacklist

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.BlackListRecord
import cu.lexz451.rentmanager.databinding.BlistListItemBinding
import cu.lexz451.rentmanager.databinding.FragmentConfigBlacklistBinding
import cu.lexz451.rentmanager.utils.BRecordDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.BlackListViewModel
import java.time.format.DateTimeFormatter

class BlackListFragment : Fragment() {

    private val viewModel by viewModels<BlackListViewModel> { getViewModelFactory() }

    private var binding: FragmentConfigBlacklistBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigBlacklistBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = RecordAdapter()
        binding?.recordList?.adapter = adapter
        viewModel.records.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
    }

    inner class RecordVH(
        private val binding: BlistListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(record: BlackListRecord) {
            binding.recordDate.text = record.date.format(DateTimeFormatter.ofPattern("d/MM/YY"))
            binding.recordClientName.text = record.client.name
            binding.recordDescription.text = record.reason
            binding.btnRemove.setOnClickListener {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(R.string.confirm_remove_blacklist)
                    .setPositiveButton(R.string.accept) {dialog, _ ->
                        viewModel.removeRecord(record)
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) {dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.create()
                dialog.show()
            }
            binding.executePendingBindings()
        }
    }

    inner class RecordAdapter : ListAdapter<BlackListRecord, RecyclerView.ViewHolder>(BRecordDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = BlistListItemBinding.inflate(inflater, parent, false)
            return RecordVH(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as RecordVH).bind(getItem(position))
        }
    }
}
