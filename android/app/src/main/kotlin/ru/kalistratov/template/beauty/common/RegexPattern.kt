package ru.kalistratov.template.beauty.common

@Deprecated("Use RegexPattern")
const val checkEmailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$"

object RegexPattern {
    const val ONLY_NUMBERS = "[^0-9]"
    const val EMAIL = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+\$"
    const val NUMBER =
        "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}\$"
}