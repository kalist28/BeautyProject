package ru.kalistratov.template.beauty.infrastructure.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import ru.kalistratov.template.beauty.domain.repository.SequenceDayRepository
import ru.kalistratov.template.beauty.domain.repository.SequenceDayWindowsRepository
import ru.kalistratov.template.beauty.domain.service.PersonalAreaMenuService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiSequenceDayWindowsService
import ru.kalistratov.template.beauty.interfaces.server.service.ApiSequenceService
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.repository.SequenceDayRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.repository.SequenceDayWindowsRepositoryImpl
import ru.kalistratov.template.beauty.infrastructure.service.PersonalAreaMenuServiceImpl
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarComponent
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarModule
import ru.kalistratov.template.beauty.presentation.feature.changepassword.di.ChangePasswordComponent
import ru.kalistratov.template.beauty.presentation.feature.changepassword.di.ChangePasswordModule
import ru.kalistratov.template.beauty.presentation.feature.client.edit.di.EditClientComponent
import ru.kalistratov.template.beauty.presentation.feature.client.edit.di.EditClientModule
import ru.kalistratov.template.beauty.presentation.feature.client.list.di.ClientsListComponent
import ru.kalistratov.template.beauty.presentation.feature.client.list.di.ClientsListModule
import ru.kalistratov.template.beauty.presentation.feature.client.picker.di.ClientPickerComponent
import ru.kalistratov.template.beauty.presentation.feature.client.picker.di.ClientPickerModule
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.di.ContactPickerComponent
import ru.kalistratov.template.beauty.presentation.feature.contactpicker.di.ContactPickerModule
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di.EditSequenceDayWindowsComponent
import ru.kalistratov.template.beauty.presentation.feature.editsequencedaywindows.di.EditSequenceDayWindowsModule
import ru.kalistratov.template.beauty.presentation.feature.edituser.di.EditUserComponent
import ru.kalistratov.template.beauty.presentation.feature.edituser.di.EditUserModule
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.di.MyOfferListComponent
import ru.kalistratov.template.beauty.presentation.feature.myofferlist.di.MyOfferListModule
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.di.MyOfferPickerComponent
import ru.kalistratov.template.beauty.presentation.feature.offer.my.picker.di.MyOfferPickerModule
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaComponent
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaModule
import ru.kalistratov.template.beauty.presentation.feature.profile.di.ProfileComponent
import ru.kalistratov.template.beauty.presentation.feature.profile.di.ProfileModule
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.di.OfferPickerComponent
import ru.kalistratov.template.beauty.presentation.feature.offerpicker.di.OfferPickerModule
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.di.EditReservationComponent
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.edit.di.EditReservationModule
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.di.ReservationListComponent
import ru.kalistratov.template.beauty.presentation.feature.timetable.reservation.list.di.ReservationListModule
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.di.WeekSequenceComponent
import ru.kalistratov.template.beauty.presentation.feature.weeksequence.di.WeekSequenceModule

@UserScope
@Subcomponent(
    modules = [
        UserModule::class
    ]
)
interface UserComponent {

    @Subcomponent.Builder
    interface Builder {
        fun sessionModule(userModule: UserModule): Builder
        fun build(): UserComponent
    }

    fun plus(module: ProfileModule): ProfileComponent
    fun plus(module: CalendarModule): CalendarComponent
    fun plus(module: EditUserModule): EditUserComponent
    fun plus(module: EditClientModule): EditClientComponent
    fun plus(module: ClientsListModule): ClientsListComponent
    fun plus(module: OfferPickerModule): OfferPickerComponent
    fun plus(module: MyOfferListModule): MyOfferListComponent
    fun plus(module: PersonalAreaModule): PersonalAreaComponent
    fun plus(module: WeekSequenceModule): WeekSequenceComponent
    fun plus(module: ClientPickerModule): ClientPickerComponent
    fun plus(module: MyOfferPickerModule): MyOfferPickerComponent
    fun plus(module: ContactPickerModule): ContactPickerComponent
    fun plus(module: ChangePasswordModule): ChangePasswordComponent
    fun plus(module: ReservationListModule): ReservationListComponent
    fun plus(module: EditReservationModule): EditReservationComponent
    fun plus(module: EditSequenceDayWindowsModule): EditSequenceDayWindowsComponent
}

@Module
class UserModule(private val user: String) {

    @Provides
    @UserScope
    fun provideUserSettings(application: Application): Settings = AndroidSettings(
        application.getSharedPreferences("${user}_settings", Context.MODE_PRIVATE)
    )

    @Provides
    @UserScope
    fun providePersonalAreaMenuService(): PersonalAreaMenuService = PersonalAreaMenuServiceImpl()

    @Provides
    @UserScope
    fun provideSequenceDayRepository(
        apiSequenceDayService: ApiSequenceService
    ): SequenceDayRepository = SequenceDayRepositoryImpl(
        apiSequenceDayService
    )

    @Provides
    @UserScope
    fun provideSequenceDayWindowsRepository(
        apiSequenceDayWindowsService: ApiSequenceDayWindowsService
    ): SequenceDayWindowsRepository = SequenceDayWindowsRepositoryImpl(
        apiSequenceDayWindowsService
    )
}
