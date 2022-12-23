package ae.network.nicardmanagementsdk.presentation.ui.base_class

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


abstract class BaseViewModel: ViewModel() {
    val isVisibleProgressBar = MutableLiveData(false)
}