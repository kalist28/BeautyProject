package ru.kalistratov.template.beauty.domain.di

import android.content.Context
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import ru.kalistratov.template.beauty.domain.repository.ApiRepository
import ru.kalistratov.template.beauty.domain.service.WorkSequenceService
import ru.kalistratov.template.beauty.infrastructure.Application
import ru.kalistratov.template.beauty.infrastructure.service.WorkSequenceServiceImpl
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarComponent
import ru.kalistratov.template.beauty.presentation.feature.calendar.di.CalendarModule
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaComponent
import ru.kalistratov.template.beauty.presentation.feature.personalarea.di.PersonalAreaModule
import ru.kalistratov.template.beauty.presentation.feature.profile.di.ProfileComponent
import ru.kalistratov.template.beauty.presentation.feature.profile.di.ProfileModule
import ru.kalistratov.template.beauty.presentation.feature.timetable.di.TimetableComponent
import ru.kalistratov.template.beauty.presentation.feature.timetable.di.TimetableModule

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
    fun plus(module: TimetableModule): TimetableComponent
    fun plus(module: PersonalAreaModule): PersonalAreaComponent
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
    fun provideWeekSequenceService(
        apiRepository: ApiRepository
    ): WorkSequenceService = WorkSequenceServiceImpl(apiRepository)
}
