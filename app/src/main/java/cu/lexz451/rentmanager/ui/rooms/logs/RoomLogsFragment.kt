package cu.lexz451.rentmanager.ui.rooms.logs

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.ui.ManagerActivity
import cu.lexz451.rentmanager.utils.ClientDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelDetailFactory
import cu.lexz451.rentmanager.vm.RoomLogsViewModel
import kotlinx.android.synthetic.main.client_list_item.view.*
import kotlinx.android.synthetic.main.fragment_room_logs.*
import kotlinx.android.synthetic.main.product_list_item.view.*
import kotlinx.android.synthetic.main.product_list_item.view.name

class RoomLogsFragment : Fragment() {

    private val args by navArgs<RoomLogsFragmentArgs>()
    private val viewModel by viewModels<RoomLogsViewModel> { getViewModelDetailFactory(args.id) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_logs, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        clientList.adapter = adapter
        viewModel.clients.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.room_logs_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.delete_all_item -> {
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(R.string.confirm_delete_logs)
                    .setPositiveButton(R.string.accept) { dialog, _ ->
                        viewModel.deleteLogs()
                        dialog.dismiss()
                    }
                    .setNegativeButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                    }
                dialog.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showDialog(client: Client) {
        val dialog = ClientLogDialogFragment.getInstance(client)
        dialog.show(childFragmentManager, "dialog")
    }

    inner class ClientViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(client: Client) {
            view.name.text = client.name
            view.ci.text = client.ci
            view.totalPay.text = client.paymentDetails.total.toString()
            view.rootView.setOnClickListener {
                showDialog(client)
            }
        }
    }

    inner class Adapter : ListAdapter<Client, RecyclerView.ViewHolder>(ClientDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.client_list_item, parent, false)
            return ClientViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ClientViewHolder).bind(getItem(position))
        }
    }
}