package com.model.weather

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.model.weather.ui.main.MainFragment

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun testMainFragment() {
        val scenario = launchFragmentInContainer<MainFragment>()
        onView(withId(R.id.button_retry)).perform(click())
        scenario.onFragment { fragment ->
            fragment.getData(12.9716, 12.9716)
        }
    }

}