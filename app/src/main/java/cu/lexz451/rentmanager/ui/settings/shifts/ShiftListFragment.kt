package cu.lexz451.rentmanager.ui.settings.shifts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Shift
import cu.lexz451.rentmanager.utils.ShiftDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.ShiftListViewModel
import kotlinx.android.synthetic.main.fragment_config_shift_list.*
import kotlinx.android.synthetic.main.config_shift_list_item.view.*
import java.time.format.DateTimeFormatter


class ShiftListFragment : Fragment() {

    private val viewModel by viewModels<ShiftListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config_shift_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        shiftList.adapter = adapter

        viewModel.shifts.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        btnNew.setOnClickListener {
            val direction = ShiftListFragmentDirections
                .actionShiftListFragmentToShiftDetailFragment()
            findNavController().navigate(direction)
        }
    }

    inner class ShiftViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(item: Shift) {
            view.name.text = item.employee
            view.date.text = item.date.format(DateTimeFormatter.ofPattern("dd/MM/YYYY"))
            view.rootView.setOnClickListener {
                val direction = ShiftListFragmentDirections
                    .actionShiftListFragmentToShiftDetailFragment(item.__id)
                findNavController().navigate(direction)
            }
        }

    }

    inner class Adapter : ListAdapter<Shift, ShiftViewHolder>(ShiftDiffCallback()) {

        override fun onBindViewHolder(holder: ShiftViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShiftViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.config_shift_list_item, parent, false)
            return ShiftViewHolder(view)
        }
    }
}
