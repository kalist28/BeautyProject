package ru.kalistratov.template.beauty.presentation.feature.edituser.view

import android.view.View
import android.view.ViewParent
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItem
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserItemData
import ru.kalistratov.template.beauty.presentation.feature.edituser.entity.EditUserListItemType

class EditUserItemsController : EpoxyController() {

    var items: List<EditUserItem> = emptyList()
    var itemsData: List<EditUserItemData> = emptyList()

    override fun buildModels() {
        items.forEach { item ->
            when(item) {
                is EditUserItem.Button -> null
                is EditUserItem.EditText -> EditTextItemModel(
                    item.titleId,
                    itemsData.find { it.type == item.type }?.value,
                    when(item.type) {
                        EditUserListItemType.EMAIL -> false
                        else -> true
                    }
                )
            }?.addTo(this)
        }
    }
}

data class EditTextItemModel(
    @StringRes val titleId: Int,
    val value: String?,
    val enable: Boolean,
) : EpoxyModelWithHolder<EditTextItemModel.Holder>() {

    init {
        id(titleId.toString() + value)
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
            rootView = itemView
            editText = itemView.findViewById(R.id.input_edit_text)
            editTextLayout = itemView.findViewById(R.id.input_layout)
        }
    }
}
