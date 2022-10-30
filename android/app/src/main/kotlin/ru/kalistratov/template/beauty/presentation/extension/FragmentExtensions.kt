package ru.kalistratov.template.beauty.presentation.extension

import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.kalistratov.template.beauty.presentation.view.bottomsheet.BaseBottomSheet

inline fun <reified F : Fragment> instanceOf(
    vararg pairs: Pair<String, Any>
): F = F::class.java.newInstance().apply { bundleOf(*pairs) }

inline fun <reified T : View> Fragment.find(id: Int) = view?.findViewById(id) as T

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT) = Toast
    .makeText(requireContext(), message, length).show()

fun Fragment.showBottomSheet(sheet: BaseBottomSheet) = sheet.also {
    val tag = sheet.getSheetTag()
    if (childFragmentManager.findFragmentByTag(tag) == null)
        sheet.show(childFragmentManager, tag)
}

fun Fragment.disposeBottomSheet(sheet: BaseBottomSheet?) {
    childFragmentManager.findFragmentByTag(sheet?.getSheetTag())?.let {
        childFragmentManager.beginTransaction().apply {
            remove(it)
            commitAllowingStateLoss()
        }
    }
}
