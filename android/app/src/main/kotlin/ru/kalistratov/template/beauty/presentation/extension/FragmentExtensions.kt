package ru.kalistratov.template.beauty.presentation.extension

import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

inline fun <reified F : Fragment> instanceOf(
    vararg pairs: Pair<String, Any>
): F = F::class.java.newInstance().apply { bundleOf(*pairs) }

inline fun <reified T : View> Fragment.find(id: Int) = view?.findViewById(id) as T

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast
    .makeText(requireContext(), message, length).show()

fun Fragment.showBottomSheet(sheet: BottomSheetDialogFragment) {
    val tag = sheet.javaClass.simpleName
    if (childFragmentManager.findFragmentByTag(tag) == null)
        sheet.show(childFragmentManager, tag)
}
