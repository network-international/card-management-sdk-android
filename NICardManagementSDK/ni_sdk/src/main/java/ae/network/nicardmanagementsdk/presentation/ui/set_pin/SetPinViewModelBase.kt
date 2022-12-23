package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.api.interfaces.SuccessErrorResponse
import ae.network.nicardmanagementsdk.presentation.components.SingleLiveEvent
import ae.network.nicardmanagementsdk.presentation.models.PinBulletModel
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations

abstract class SetPinViewModelBase : BaseViewModel() {

    abstract val navTitle: LiveData<Int>
    abstract val screenTitle: LiveData<Int>

    private var _inputString = ""
    val inputString: String
        get() = _inputString

    private val _inputStringLiveData = MutableLiveData(_inputString)
    private var minPinSize = 4
    private var maxPinSize = 6

    val updateBulletCellLiveEvent = SingleLiveEvent<Int>()
    val onResultSingleLiveEvent = SingleLiveEvent<SuccessErrorResponse>()

    private var _bulletItemModels: List<PinBulletModel> = listOf()
    val bulletItemModels: List<PinBulletModel>
        get() = _bulletItemModels

    private val _bulletItemsLiveData = MutableLiveData<List<PinBulletModel>>()
    val bulletItemsLiveData: LiveData<List<PinBulletModel>> = _bulletItemsLiveData

    val validationSendButton : LiveData<Boolean> = Transformations.map(_inputStringLiveData) {
        when (it.length) {
            in minPinSize..maxPinSize -> true
            else ->  false
        }
    }



    private fun setBulletItems(bulletItems: List<PinBulletModel>) {
        _bulletItemModels = bulletItems
        _bulletItemsLiveData.value = bulletItems
    }

    fun onNumberKeyTap(value: Int) {
        if (_inputString.length < maxPinSize) {
            addString(value)
            updateBulletStateAdd()
        }
    }

    abstract fun onDoneImageButtonTap()

    fun onBackspaceImageButtonTap() {
        if (_inputString.isNotEmpty()) {
            removeString()
            updateBulletStateRemove()
        }
    }

    private fun addString(value: Int) {
        val builder = StringBuilder(_inputString)
        builder.append(value)
        updateInputString(builder)
    }

    private fun removeString() {
        val builder = StringBuilder(_inputString)
        builder.deleteCharAt(_inputString.lastIndex)
        updateInputString(builder)
    }

    private fun updateInputString(builder: StringBuilder) {
        _inputString = builder.toString()
        _inputStringLiveData.value = _inputString
    }

    private fun updateBulletStateRemove() {
        val index = _inputString.length
        updateBulletState(index, false)
    }

    private fun updateBulletStateAdd() {
        val index = _inputString.length - 1
        updateBulletState(index, true)
    }

    private fun updateBulletState(index: Int, value: Boolean) {
        _bulletItemModels[index].checked = value
        updateBulletCellLiveEvent.value = index
    }

    fun generateInitialState(minPinSize: Int, maxPinSize: Int) {
        this.minPinSize = minPinSize
        this.maxPinSize = maxPinSize
        val bullets: List<PinBulletModel> = (1..maxPinSize).map { PinBulletModel() }
        setBulletItems(bullets)
    }

    fun resetState() {
        generateInitialState(minPinSize, maxPinSize)
        _inputString = ""
        _inputStringLiveData.value = _inputString
    }

}