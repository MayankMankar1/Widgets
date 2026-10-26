package com.hundredwidgets.app

import com.hundredwidgets.app.configure.ConfigureActivity
import com.hundredwidgets.widget.engine.EngineContract
import org.junit.Assert.assertEquals
import org.junit.Test

class EngineContractTest {

    @Test
    fun configureActivityNameMatchesTheEngineContract() {
        // The widget info XML and unconfigured widgets open the chooser by this class name.
        assertEquals(ConfigureActivity::class.java.name, EngineContract.CONFIGURE_ACTIVITY)
    }
}
