package ae.network.nicardmanagementsdk.presentation.ui.set_pin

import ae.network.nicardmanagementsdk.R
import ae.network.nicardmanagementsdk.api.models.input.NIPinFormType
import ae.network.nicardmanagementsdk.databinding.ActivitySetPinBinding
import ae.network.nicardmanagementsdk.presentation.adapters.BulletListAdapter
import ae.network.nicardmanagementsdk.presentation.extension_methods.getSerializableExtraCompat
import ae.network.nicardmanagementsdk.presentation.models.Extra
import ae.network.nicardmanagementsdk.presentation.ui.base_class.BaseActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager

abstract class SetPinActivityBase<T : SetPinViewModelBase> : BaseActivity<T>() {

    protected lateinit var binding: ActivitySetPinBinding
    abstract override var viewModel: T
    private lateinit var niPinFormType: NIPinFormType


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.getSerializableExtraCompat(Extra.EXTRA_NI_PIN_FORM_TYPE, NIPinFormType::class.java)?.let {
            niPinFormType = it
        } ?: throw RuntimeException("${this::class.java.simpleName} intent serializable ${Extra.EXTRA_NI_PIN_FORM_TYPE} is missing")

        setArchitectureComponents()
    }

    private fun setArchitectureComponents() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_pin)
    }

    protected open fun initializeUI() {
        binding.apply {
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = BulletListAdapter()
                setHasFixedSize(true)
                itemAnimator?.changeDuration = 0

                this@SetPinActivityBase.viewModel.bulletItemsLiveData.observe(this@SetPinActivityBase) { itemModels ->
                    itemModels?.let {
                        (adapter as BulletListAdapter).setItems(it)
                    }
                }

                this@SetPinActivityBase.viewModel.updateBulletCellLiveEvent.observe(this@SetPinActivityBase) { index ->
                    index?.let {
                        (adapter as BulletListAdapter).notifyUpdate(it)
                    }
                }
            }
        }
    }

    protected fun setViewModelData() {
        if (viewModel.bulletItemModels.isEmpty()) {
            viewModel.generateInitialState(niPinFormType.minSize, niPinFormType.maxSize)
        }
    }
}