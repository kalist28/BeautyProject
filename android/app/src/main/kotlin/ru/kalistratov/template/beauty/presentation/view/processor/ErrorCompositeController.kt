package ru.kalistratov.template.beauty.presentation.view.processor

class ErrorCompositeController(
    private var processors: Map<String, TextFieldErrorProcessor>
) {
    init {
        processors.forEach { (key, processor) ->
            processor.listener = { update(key, it) }
        }
    }

    operator fun get(key: String) = processors[key]

    var changesListener: (Boolean) -> Unit = {}

    private val errors = mutableMapOf<String, Boolean>()

    private fun update(key: String, value: Boolean) {
        errors[key] = value
        changesListener.invoke(isErrorsExist())
    }

    fun isErrorsExist(): Boolean {
        var errorExist = false
        errors.forEach { (_, exist) -> errorExist = errorExist || exist }
        return errorExist
    }

    fun checkAndShowErrors(): Boolean {
        var errorExist = false
        processors.forEach { (_, processor) ->
            errorExist = processor.showError() || errorExist
        }
        return errorExist
    }

}