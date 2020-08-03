package cu.lexz451.rentmanager.ui.rooms.vip

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
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.ui.dialog.ProductListDialogFragment
import cu.lexz451.rentmanager.utils.ProductDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.VipViewModel
import kotlinx.android.synthetic.main.fragment_vip.*
import kotlinx.android.synthetic.main.product_list_item.view.*
import kotlinx.android.synthetic.main.product_list_item.view.btnRemove
import kotlinx.android.synthetic.main.product_list_item.view.name
import kotlinx.android.synthetic.main.product_list_item.view.quantity
import kotlinx.android.synthetic.main.product_vip_list_item.view.*

class VipFragment : Fragment() {

    private val viewModel by viewModels<VipViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_vip, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        productList.adapter = adapter
        btnAdd.setOnClickListener {
            val dialog = ProductListDialogFragment.getInstance()
            dialog.listener = object : ProductListDialogFragment.OnItemSelectedListener {
                override fun onItemSelected(product: Product) {
                    viewModel.add(product)
                }
            }
            dialog.show(childFragmentManager, "dialog")
        }
        viewModel.record.observe(viewLifecycleOwner, Observer { record ->
            adapter.submitList(record.products)
            adapter.notifyDataSetChanged()
        })
    }

    inner class ProductViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {

        fun bind(product: Product) {
            view.name.text = product.name
            view.quantity.text = product.quantity.toString()
            view.toPay.text = (product.quantity * product.price).toString()
            view.btnRemove.setOnClickListener {
                viewModel.remove(product)
            }
        }
    }

    inner class Adapter : ListAdapter<Product, RecyclerView.ViewHolder>(ProductDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.product_vip_list_item, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ProductViewHolder).bind(getItem(position))
        }

    }
}