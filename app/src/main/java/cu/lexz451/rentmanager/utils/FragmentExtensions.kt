package cu.lexz451.rentmanager.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import cu.lexz451.rentmanager.ManagerApp

fun Fragment.getViewModelFactory(): ViewModelFactory {
    return ViewModelFactory(ManagerApp.instance.database, this)
}

fun Fragment.getViewModelDetailFactory(id: Long): ViewModelDetailFactory {
    return ViewModelDetailFactory(id, ManagerApp.instance.database, this)
}

fun Fragment.showKeyboard(show: Boolean) {
    val inputManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken,
        if (show) InputMethodManager.SHOW_FORCED else InputMethodManager.HIDE_NOT_ALWAYS)
}