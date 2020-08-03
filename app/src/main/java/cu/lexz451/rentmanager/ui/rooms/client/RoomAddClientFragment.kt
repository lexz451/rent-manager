package cu.lexz451.rentmanager.ui.rooms.client

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.form_validation.rule.LengthRule
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.databinding.FragmentRoomAddClientBinding
import cu.lexz451.rentmanager.utils.getViewModelDetailFactory
import cu.lexz451.rentmanager.vm.RoomAddClientViewModel
import kotlinx.android.synthetic.main.fragment_room_add_client.*

class RoomAddClientFragment : Fragment() {

    private val args by navArgs<RoomAddClientFragmentArgs>()
    private val viewModel by viewModels<RoomAddClientViewModel> { getViewModelDetailFactory(args.id) }

    private var binding: FragmentRoomAddClientBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRoomAddClientBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding?.viewModel = viewModel

        binding?.btnSave?.setOnClickListener {
            if (validateForm()) {
                viewModel.addClient()
                findNavController().navigateUp()
            }
        }

        viewModel.blacklistClient.observe(viewLifecycleOwner, Observer {
            it?.let {
                val message =
                        "Usuario en Lista Negra detectado." +
                        "Motivo: ${it.reason}"
                val dialog = MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.warning)
                    .setMessage(message)
                    .setPositiveButton(R.string.cancel) { dialog, _ ->
                        dialog.dismiss()
                        findNavController().navigateUp()
                    }
                    .setCancelable(false)
                dialog.create()
                dialog.show()
            }
        })
    }

    private fun validateForm(): Boolean {
        return FormValidator.getInstance()
            .addField(ci, NonEmptyRule(R.string.no_empty_field), LengthRule(11, R.string.invalid_ci))
            .addField(name, NonEmptyRule(R.string.no_empty_field))
            .addField(companion_ci, NonEmptyRule(R.string.no_empty_field), LengthRule(11, R.string.invalid_ci))
            .addField(companion_name, NonEmptyRule(R.string.no_empty_field))
            .validate()
    }

}