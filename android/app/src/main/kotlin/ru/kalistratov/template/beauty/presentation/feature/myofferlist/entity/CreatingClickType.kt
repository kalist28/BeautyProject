package ru.kalistratov.template.beauty.presentation.feature.myofferlist.entity

sealed interface CreatingClickType {
    object SelectCategory : CreatingClickType
    object SelectType : CreatingClickType
    object SelectTypeProperty : CreatingClickType
    object Save : CreatingClickType
}