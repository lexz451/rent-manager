package cu.lexz451.rentmanager.ui.rooms.payment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.form_validation.rule.BaseRule
import com.github.dhaval2404.form_validation.rule.EqualRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.rule.NumberRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.*
import cu.lexz451.rentmanager.databinding.FragmentRoomPaymentBinding
import cu.lexz451.rentmanager.ui.rooms.payment.blacklist.BlackListClientDialog
import cu.lexz451.rentmanager.utils.AlarmScheduler
import cu.lexz451.rentmanager.utils.findById
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.RoomPaymentViewModel
import kotlinx.android.synthetic.main.fragment_room_payment.*
import kotlinx.android.synthetic.main.price_spinner_dropdown_item.view.*
import kotlinx.android.synthetic.main.price_spinner_item.view.*
import java.time.LocalDate

class RoomPaymentFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private val args by navArgs<RoomPaymentFragmentArgs>()
    private val viewModel by viewModels<RoomPaymentViewModel> { getViewModelFactory() }

    private var binding: FragmentRoomPaymentBinding? = null

    private lateinit var client: Client

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomPaymentBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        client = args.client
        viewModel.prices.observe(viewLifecycleOwner, Observer { prices ->
            if (prices.isEmpty()) {
                binding?.roomPaymentContainer?.visibility = View.GONE
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setCancelable(false)
                    .setTitle(R.string.warning)
                    .setMessage(R.string.no_price_defined)
                    .setPositiveButton(R.string.accept) { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                dialog.create()
                dialog.show()
            } else {
                binding?.roomPaymentContainer?.visibility = View.VISIBLE
                val adapter = SpinnerAdapter(requireContext(), prices.toMutableList())
                priceSelector.adapter = adapter
                priceSelector.onItemSelectedListener = this
                val selected = ManagerApp.instance.settings.currentRoomPrice
                for (price in prices) {
                    if (price.__id == selected) {
                        priceSelector.setSelection(adapter.getPosition(price))
                    } else {
                        priceSelector.setSelection(0)
                    }
                }
                extra_hours.addTextChangedListener {
                    onChange(client)
                }
                paid_input.addTextChangedListener {
                    onChange(client)
                }

                binding?.btnPayDone?.setOnClickListener {
                    if (isFormValid()) {
                        val dialog = MaterialAlertDialogBuilder(requireContext())
                            .setTitle(R.string.warning)
                            .setMessage(R.string.confirm_close_account)
                            .setPositiveButton(R.string.accept) { dialog, _ ->
                                viewModel.processClient(client)
                                dialog.dismiss()
                                findNavController().navigateUp()
                            }
                            .setNegativeButton(R.string.cancel) { dialog, _ ->
                                dialog.dismiss()
                            }
                        dialog.create()
                        dialog.show()
                    }
                }
                binding?.btnBlackList?.setOnClickListener {
                    if (isFormValid()) {
                        val newRecord = BlackListRecord(0, client, "", LocalDate.now())
                        val dialog = BlackListClientDialog.getInstance(newRecord)
                        dialog.listener = object : BlackListClientDialog.OnDialogResultListener {
                            override fun onDialogResult(record: BlackListRecord) {
                                viewModel.processClientAndBlackList(record)
                                dialog.dismiss()
                                findNavController().navigateUp()
                            }
                        }
                        dialog.show(childFragmentManager, "dialog")
                    }
                }
                onChange(client)
            }
        })
    }

    private fun isFormValid(): Boolean {
        if (paid_input_input.error != null) return false
        return FormValidator.getInstance()
            .addField(extra_hours_input, NonEmptyRule(R.string.no_empty_field))
            .addField(paid_input_input, NonEmptyRule(R.string.no_empty_field))
            .validate()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun onChange(client: Client) {
        val price = priceSelector.selectedItem as Price
        val extra = if (extra_hours.text.isNullOrEmpty()) 0 else extra_hours.text.toString().toInt()
        val paid = if (paid_input.text.isNullOrEmpty()) .0 else paid_input.text.toString().toDouble()
        if (paid < client.paymentDetails.total) {
            paid_input_input.error = "Importe insuficiente"
        } else {
            paid_input_input.error = null
        }
        calculatePayment(client, extra, price.price, paid)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val current = ManagerApp.instance.settings.currentRoomPrice
        val price = parent?.getItemAtPosition(position) as Price
        if (price.__id == current) return
        ManagerApp.instance.settings.currentRoomPrice = price.__id
        onChange(client)
    }

    private fun calculatePayment(
        client: Client,
        extra: Int,
        price: Double,
        paid: Double) {

        var forRoom = price
        forRoom += extra * ManagerApp.instance.settings.extraRoomPrice

        val forProducts = client.products.sumByDouble { it.quantity * it.price }

        val total = forRoom + forProducts

        var paidReturn = paid - total
        if (paidReturn < 0) {
            paidReturn = .0
        }

        val currentShift = ManagerApp.instance.database.shiftStore.findById(
            ManagerApp.instance.settings.currentShift
        ).first()

        client.paymentDetails = PaymentDetails(forRoom, forProducts, total, paid, paidReturn, extra, currentShift.__id)
        binding?.paymentReport = client.generateHTMLReport()
    }

    inner class SpinnerAdapter(context: Context,
                               private val items: MutableList<Price>)
        : ArrayAdapter<Price>(context, 0, items) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listItem = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.price_spinner_item, parent, false)
            val item = items[position]
            listItem.priceName.text = item.name
            return listItem
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val listItem = convertView ?: LayoutInflater.from(parent.context)
                .inflate(R.layout.price_spinner_dropdown_item, parent, false)
            val item = items[position]
            listItem.priceDropdown.text = item.name
            return listItem
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}