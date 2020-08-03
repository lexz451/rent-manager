package cu.lexz451.rentmanager.ui.settings.shifts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.github.dhaval2404.form_validation.rule.NonEmptyRule
import com.github.dhaval2404.form_validation.validation.FormValidator

import cu.lexz451.rentmanager.R
import cu.lexz451.rentmanager.databinding.FragmentConfigShiftDetailBinding
import cu.lexz451.rentmanager.utils.getViewModelDetailFactory
import cu.lexz451.rentmanager.utils.showKeyboard
import cu.lexz451.rentmanager.vm.ShiftDetailViewModel


class ShiftDetailFragment : Fragment() {

    private val args by navArgs<ShiftDetailFragmentArgs>()
    private val viewModel by viewModels<ShiftDetailViewModel> { getViewModelDetailFactory(args.id) }

    private var binding: FragmentConfigShiftDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConfigShiftDetailBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.shift.observe(viewLifecycleOwner, Observer {
            binding?.shift = it
            if (it.__id != 0L) {
                binding?.btnRemove?.visibility = View.VISIBLE
            }
        })
        binding?.setOnSave {
            if (isFormValid()) {
                viewModel.save()
                findNavController().navigateUp()
                showKeyboard(false)
            }
        }
        binding?.setOnRemove {
            viewModel.remove()
            findNavController().navigateUp()
            showKeyboard(false)
        }
    }

    private fun isFormValid(): Boolean {
        return FormValidator.getInstance()
            .addField(binding?.employeeName!!, NonEmptyRule(R.string.no_empty_field))
            .validate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
