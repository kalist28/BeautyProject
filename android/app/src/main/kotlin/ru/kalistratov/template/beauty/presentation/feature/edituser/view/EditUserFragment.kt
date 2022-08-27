package ru.kalistratov.template.beauty.presentation.feature.edituser.view

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import javax.inject.Inject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.domain.di.UserComponent
import ru.kalistratov.template.beauty.domain.di.ViewModelFactory
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.coroutines.addTo
import ru.kalistratov.template.beauty.infrastructure.extensions.loge
import ru.kalistratov.template.beauty.presentation.extension.find
import ru.kalistratov.template.beauty.presentation.feature.edituser.EditUserState
import ru.kalistratov.template.beauty.presentation.feature.edituser.EditUserViewModel
import ru.kalistratov.template.beauty.presentation.feature.edituser.di.EditUserModule

sealed interface EditUserIntent : BaseIntent {
    object InitData : EditUserIntent
}

class EditUserFragment : BaseFragment(),
    BaseView<EditUserIntent, EditUserState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private var recyclerView: RecyclerView? = null

    private val controller = EditUserItemsController()

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[EditUserViewModel::class.java]
    }

    override fun findViews() {
        recyclerView = find(R.id.recycler_view)
    }

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(EditUserModule(this)).inject(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView?.apply {
            adapter = controller.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        with(viewModel) {
            viewModelScope.launch {
                stateUpdates()
                    .collect(::render)
            }.addTo(jobComposite)
            processIntent(intents())
        }
    }

    override fun intents(): Flow<EditUserIntent> = merge(
        flowOf(EditUserIntent.InitData)
    )

    override fun render(state: EditUserState) {
        loge(state)
        with(controller) {
            items = state.settingItems
            itemsData = state.settingData
            requestModelBuild()
        }
    }
}
