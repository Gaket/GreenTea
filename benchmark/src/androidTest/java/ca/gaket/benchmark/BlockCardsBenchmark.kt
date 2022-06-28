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
class BlockCardsBenchmark {

  @get:Rule
  val benchmarkRule = BenchmarkRule()

  @Test
  fun textInCard() {
    val context = InstrumentationRegistry.getInstrumentation().context.apply {
      setTheme(R.style.Theme_MaterialComponents_Light)
    }
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.item_calendar_block_slot_card, root, false)
    }
  }

  @Test
  fun textInFrame() {
    val context = InstrumentationRegistry.getInstrumentation().context.apply {
      setTheme(R.style.Theme_MaterialComponents_Light)
    }
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.item_calendar_block_slot_frame, root, false)
    }
  }

  @Test
  fun textOnly() {
    val context = InstrumentationRegistry.getInstrumentation().context.apply {
      setTheme(R.style.Theme_MaterialComponents_Light)
    }
    val inflater = LayoutInflater.from(context)
    val root = FrameLayout(context)

    benchmarkRule.measureRepeated {
      @Suppress("UNUSED_VARIABLE")
      val inflated = inflater.inflate(R.layout.item_calendar_block_slot_text, root, false)
    }
  }
}
