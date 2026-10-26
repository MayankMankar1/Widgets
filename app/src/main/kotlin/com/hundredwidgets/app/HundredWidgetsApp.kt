package com.hundredwidgets.app

import android.app.Application
import com.hundredwidgets.widget.data.WidgetRefreshCoordinator
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class HundredWidgetsApp : Application() {

    @Inject
    lateinit var refreshCoordinator: WidgetRefreshCoordinator

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        refreshCoordinator.start(appScope)
    }
}
