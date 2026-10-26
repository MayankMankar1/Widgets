package com.hundredwidgets.widget.engine

object EngineContract {
    /**
     * The design chooser lives in :app. The launcher opens it through the widget info XML,
     * and an unconfigured widget opens it on tap. An :app test keeps this name in sync.
     */
    const val CONFIGURE_ACTIVITY = "com.hundredwidgets.app.configure.ConfigureActivity"

    const val EXTRA_DESIGN_ID = "com.hundredwidgets.extra.DESIGN_ID"
    const val EXTRA_CELL_SIZE = "com.hundredwidgets.extra.CELL_SIZE"
}
