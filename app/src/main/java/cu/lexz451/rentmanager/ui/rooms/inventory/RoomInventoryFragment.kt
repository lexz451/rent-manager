package cu.lexz451.rentmanager.ui.rooms.inventory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.databinding.ProductListItemBinding
import cu.lexz451.rentmanager.ui.dialog.ProductListDialogFragment
import cu.lexz451.rentmanager.utils.ProductDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelDetailFactory
import cu.lexz451.rentmanager.vm.RoomInventoryViewModel
import kotlinx.android.synthetic.main.fragment_room_inventory.*

class RoomInventoryFragment : Fragment() {

    private val args by navArgs<RoomInventoryFragmentArgs>()
    private val viewModel by viewModels<RoomInventoryViewModel> { getViewModelDetailFactory(args.id) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_room_inventory, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        productList.adapter = adapter
        viewModel.products.observe(viewLifecycleOwner, Observer { products ->
            adapter.submitList(products)
            adapter.notifyDataSetChanged()
            btnAdd.setOnClickListener {
                val dialog = ProductListDialogFragment.getInstance()
                dialog.listener = object : ProductListDialogFragment.OnItemSelectedListener {
                    override fun onItemSelected(product: Product) {
                        if (products.any { p -> p.__id == product.__id }) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.product_already_added),
                                Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addRoomProduct(product)
                        }
                    }
                }
                dialog.show(childFragmentManager, "dialog")
            }
        })
    }

    inner class RoomProductViewHolder(
        private val binding: ProductListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.name.text = product.name
            binding.quantity.text = product.quantity.toString()
            binding.btnRemove.setOnClickListener {
                viewModel.removeRoomProduct(product)
            }
        }
    }

    inner class Adapter : ListAdapter<Product, RecyclerView.ViewHolder>(ProductDiffCallback()) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ProductListItemBinding.inflate(inflater, parent, false)
            return RoomProductViewHolder(binding)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as RoomProductViewHolder).bind(getItem(position))
        }
    }
}