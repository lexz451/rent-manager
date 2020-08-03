package cu.lexz451.rentmanager.ui.rooms

import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.databinding.ProductListItemBinding
import cu.lexz451.rentmanager.ui.dialog.ProductListDialogFragment
import cu.lexz451.rentmanager.utils.*
import cu.lexz451.rentmanager.vm.RoomDetailViewModel
import kotlinx.android.synthetic.main.fragment_room_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.timerTask
import kotlin.math.abs


class RoomDetailFragment : Fragment() {


    private val args by navArgs<RoomDetailFragmentArgs>()
    private val viewModel by viewModels<RoomDetailViewModel> { getViewModelDetailFactory(args.id) }

    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.room.observe(viewLifecycleOwner, Observer { room ->
            val client = room.client
            val adapter = Adapter()
            productList.adapter = adapter
            if (client == null) {
                clientRegister.visibility = View.VISIBLE
                clientInfo.visibility = View.GONE
                clientProducts.visibility = View.GONE
                btnPay.visibility = View.GONE
                btnRegister.setOnClickListener {
                    findNavController().navigate(RoomDetailFragmentDirections
                        .actionRoomDetailFragmentToRoomAddClientFragment(room.__id))
                }
                AlarmScheduler.cancelAlarm(requireContext(), room.__id.toInt())
            } else {
                clientRegister.visibility = View.GONE
                clientInfo.visibility = View.VISIBLE
                btnPay.visibility = View.VISIBLE
                clientProducts.visibility = View.VISIBLE
                btnPay.setOnClickListener {
                    findNavController().navigate(RoomDetailFragmentDirections
                        .actionRoomDetailFragmentToRoomPaymentFragment(client))
                }
                clientId.text = client.ci
                clientName.text = client.name
                checkInHour.text = client.checkInDate
                    .toLocalTime().format(DateTimeFormatter.ofPattern("h:mm a"))
                adapter.submitList(client.products)
                adapter.notifyDataSetChanged()
                btnAddProduct.setOnClickListener {
                    val dialog = ProductListDialogFragment
                        .getInstance(room.__id)
                    dialog.listener = object : ProductListDialogFragment.OnItemSelectedListener {
                        override fun onItemSelected(product: Product) {
                            if (client.products.any { p -> p.__id == product.__id }) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.product_already_added),
                                    Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.addClientProduct(product)
                            }
                        }
                    }
                    dialog.show(childFragmentManager, "dialog")
                }
                timer = Timer()
                timer?.schedule(timerTask {
                    val checkIn = client.checkInDate
                    val now = LocalTime.now()
                    val duration = Duration.between(checkIn.toLocalTime(), now)
                    timingHours?.post {
                        timingHours?.text = formatDuration(duration) ?: "-"
                    }
                }, 0, 1000)

                val remindCheckOut = client.checkInDate.plusHours(3).minusMinutes(15)

                val alarmTitle = "Aviso de Tiempo: Habitación ${room.tag}"
                val alarmMessage = "El tiempo de habitación para el cliente: ${client.name} esta al terminar"

                AlarmScheduler.scheduleAlarmForReminder(
                    requireContext(),
                    room.__id.toInt(),
                    alarmTitle,
                    alarmMessage,
                    remindCheckOut.hour,
                    remindCheckOut.minute
                )
            }

            btnInventory.setOnClickListener {
                findNavController().navigate(RoomDetailFragmentDirections
                    .actionRoomDetailFragmentToRoomInventoryFragment(room.__id))
            }
            btnHistory.setOnClickListener {
                findNavController().navigate(RoomDetailFragmentDirections
                    .actionRoomDetailFragmentToRoomLogsFragment(room.__id))
            }

            roomInfo.visibility = View.VISIBLE
        })
    }


    private fun formatDuration(duration: Duration): String? {
        val seconds = duration.seconds
        val absSeconds = abs(seconds)
        val positive = String.format(
            "%d:%02d:%02d",
            absSeconds / 3600,
            absSeconds % 3600 / 60,
            absSeconds % 60
        )
        return if (seconds < 0) "-$positive" else positive
    }

    override fun onDestroyView() {
        timer?.cancel()
        timer?.purge()
        timer = null
        super.onDestroyView()
    }

    inner class ClientProductViewHolder(
        private val binding: ProductListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.name.text = product.name
            binding.quantity.text = product.quantity.toString()
            binding.btnRemove.setOnClickListener {
                viewModel.removeClientProduct(product)
            }
            binding.executePendingBindings()
        }
    }

    inner class Adapter : ListAdapter<Product, RecyclerView.ViewHolder>(ProductDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ProductListItemBinding.inflate(inflater, parent, false)
            return ClientProductViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ClientProductViewHolder).bind(getItem(position))
        }
    }
}