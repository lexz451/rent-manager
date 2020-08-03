package cu.lexz451.rentmanager.ui.settings.prices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Price
import cu.lexz451.rentmanager.databinding.FragmentPriceDialogBinding

class PriceDetailDialog private constructor(
    private val price: Price
) : DialogFragment() {

    private var binding: FragmentPriceDialogBinding? = null

    var listener: OnDialogResultListener? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPriceDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.price = price
        binding?.setOnCancel {
            dismiss()
        }
        binding?.setOnSave {
            if (isFormValid()) {
                listener?.onDialogResult(binding?.price ?: price)
                dismiss()
            }
        }
    }

    private fun isFormValid(): Boolean {
        return FormValidator.getInstance()
            .addField(binding!!.nameInput, NonEmptyRule(R.string.no_empty_field))
            .addField(binding!!.priceInput, NonEmptyRule(R.string.no_empty_field))
            .validate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun getInstance(price: Price): PriceDetailDialog {
            return PriceDetailDialog(price)
        }
    }

    interface OnDialogResultListener {
        fun onDialogResult(price: Price)
    }
}