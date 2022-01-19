package ru.kalistratov.template.beauty.presentation.extension

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.BaseBottomSheet
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.BottomSheetOnClosesListener

fun BaseBottomSheet.onCloses() = callbackFlow {
    onClosesListener = BottomSheetOnClosesListener { trySend(Unit) }
    awaitClose { onClosesListener = null }
}.conflate()
