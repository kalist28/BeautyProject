package ru.kalistratov.template.beauty.presentation.view.epoxy

import android.content.res.Configuration
import android.text.InputType
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.ViewParent
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelWithHolder
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.ListItemPriceBinding
import ru.kalistratov.template.beauty.presentation.entity.PriceContainer
import ru.kalistratov.template.beauty.presentation.view.MarginsBundle
import ru.kalistratov.template.beauty.presentation.view.setMargins

data class PriceModel(
    private val id: String,
    private val selection: PriceContainer?,
    private val selectionChange: (PriceContainer) -> Unit,
    private val marginsBundle: MarginsBundle? = null,
    @StringRes private val startHintId: Int? = null,
    @StringRes private val finishHintId: Int? = null,
) : EpoxyModelWithHolder<PriceModel.Holder>() {

    init {
        id("price_range${id}")
    }

    private var to = when (selection) {
        is PriceContainer.Range -> selection.to
        else -> 0
    }

    private var from = when (selection) {
        is PriceContainer.Range -> selection.from
        is PriceContainer.Amount -> selection.value
        else -> 0
    }

    private var isAmount = selection is PriceContainer.Amount
    private var byAgreement = selection is PriceContainer.ByAgreement

    override fun bind(holder: Holder): Unit = with(holder.binding) {
        rangeSwitch.isChecked = isAmount
        byAgreementSwitch.isChecked = byAgreement
        toEditText.setTextWithChecking(to)
        fromEditText.setTextWithChecking(from)
    }

    override fun createNewHolder(parent: ViewParent) = Holder()

    override fun getDefaultLayout() = R.layout.list_item_price

    private fun pushChange() = selectionChange.invoke(
        if (byAgreement) PriceContainer.ByAgreement
        else when (isAmount) {
            true -> PriceContainer.Amount(from)
            false -> PriceContainer.Range(from, to)
        }
    )

    private fun EditText.setTextWithChecking(value: Int) {
        if (text.toString().toIntOrNull() != value) {
            setText(value.toString())
        }
    }

    private fun EditText.initTimeEditFile() {
        inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        transformationMethod = null
        setRawInputType(Configuration.KEYBOARD_12KEY)
        transformationMethod = object : PasswordTransformationMethod() {
            override fun getTransformation(source: CharSequence?, view: View?): CharSequence {
                return source ?: ""
            }
        }
        doOnTextChanged { text, _, _, _ ->
            var sequence = text ?: return@doOnTextChanged
            if (sequence.isEmpty()) return@doOnTextChanged
            if (sequence.first() != '0') return@doOnTextChanged
            while (sequence.length > 1 && sequence.first() == '0') {
                sequence = sequence.removePrefix("0")
            }
            setText(if (sequence.length == 1 && sequence.first() == '0') null else sequence)
        }
    }

    private fun checkOnError(
        binding: ListItemPriceBinding,
        doOnSuccess: () -> Unit
    ) = with(binding) {
        var errorExist = false
        if (isAmount && from != 0) return doOnSuccess.invoke()
        if (from == 0 && to == 0) return doOnSuccess.invoke()

        toInputLayout.error = if (from >= to && !byAgreement) binding
            .root.context
            .getString(R.string.price_error_to_less_than_from, from)
            .also { errorExist = true } else null

        if (!errorExist) doOnSuccess.invoke()
    }

    private fun ListItemPriceBinding.amountSwitchChanged(check: Boolean) {
        if (check) {
            fromInputLayout.hint = "Сумма"
            root.transitionToEnd()
        } else {
            fromInputLayout.hint = "От"
            root.transitionToStart()
        }
        isAmount = check
        pushChange()
    }

    private fun ListItemPriceBinding.byAgreementSwitchChanged(check: Boolean) {
        toEditText.isEnabled = !check
        fromEditText.isEnabled = !check
        rangeSwitch.isEnabled = !check
        byAgreement = check
        pushChange()
    }

    inner class Holder : EpoxyHolder() {
        lateinit var binding: ListItemPriceBinding
        override fun bindView(v: View) = ListItemPriceBinding.bind(v).run {
            marginsBundle?.let(root::setMargins)
            toEditText.initTimeEditFile()
            fromEditText.initTimeEditFile()

            rangeSwitch.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    amountSwitchChanged(isChecked)
                }
            }

            byAgreementSwitch.apply {
                setOnCheckedChangeListener { _, isChecked ->
                    byAgreementSwitchChanged(isChecked)
                }
            }

            toEditText.addTextChangedListener {
                to = it?.toString()?.toIntOrNull() ?: 0
                checkOnError(this) {
                    pushChange()
                }
            }

            fromEditText.addTextChangedListener {
                from = it?.toString()?.toIntOrNull() ?: 0
                checkOnError(this) {
                    pushChange()
                }
            }
            binding = this
        }
    }
}