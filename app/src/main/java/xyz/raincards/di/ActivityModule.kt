package xyz.raincards.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import xyz.raincards.utils.navigation.GoTo
import xyz.raincards.utils.navigation.GoToImplementation

@Module
@InstallIn(ActivityComponent::class)
abstract class ActivityModule {

    @ActivityScoped
    @Binds
    abstract fun goTo(implementation: GoToImplementation): GoTo
}
