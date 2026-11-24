package com.benzomageddon

import android.app.Application
import com.benzomageddon.data.repository.CategoryRepository
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class BenzomagedonApp : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch { categoryRepository.ensureDefaults() }
    }
}
