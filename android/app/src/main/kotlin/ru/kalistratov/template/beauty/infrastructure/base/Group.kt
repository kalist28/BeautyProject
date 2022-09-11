package ru.kalistratov.template.beauty.infrastructure.base

interface GroupElement<T>

open class Group<T>(
    val id: Long,
    val title: String,
    val items: List<GroupElement<T>>
) : GroupElement<T>

data class GroupItem<T>(
    val item: T
): GroupElement<T>