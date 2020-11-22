package cu.lexz451.rentmanager.ui.rooms.logs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.data.model.Client
import cu.lexz451.rentmanager.databinding.FragmentClientLogDialogBinding

class ClientLogDialogFragment private constructor(
    private val client: Client): DialogFragment() {

    private var binding: FragmentClientLogDialogBinding? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentClientLogDialogBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding?.html = client.generateHTMLReport()
        binding?.btnAccept?.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    companion object {
        fun getInstance(client: Client): ClientLogDialogFragment {
            return ClientLogDialogFragment(client)
        }
    }
}