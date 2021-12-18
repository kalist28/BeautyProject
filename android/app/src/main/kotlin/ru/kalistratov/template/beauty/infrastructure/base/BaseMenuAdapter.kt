package ru.kalistratov.template.beauty.infrastructure.base

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import nl.psdcompany.duonavigationdrawer.views.DuoOptionView

class BaseMenuAdapter(
    private val options: ArrayList<String> = arrayListOf("Profile", "Profile2", "Timetable", "settings", "Profile", "Profile2", "Timetable", "settings")
) : BaseAdapter() {
    private val optionViews = arrayListOf<DuoOptionView>()

    fun setViewSelected(position: Int, selected: Boolean) {
        // Looping through the options in the menu
        // Selecting the chosen option
        for (i in 0 until optionViews.size) {
            if (i == position) {
                optionViews[i].isSelected = selected
            } else {
                optionViews[i].isSelected = !selected
            }
        }
    }

    override fun getItem(position: Int) = options[position]

    override fun getCount() = options.size

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val option = options[position]

        // Using the DuoOptionView to easily recreate the demo
        val optionView: DuoOptionView =
            if (convertView == null) DuoOptionView(parent.context)
            else convertView as DuoOptionView

        // Using the DuoOptionView's default selectors
        optionView.bind(option, null, null)

        // Adding the views to an array list to handle view selection
        optionViews.add(optionView)
        return optionView
    }
}
