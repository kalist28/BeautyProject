package ru.kalistratov.template.beauty.presentation.feature.timetable.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import nl.psdcompany.duonavigationdrawer.views.DuoDrawerLayout
import nl.psdcompany.duonavigationdrawer.views.DuoMenuView
import nl.psdcompany.duonavigationdrawer.widgets.DuoDrawerToggle
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseMenuAdapter
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.timetable.TimetableState


sealed class TimetableIntent : BaseIntent {

}

class TimetableFragment : BaseFragment(), BaseView<TimetableIntent, TimetableState> {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_timetable, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val drawerLayout = find(R.id.drawer) as DuoDrawerLayout
        val drawerToggle = DuoDrawerToggle(
            activity, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        drawerLayout.setDrawerListener(drawerToggle)

        val duoMenuView = find<DuoMenuView>(R.id.menu)
        val menuAdapter = BaseMenuAdapter()
        duoMenuView.adapter = menuAdapter


        drawerToggle.syncState()
    }

    override fun intents(): Flow<TimetableIntent> = emptyFlow()

    override fun render(state: TimetableState) {

    }
}