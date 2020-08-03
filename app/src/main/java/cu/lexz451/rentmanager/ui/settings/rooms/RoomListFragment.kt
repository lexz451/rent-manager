package cu.lexz451.rentmanager.ui.settings.rooms

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Room
import cu.lexz451.rentmanager.databinding.ConfigRoomListItemBinding
import cu.lexz451.rentmanager.utils.RoomDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.RoomListViewModel
import kotlinx.android.synthetic.main.fragment_config_room_list.*


class RoomListFragment : Fragment() {

    private val viewModel by viewModels<RoomListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config_room_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        adapter.listener = object : OnItemRemoveListener {
            override fun onRemove(room: Room) {
                viewModel.remove(room)
            }
        }
        btnNew.setOnClickListener {
            viewModel.add()
        }
        roomList.adapter = adapter
        viewModel.rooms.observe(viewLifecycleOwner, Observer { adapter.submitList(it) })
    }

    inner class RoomViewHolder(
        private val binding: ConfigRoomListItemBinding,
        private val listener: OnItemRemoveListener?) : RecyclerView.ViewHolder(binding.root) {
        fun bind(room: Room) {
            binding.tag = room.tag
            binding.setOnRemove { listener?.onRemove(room) }
            binding.executePendingBindings()
        }
    }

    inner class Adapter : ListAdapter<Room, RecyclerView.ViewHolder>(RoomDiffCallback()) {

        var listener: OnItemRemoveListener? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding =
                ConfigRoomListItemBinding.inflate(inflater, parent, false)
            return RoomViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as RoomViewHolder).bind(getItem(position))
        }
    }

    interface OnItemRemoveListener {
        fun onRemove(room: Room)
    }
}


