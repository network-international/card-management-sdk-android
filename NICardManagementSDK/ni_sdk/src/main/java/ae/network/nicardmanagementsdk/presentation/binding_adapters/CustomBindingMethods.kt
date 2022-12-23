package ae.network.nicardmanagementsdk.presentation.binding_adapters

import android.widget.ImageView
import androidx.databinding.BindingMethod
import androidx.databinding.BindingMethods

@BindingMethods(value = [
    BindingMethod(type = ImageView::class, attribute = "srcCompat", method = "setImageDrawable")
])

class CustomBindingMethods