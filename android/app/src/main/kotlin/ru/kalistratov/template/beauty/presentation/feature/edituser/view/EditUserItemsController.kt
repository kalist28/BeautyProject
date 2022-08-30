package ru.kalistratov.template.beauty.presentation.feature.edituser.view

import android.view.View
import android.view.ViewParent
import android.widget.Button
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItemData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

class EditUserItemsController : EpoxyController() {

    var items: List<EditUserItem> = emptyList()
    var itemsData: List<EditUserItemData> = emptyList()

    val buttonClicks = mutableSharedFlow<EditUserListItemType>()

    override fun buildModels() {
        items.forEach { item ->
            when (item) {
                is EditUserItem.Button -> ButtonItemModel(
                    item.titleId
                ) { buttonClicks.tryEmit(item.type) }
                is EditUserItem.EditText -> EditTextItemModel(
                    item.titleId,
                    itemsData.find { it.type == item.type }?.value,
                    when (item.type) {
                        EditUserListItemType.EMAIL -> false
                        else -> true
                    }
                )
            }.addTo(this)
        }
    }
}

data class EditTextItemModel(
    @StringRes val titleId: Int,
    val value: String?,
    val enable: Boolean,
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
            }
        }
    }

    override fun getDefaultLayout() = R.layout.list_item_edit_user_edit_text

    override fun createNewHolder(parent: ViewParent) = Holder()

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
    @StringRes val titleId: Int,
    val clickAction: () -> Unit
) : EpoxyModelWithHolder<ButtonItemModel.Holder>() {

    init {
        id(titleId.toString())
    }

    override fun bind(holder: Holder) {
        with(holder) {
            button.apply {
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
