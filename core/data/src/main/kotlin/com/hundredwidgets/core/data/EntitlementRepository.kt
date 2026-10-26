package com.hundredwidgets.core.data

import com.hundredwidgets.core.model.Entitlement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/** The only place that answers "is this unlocked?". Play Billing plugs in here later. */
interface EntitlementRepository {
    val entitlement: Flow<Entitlement>
}

internal class FreeEntitlementRepository @Inject constructor() : EntitlementRepository {
    override val entitlement: Flow<Entitlement> = flowOf(Entitlement.FREE)
}
