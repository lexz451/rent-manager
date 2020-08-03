package cu.lexz451.rentmanager.utils

import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import java.lang.NumberFormatException

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("android:text")
fun setDouble(view: TextInputEditText, value: Double) {
    if (!value.isNaN()) view.setText(value.toString())
}

@InverseBindingAdapter(attribute = "android:text")
fun getDouble(view: TextInputEditText): Double {
    return try {
        view.text.toString().toDouble()
    } catch (e: NumberFormatException) {
        .0
    }
}

@BindingAdapter("android:text")
fun setInt(view: TextInputEditText, value: Int) {
    view.setText(value.toString())
}

@InverseBindingAdapter(attribute = "android:text")
fun getInt(view: TextInputEditText): Int {
    return try {
        view.text.toString().toInt()
    } catch (e: NumberFormatException) {
        0
    }
}

@BindingAdapter("renderHtml")
fun bindRenderHtml(view: MaterialTextView, description: String?) {
    if (description != null) {
        view.text = HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        view.movementMethod = LinkMovementMethod.getInstance()
    } else {
        view.text = ""
    }
}
