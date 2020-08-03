package cu.lexz451.rentmanager.ui.rooms.payment.blacklist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.BlackListRecord
import cu.lexz451.rentmanager.databinding.FragmentBlacklistClientDialogBinding

class BlackListClientDialog private constructor(
    private val record: BlackListRecord
): DialogFragment() {

    private var binding: FragmentBlacklistClientDialogBinding? = null

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
        binding = FragmentBlacklistClientDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.record = record
        binding?.btnAdd?.setOnClickListener {
            val isValid = FormValidator.getInstance()
                .addField(binding!!.reasonInput, NonEmptyRule(R.string.no_empty_field))
                .validate()
            if (isValid) {
                listener?.onDialogResult(binding!!.record!!)
                dismiss()
            }
        }
        binding?.btnCancel?.setOnClickListener {
            dismiss()
        }
    }

    interface OnDialogResultListener {
        fun onDialogResult(record: BlackListRecord)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun getInstance(record: BlackListRecord): BlackListClientDialog {
            return BlackListClientDialog(record)
        }
    }
}