package org.odk.collect.android.injection.config.architecture;


import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

/**
 * Module for providing our custom ViewModelProvider Factory.
 * Don't modify unless you absolutely need to.
 */
@Module
public abstract class ViewModelFactoryModule {
    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}