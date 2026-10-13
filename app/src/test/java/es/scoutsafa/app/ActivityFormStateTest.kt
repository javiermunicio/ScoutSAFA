package es.scoutsafa.app

import es.scoutsafa.app.domain.model.Section
import es.scoutsafa.app.presentation.viewmodel.ActivityFormState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ActivityFormStateTest {

    private val valid = ActivityFormState(
        title = "Salida al monte",
        section = Section.SCOUTS,
        startDate = "14/11/2026", startTime = "10:00",
        endDate = "14/11/2026", endTime = "18:00"
    )

    @Test
    fun validForm_hasNoError() {
        assertNull(valid.validate())
    }

    @Test
    fun blankTitle_isRejected() {
        assertNotNull(valid.copy(title = "  ").validate())
    }

    @Test
    fun endBeforeStart_isRejected() {
        assertNotNull(valid.copy(endTime = "09:00").validate())
    }

    @Test
    fun badDate_isRejected() {
        assertNotNull(valid.copy(startDate = "32/11/2026").validate())
    }

    @Test
    fun toActivity_roundTripsThroughFrom() {
        val activity = valid.toActivity()
        assertEquals("SCOUTS", activity.section)
        assertEquals(valid, ActivityFormState.from(activity).copy(description = "", location = "", meetingPoint = ""))
    }
}
