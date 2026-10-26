package com.hundredwidgets.core.model

enum class Tier { FREE, PRO }

enum class Entitlement {
    FREE,
    PRO,
    ;

    fun unlocks(tier: Tier): Boolean = tier == Tier.FREE || this == PRO
}

/** Permissions a design can need. Asked for just in time (README §14). */
enum class AppPermission {
    READ_CALENDAR,
    ACTIVITY_RECOGNITION,
    NOTIFICATION_POLICY,
}
