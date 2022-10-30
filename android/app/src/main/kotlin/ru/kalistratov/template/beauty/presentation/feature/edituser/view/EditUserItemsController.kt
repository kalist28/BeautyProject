package ru.kalistratov.template.beauty.presentation.feature.edituser.view

import android.view.View
import android.view.ViewParent
import android.widget.Button
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.entity.ViewListItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

class EditUserItemsController : EpoxyController() {

    var items: List<ViewListItem> = emptyList()
    var itemsData: List<EditUserData> = emptyList()

    val buttonClicks = mutableSharedFlow<EditUserListItemType>()
    val dataUpdates = mutableSharedFlow<EditUserData>()

    var allowSaveChanges: Boolean = false

    override fun buildModels() {
        items.forEach { item ->
            val itemType = item.type
            if (itemType !is EditUserListItemType) return@forEach
            when (item) {
                is ViewListItem.Button -> createButtonItemModel(item, itemType)
                is ViewListItem.EditText -> createEditTextItemModel(item, itemType)
                else -> null
            }?.addTo(this)
        }
    }

    private fun createButtonItemModel(
        item: ViewListItem.Button,
        itemType: EditUserListItemType
    ) = ButtonItemModel(
        item.titleId,
        when (itemType != EditUserListItemType.ChangePasswordButton) {
            true -> allowSaveChanges
            false -> false
        }
    ) { buttonClicks.tryEmit(itemType) }

    private fun createEditTextItemModel(
        item: ViewListItem.EditText,
        itemType: EditUserListItemType
    ) = EditTextItemModel(
        item.titleId,
        itemsData.find { it.type == itemType }?.value,
        when (itemType) {
            EditUserListItemType.Email -> false
            else -> true
        },
        textUpdatedAction = {
            dataUpdates.tryEmit(
                EditUserData(itemType, it)
            )
        }
    )
}

class EditTextItemModel(
    @StringRes val titleId: Int,
    val value: String?,
    val enable: Boolean,
    val textUpdatedAction: (String) -> Unit = { }
) : EpoxyModelWithHolder<EditTextItemModel.Holder>() {

    init {
        id(titleId.toString())
    }

    override fun bind(holder: Holder) {
        with(holder) {
            val context = rootView.context
            editTextLayout?.hint = context.getString(titleId)
            editText?.apply {
                isEnabled = enable
                setText(value ?: "")
                addTextChangedListener {
                    textUpdatedAction.invoke(it?.toString() ?: "")
                }
            }
        }
    }

    override fun getDefaultLayout() = R.layout.list_item_edit_user_edit_text

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as EditTextItemModel

        if (titleId != other.titleId) return false
        if (value != other.value) return false
        if (enable != other.enable) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + titleId
        result = 31 * result + (value?.hashCode() ?: 0)
        result = 31 * result + enable.hashCode()
        return result
    }

    inner class Holder : EpoxyHolder() {
        lateinit var rootView: View
        var editText: TextInputEditText? = null
        var editTextLayout: TextInputLayout? = null

        override fun bindView(itemView: View) {
            rootView = itemView.also {
                editText = it.findViewById(R.id.input_edit_text)
                editTextLayout = it.findViewById(R.id.input_layout)
            }
        }
    }
}

data class ButtonItemModel(
    @StringRes private val titleId: Int,
    private val isEnable: Boolean,
    private val clickAction: () -> Unit
) : EpoxyModelWithHolder<ButtonItemModel.Holder>() {

    init {
        id(titleId.toString())
    }

    override fun bind(holder: Holder) {
        with(holder) {
            button.apply {
                isEnabled = isEnable
                text = context.getString(titleId)
                setOnClickListener { clickAction.invoke() }
            }
        }
    }

    override fun getDefaultLayout() = R.layout.list_item_edit_user_button

    override fun createNewHolder(parent: ViewParent) = Holder()

    inner class Holder : EpoxyHolder() {
        lateinit var rootView: View
        lateinit var button: Button

        override fun bindView(itemView: View) {
            rootView = itemView.also {
                button = it.findViewById(R.id.button)
            }
        }
    }
}
