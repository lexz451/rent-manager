package cu.lexz451.rentmanager.ui.rooms

import android.content.Context
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.ManagerApp

import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.data.model.Shift
import cu.lexz451.rentmanager.databinding.RoomListItemBinding
import cu.lexz451.rentmanager.utils.RoomDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.RoomListViewModel
import kotlinx.android.synthetic.main.fragment_config_shift_detail.view.*
import kotlinx.android.synthetic.main.fragment_room_list.*
import kotlinx.android.synthetic.main.shift_spinner_dropdown_item.view.*
import kotlinx.android.synthetic.main.shift_spinner_item.view.*
import okhttp3.internal.format
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RoomListFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val viewModel by viewModels<RoomListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        roomList.adapter = adapter
        viewModel.rooms.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        viewModel.shifts.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                room_list_container.visibility = View.GONE
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.no_shift_defined)
                    .setPositiveButton(R.string.accept) { _, _ ->
                        ///
                    }
                dialog.create()
                dialog.show()
            } else {
                room_list_container.visibility = View.VISIBLE
                setupSpinner(it)
            }
        })
        viewModel.record.observe(viewLifecycleOwner, Observer {record ->
            if (record != null) {
                vip_products_total.text = record.products.sumBy { p -> p.quantity }.toString()
                vip_pay_total.text = record.products.sumByDouble { p -> (p.quantity * p.price) }.toString()
            } else {
                vip_products_total.text = 0.toString()
                vip_pay_total.text = .0.toString()
            }
        })
        vip.setOnClickListener {
            findNavController()
                .navigate(RoomListFragmentDirections.actionRoomListFragmentToVipFragment())
        }
    }

    private fun setupSpinner(items: MutableList<Shift>) {
        val adapter = SpinnerAdapter(requireContext(), items)
        shiftSelector.adapter = adapter
        shiftSelector.onItemSelectedListener = this
        val selected = ManagerApp.instance.settings.currentShift
        val selection = items.find { it.__id == selected }
        if (selection != null) {
            shiftSelector.setSelection(adapter.getPosition(selection))
        } else {
            shiftSelector.setSelection(0)
        }
    }

    inner class RoomViewHolder(
        private val binding: RoomListItemBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.room = room
            room.client?.let {
                binding.exitTime.text = it.checkInDate.toLocalTime().plusHours(3).format(
                    DateTimeFormatter.ofPattern("h:mm a"))
            }
            binding.setOnItemSelected {
                findNavController().navigate(RoomListFragmentDirections
                    .actionRoomListFragmentToRoomDetailFragment(room.__id))
            }
            binding.executePendingBindings()
        }
    }

    inner class Adapter : ListAdapter<Room, RoomViewHolder>(RoomDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding =
                RoomListItemBinding.inflate(inflater, parent, false)
            return RoomViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
            holder.bind(getItem(position))
        }
    }

    inner class SpinnerAdapter(context: Context,
                               private val items: MutableList<Shift>)
        : ArrayAdapter<Shift>(context, 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listItem = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.shift_spinner_item, parent, false)
            val item = items[position]
            listItem.employee.text = item.employee
            return listItem
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listItem = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.shift_spinner_dropdown_item, parent, false)
            val item = items[position]
            listItem.employeeDropdown.text = item.employee
            return listItem
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val current = ManagerApp.instance.settings.currentShift
        val shift = parent?.getItemAtPosition(position) as Shift
        if (shift.__id == current) return
        shift.date = LocalDateTime.now()
        viewModel.updateShift(shift)
        Log.d("RentManager","Change shift to: $shift")
    }
}
