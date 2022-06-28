package ca.gaket.benchmark

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.getsquire.benchmark.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class DrawableIconsBenchmark {

  @get:Rule
  val benchmarkRule = BenchmarkRule()

  @Test
  fun iconInflation() {
    val context = InstrumentationRegistry.getInstrumentation().context
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.layout_notes, root, false)
    }
  }

  @Test
  fun simplifiedIconInflation() {
    val context = InstrumentationRegistry.getInstrumentation().context
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.layout_notes, root, false)
    }
  }

  @Test
  fun tintedIconInflation() {
    val context = InstrumentationRegistry.getInstrumentation().context
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.layout_notes_tinted, root, false)
    }
  }
}
