package cu.lexz451.rentmanager.ui.settings.products

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.utils.ProductDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelFactory
import cu.lexz451.rentmanager.vm.ProductListViewModel
import kotlinx.android.synthetic.main.config_product_list_item.view.*
import kotlinx.android.synthetic.main.fragment_config_product_list.*

class ProductListFragment : Fragment() {

    private val viewModel by viewModels<ProductListViewModel> { getViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_config_product_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        productList.adapter = adapter
        viewModel.products.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
        btnNew.setOnClickListener {
            findNavController()
                .navigate(ProductListFragmentDirections.
                    actionProductListFragmentToProductDetailFragment())
        }
    }

    inner class ProductViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product) {
            view.name.text = product.name
            view.price.text = product.price.toString()
            view.quantity.text = product.quantity.toString()
            view.rootView.setOnClickListener {
                view.findNavController()
                    .navigate(ProductListFragmentDirections
                        .actionProductListFragmentToProductDetailFragment(product.__id))
            }
        }
    }

    inner class Adapter : ListAdapter<Product, RecyclerView.ViewHolder>(ProductDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.config_product_list_item, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ProductViewHolder).bind(getItem(position))
        }
    }
}