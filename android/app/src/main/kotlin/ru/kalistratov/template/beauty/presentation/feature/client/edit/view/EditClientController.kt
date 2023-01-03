package ru.kalistratov.template.beauty.presentation.feature.client.edit.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.addTextChangedListener
import com.airbnb.epoxy.EpoxyController
import kotlinx.coroutines.flow.asSharedFlow
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.common.RegexPattern
import ru.kalistratov.template.beauty.domain.entity.Client
import ru.kalistratov.template.beauty.domain.entity.exist
import ru.kalistratov.template.beauty.infrastructure.coroutines.mutableSharedFlow
import ru.kalistratov.template.beauty.presentation.feature.client.edit.entity.ClientChange
import ru.kalistratov.template.beauty.presentation.feature.client.edit.entity.ClientKey
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.processor.TextFieldErrorProcessor
import ru.kalistratov.template.beauty.presentation.view.epoxy.PhoneNumberTextFieldModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.TextFieldModel
import ru.kalistratov.template.beauty.presentation.view.epoxy.setMargins
import ru.kalistratov.template.beauty.presentation.view.processor.ErrorCompositeController
import ru.kalistratov.template.beauty.simpleButton
import ru.kalistratov.template.beauty.subtitle
import ru.kalistratov.template.beauty.textMultiline


class EditClientController(
    private val context: Context
) : EpoxyController() {

    var client: Client? = null

    private val errorsController = ErrorCompositeController(
        mapOf(
            ClientKey.NAME.name to TextFieldErrorProcessor.NotBlank(
                TextFieldErrorProcessor.Type.PASSIVE,
                R.string.client_error_name
            ),
            ClientKey.NUMBER.name to TextFieldErrorProcessor.Regex(
                TextFieldErrorProcessor.Type.PASSIVE,
                R.string.client_error_number,
                R.string.required_to_fill,
                Regex(RegexPattern.NUMBER)
            )
        )
    )

    private val buildFinishedUpdates = mutableSharedFlow<Unit>()
    private val mutableSaveClicks = mutableSharedFlow<Unit>()
    private val mutableDataChanges = mutableSharedFlow<ClientChange>()
    private val toPickerClicksMutableFlow = mutableSharedFlow<Unit>()

    fun saveClicks() = mutableSaveClicks.asSharedFlow()
    fun dataChanges() = mutableDataChanges.asSharedFlow()
    fun toPickerClicks() = toPickerClicksMutableFlow.asSharedFlow()
    fun buildFinishedUpdates() = buildFinishedUpdates.asSharedFlow()

    override fun buildModels() {
        val client = client
        if (client != null && !client.id.exist()) buildAddFromContactsClickableText()

        buildTextField(
            key = ClientKey.NAME,
            text = client?.name,
            hintId = R.string.name,
            errorProcessor = errorsController[ClientKey.NAME.name]
        )

        buildTextField(
            key = ClientKey.SURNAME,
            text = client?.surname,
            hintId = R.string.surname,
        )

        buildTextField(
            key = ClientKey.PATRONYMIC,
            text = client?.patronymic,
            hintId = R.string.patronymic,
        )

        PhoneNumberTextFieldModel(
            client?.number,
            { postChange(ClientKey.NUMBER to it) },
            marginsBundle = MarginsBundle.base,
            errorProcessor = errorsController[ClientKey.NUMBER.name]
        ).addTo(this)

        buildNoteTextField(client?.note)
        buildSaveButton()

        buildFinishedUpdates.tryEmit(Unit)
    }

    private fun buildNoteTextField(note: String?) {
        val noteHint = context.getString(R.string.comment)
        val changeProcessor: (String) -> Unit = {
            postChange(ClientKey.NOTE to it)
        }
        textMultiline {
            id("note")
            hint(noteHint)
            note?.let(::text)
            onBind { _, holder, _ ->
                holder.setMargins(MarginsBundle.base)
                holder.dataBinding.root.findViewById<EditText>(R.id.edit_text).apply {
                    addTextChangedListener {
                        it?.toString()?.let(changeProcessor::invoke)
                    }
                }
            }
        }
    }

    private fun buildSaveButton() {
        val listener = View.OnClickListener {
            if (errorsController.checkAndShowErrors())
                return@OnClickListener
            mutableSaveClicks.tryEmit(Unit)
        }
        simpleButton {
            id("save_button")
            text("Сохранить")
            enable(true)
            onBind { _, holder, _ -> holder.setMargins(MarginsBundle.base) }
            onClick(listener)
        }
    }

    private fun buildTextField(
        key: ClientKey,
        text: String?,
        @StringRes hintId: Int,
        errorProcessor: TextFieldErrorProcessor? = null
    ) = TextFieldModel(
        id = key.name,
        text = text,
        hintId = hintId,
        changes = { postChange(key to it) },
        marginsBundle = MarginsBundle.base,
        errorProcessor = errorProcessor
    ).addTo(this)

    private fun postChange(pair: Pair<ClientKey, String>) {
        mutableDataChanges.tryEmit(ClientChange(pair.first, pair.second))
    }

    private fun buildAddFromContactsClickableText() {
        val addFromContactsText = context.getString(R.string.add_from_contacts)
        val addFromContactsTextSpannable = SpannableString(addFromContactsText).apply {
            val startIndex = 0
            val endIndex = addFromContactsText.length
            setSpan(UnderlineSpan(), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        val listener = View.OnClickListener { toPickerClicksMutableFlow.tryEmit(Unit) }
        subtitle {
            id("addFromContactsClickableText")
            gravity(Gravity.CENTER)
            onBind { _, holder, _ ->
                with(holder) {
                    setMargins(MarginsBundle.baseVertical)
                    dataBinding.root.findViewById<TextView>(R.id.subtitle).apply {
                        text = addFromContactsTextSpannable
                        setOnClickListener(listener)
                    }
                }
            }
        }
    }
}