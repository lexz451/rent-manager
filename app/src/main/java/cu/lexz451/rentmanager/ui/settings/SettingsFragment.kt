package cu.lexz451.rentmanager.ui.settings

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cu.lexz451.rentmanager.ManagerApp
import cu.lexz451.rentmanager.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        findPreference<Preference>("shifts")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections
                .actionSettingsFragmentToShiftListFragment())
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("rooms")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections
                .actionSettingsFragmentToConfigRoomList())
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("products")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections
                .actionSettingsFragmentToProductListFragment())
            return@setOnPreferenceClickListener true
        }

        findPreference<Preference>("base_prices")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections
                .actionSettingsFragmentToPriceListFragment())
            return@setOnPreferenceClickListener true
        }

        findPreference<EditTextPreference>("extra_room_price")?.setOnBindEditTextListener {
            it.inputType = InputType.TYPE_CLASS_NUMBER
        }

        findPreference<Preference>("blacklist")?.setOnPreferenceClickListener {
            findNavController().navigate(SettingsFragmentDirections
                .actionSettingsFragmentToBlackListFragment())
            return@setOnPreferenceClickListener true
        }
    }
}
