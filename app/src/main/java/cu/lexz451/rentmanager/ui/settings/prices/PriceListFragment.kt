package cu.lexz451.rentmanager.ui.settings.prices

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
import cu.lexz451.rentmanager.data.model.Price
import cu.lexz451.rentmanager.utils.PriceDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.PriceListViewModel
import kotlinx.android.synthetic.main.fragment_config_price_list.*
import kotlinx.android.synthetic.main.price_list_item.view.*

class PriceListFragment : Fragment() {

    private val viewModel by viewModels<PriceListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config_price_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        priceList.adapter = adapter
        viewModel.prices.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        })
        btnAdd.setOnClickListener {
            showDialog(Price(0, "", .0))
        }
    }

    private fun showDialog(price: Price) {
        val dialog = PriceDetailDialog.getInstance(price)
        dialog.listener = object : PriceDetailDialog.OnDialogResultListener {
            override fun onDialogResult(price: Price) {
                viewModel.updatePrice(price)
            }
        }
        dialog.show(childFragmentManager, "dialog")
    }

    inner class PriceViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bind(price: Price) {
            view.name.text = price.name
            view.price.text = price.price.toString()
            view.rootView.setOnClickListener {
                showDialog(price)
            }
        }
    }

    inner class Adapter : ListAdapter<Price, RecyclerView.ViewHolder>(PriceDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.price_list_item, parent, false)
            return PriceViewHolder(view)
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as PriceViewHolder).bind(getItem(position))
        }
    }
}