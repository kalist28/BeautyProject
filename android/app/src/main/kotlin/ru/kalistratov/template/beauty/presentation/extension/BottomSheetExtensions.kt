package ru.kalistratov.template.beauty.presentation.extension

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.presentation.view.TemplateBottomSheet

fun TemplateBottomSheet.onCloses() = callbackFlow {
    onClosesListener = TemplateBottomSheet.BottomSheetOnClosesListener { trySend(Unit) }
    awaitClose { onClosesListener = null }
}.conflate()
