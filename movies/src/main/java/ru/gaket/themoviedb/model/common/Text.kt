package ru.gaket.themoviedb.model.common

import android.content.Context
import android.text.SpannableStringBuilder
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.text.bold
import androidx.core.text.strikeThrough
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

/**
 * An abstraction over text, so that we can describe them in our data classes layers (StateToUiModels)
 * where we don't necessarily have access to context or resources, and resolve them later in the UI layer.
 *
 * It's also really useful to be able to unit test our models without mocking Resources.
 */
sealed class Text {

    abstract fun resolve(context: Context): CharSequence

    fun resolveAsString(context: Context): String = resolve(context).toString()

    data class MaskedText(val text: Text, val indices: (String) -> IntArray, val mask: Char) : Text() {

        override fun resolve(context: Context): CharSequence = SpannableStringBuilder(text.resolve(context))
            .apply {
                indices(toString())
                    .forEach { index ->
                        replace(index, index + 1, mask.toString())
                    }
            }

        companion object {
            val PHONE_MASK_INDICES: (String) -> IntArray = { phone ->
                phone.indices
                    .drop(2)
                    .dropLast(2)
                    .toIntArray()
            }
            val EMAIL_MASK_INDICES: (String) -> IntArray = { email ->
                val dotIndex = email.indexOfLast { it == '.' }
                val atIndex = email.indexOf('@')

                email.indices
                    .drop(1)
                    .filter { it != atIndex && it < dotIndex }
                    .toIntArray()
            }
        }
    }

    data class BoldText(val text: Text) : Text() {

        override fun resolve(context: Context): CharSequence = SpannableStringBuilder()
            .bold { append(text.resolve(context)) }
    }

    data class StrikeThroughText(val text: Text) : Text() {

        override fun resolve(context: Context): CharSequence = SpannableStringBuilder()
            .strikeThrough { append(text.resolve(context)) }
    }


    data class PlainText(val value: String) : Text() {
        override fun resolve(context: Context): CharSequence = value

        companion object {
            val EMPTY = PlainText("")
        }
    }

    data class ResText(@StringRes val resId: Int, private val formatArgs: List<Any>? = null) : Text() {
        override fun resolve(context: Context): CharSequence =
            if (formatArgs == null) context.resources.getString(resId)
            else {
                val resolvedArgs = formatArgs.map { arg -> if (arg is Text) arg.resolveAsString(context) else arg }
                context.resources.getString(resId, *resolvedArgs.toTypedArray())
            }
    }

    data class ResPluralText(
        @PluralsRes private val resId: Int,
        private val quantity: Int,
        private val formatArgs: List<Any>? = null
    ) : Text() {
        override fun resolve(context: Context): CharSequence =
            if (formatArgs == null) context.resources.getQuantityString(resId, quantity, quantity)
            else context.resources.getQuantityString(resId, quantity, quantity, *formatArgs.toTypedArray())
    }

    data class LocalDateText(
        val temporal: TemporalAccessor,
        val patternResId: Int,
    ) : Text() {

        override fun resolve(context: Context): CharSequence {
            val pattern = context.resources.getString(patternResId)
            return DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format(temporal)
        }
    }
}

/**
 * Convenience method to convert String to Text
 *
 * @return text - [Text.PlainText] wrapping the String
 */
fun String.toText() = Text.PlainText(this)
