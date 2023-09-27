package ae.network.nicardmanagementsdk.sample

import ae.network.nicardmanagementsdk.api.models.input.*
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ae.network.nicardmanagementsdk.sample.models.EntriesItemModel
import ae.network.nicardmanagementsdk.sample.models.SampleAppFormEntryEnum

class MainViewModel : ViewModel() {

    private var _entriesItemModels: List<EntriesItemModel> = listOf()
    val entriesItemModels: List<EntriesItemModel>
        get() = _entriesItemModels

    private val _entriesItemsLiveData = MutableLiveData<List<EntriesItemModel>>()
    val entriesItemsLiveData: LiveData<List<EntriesItemModel>> = _entriesItemsLiveData

    fun setEntriesItems(searchSelectItems: List<EntriesItemModel>) {
        _entriesItemModels = searchSelectItems
        _entriesItemsLiveData.value = searchSelectItems
    }

    fun getPINLength(): NIPinFormType {
        val name =
            _entriesItemModels.first { model -> model.id == SampleAppFormEntryEnum.PIN_LENGTH }.value
        return NIPinFormType.valueOf(name)
    }

}