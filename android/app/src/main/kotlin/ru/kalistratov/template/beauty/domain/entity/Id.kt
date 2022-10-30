package ru.kalistratov.template.beauty.domain.entity

typealias Id = String

val emptyId = ""

fun Id.exist() = this.isNotBlank()