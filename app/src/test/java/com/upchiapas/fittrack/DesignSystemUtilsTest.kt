package com.upchiapas.fittrack

import com.upchiapas.fittrack.core.designsystem.components.initialsOf
import com.upchiapas.fittrack.core.designsystem.components.normalizeProgress
import org.junit.Assert.assertEquals
import org.junit.Test

class DesignSystemUtilsTest {
    @Test fun progressClampsOutOfRangeAndNonFiniteValues() {
        assertEquals(0f, normalizeProgress(Float.NaN))
        assertEquals(0f, normalizeProgress(Float.NEGATIVE_INFINITY))
        assertEquals(0f, normalizeProgress(-0.4f))
        assertEquals(1f, normalizeProgress(2f))
        assertEquals(0.5f, normalizeProgress(0.5f))
    }

    @Test fun initialsHandleNamesAndEmptyInput() {
        assertEquals("?", initialsOf("  "))
        assertEquals("AL", initialsOf("Ana López"))
        assertEquals("J", initialsOf("José"))
    }
}
