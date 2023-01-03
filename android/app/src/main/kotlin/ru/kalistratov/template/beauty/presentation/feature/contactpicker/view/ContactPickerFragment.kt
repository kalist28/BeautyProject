package ru.kalistratov.template.beauty.presentation.feature.contactpicker.view

import android.content.res.Configuration
import android.os.Bundle
import android.view.*
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import ru.kalistratov.template.beauty.R
import ru.kalistratov.template.beauty.databinding.FragmentListBaseBinding
import ru.kalistratov.template.beauty.domain.entity.Id
import ru.kalistratov.template.beauty.infrastructure.base.BaseFragment
import ru.kalistratov.template.beauty.infrastructure.base.BaseIntent
import ru.kalistratov.template.beauty.infrastructure.base.BaseView
import ru.kalistratov.template.beauty.infrastructure.di.UserComponent
import ru.kalistratov.template.beauty.infrastructure.di.ViewModelFactory
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerRouter
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerState
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.ContactPickerViewModel
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.di.ContactPickerModule
import javax.inject.Inject

sealed interface ContactPickerIntent : BaseIntent {
    data class ContactClick(val id: Id) : ContactPickerIntent
    object InitData : ContactPickerIntent
}

class ContactPickerFragment : BaseFragment(), BaseView<ContactPickerIntent, ContactPickerState> {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var contactPickerRouter: ContactPickerRouter

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ContactPickerViewModel::class.java]
    }

    private val binding: FragmentListBaseBinding by viewBinding(CreateMethod.INFLATE)

    private val controller = ContactPickerController()

    override fun injectUserComponent(userComponent: UserComponent) =
        userComponent.plus(ContactPickerModule(this)).inject(this)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ) = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAppBar(getString(R.string.contacts))

        with(binding.recycler) {
            adapter = controller.adapter
            val isTablet = resources.getBoolean(R.bool.isTablet)
            val orientationPortrait = resources.configuration
                    .orientation == Configuration.ORIENTATION_PORTRAIT
            layoutManager = if (isTablet) GridLayoutManager(
                requireContext(),
                if (orientationPortrait) 2 else 3,
            )
            else LinearLayoutManager(requireContext())
        }

        viewModel.apply {
            router = contactPickerRouter
            connectDialogLoadingDisplay()
            connectInto(this@ContactPickerFragment)
        }
    }

    override fun onAppBarBackPressed() = contactPickerRouter.back()

    override fun intents(): Flow<ContactPickerIntent> = merge(
        flowOf(ContactPickerIntent.InitData),
        controller.clicks().map(ContactPickerIntent::ContactClick)
    )

    override fun render(state: ContactPickerState) {
        with(controller) {
            contacts = state.contacts
            requestModelBuild()
        }
    }
}