package ru.gaket.themoviedb.data.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class Try<out T> {
  class Success<T>(val value: T) : Try<T>()
  class Failure(val error: Throwable) : Try<Nothing>()

  override fun toString(): String {
    return when (this) {
      is Success<*> -> "Success[value=$value]"
      is Failure -> "Failure[error=$error]"
    }
  }
}

@OptIn(ExperimentalContracts::class)
fun <T> Try<T>.isSuccess(): Boolean {
  contract {
    returns(true) implies (this@isSuccess is Try.Success)
    returns(false) implies (this@isSuccess is Try.Failure)
  }
  return this is Try.Success
}


/**
 * Return [Try.Success.value] or `null`
 */
fun <T : Any> Try<T>.getOrNull(): T? = (this as? Try.Success)?.value

/**
 * Return [Try.Success.value] or [default]
 */
fun <T : Any> Try<T>.getOrDefault(default: T): T = getOrNull() ?: default

/**
 * Return [Try.Success.value] or throw [throwable] if defined or [Try.Failure.error]
 */
fun <T : Any> Try<T>.getOrThrow(throwable: Throwable? = null): T {
  return when (this) {
    is Try.Success -> value
    is Try.Failure -> throw throwable ?: error
  }
}
