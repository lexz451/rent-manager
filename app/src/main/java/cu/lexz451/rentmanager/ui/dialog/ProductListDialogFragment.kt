package cu.lexz451.rentmanager.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Product
import cu.lexz451.rentmanager.utils.ProductDiffCallback
import cu.lexz451.rentmanager.utils.getViewModelDetailFactory
import cu.lexz451.rentmanager.vm.ProductListDialogViewModel
import kotlinx.android.synthetic.main.fragment_product_list_dialog.*
import kotlinx.android.synthetic.main.fragment_product_list_dialog_item.view.*

/**
 * Load a selectable list of Products of a particular Room or the General Inventory
 */
class ProductListDialogFragment : BottomSheetDialogFragment() {

    var listener: OnItemSelectedListener? = null

    private val viewModel by viewModels<ProductListDialogViewModel> {
        getViewModelDetailFactory(requireArguments().getLong("roomId"))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_product_list_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = Adapter()
        productList.adapter = adapter
        viewModel.products.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    companion object {
        fun getInstance(roomId: Long = 0): ProductListDialogFragment {
            val fragment = ProductListDialogFragment()
            fragment.arguments = Bundle().apply {
                putLong("roomId", roomId)
            }
            return fragment
        }
    }

    inner class ProductViewHolder(
        private val view: View
    ) : RecyclerView.ViewHolder(view) {
        fun bind(product: Product) {
            view.name.text = product.name

            view.quantityPicker.run {
                wrapSelectorWheel = true
                if (product.quantity > 0) {
                    minValue = 1
                    maxValue = product.quantity
                    isEnabled = true
                    view.btnAdd.isEnabled = true
                } else {
                    minValue = 0
                    maxValue = 0
                    isEnabled = false
                    view.btnAdd.isEnabled = false
                }
            }

            view.btnAdd.setOnClickListener {
                val copy = product.copy()
                copy.quantity = view.quantityPicker.value
                listener?.onItemSelected(copy)
                dismiss()
            }
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(product: Product)
    }

    inner class Adapter : ListAdapter<Product, RecyclerView.ViewHolder>(ProductDiffCallback()) {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(R.layout.fragment_product_list_dialog_item, parent, false)
            return ProductViewHolder(view)
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            (holder as ProductViewHolder).bind(getItem(position))
        }
    }
}