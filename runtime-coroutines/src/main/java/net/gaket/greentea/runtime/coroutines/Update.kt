package net.gaket.greentea.runtime.coroutines


data class Update<State, Message, Dependencies>(
  val state: State,
  val effects: Set<Effect<Dependencies, Message>> = emptySet()
)

infix fun <State, Message, Dependencies> State.with(effect: Effect<Dependencies, Message>) =
  Update(this, setOf(effect))

infix fun <State, Message, Dependencies> State.with(effects: Set<Effect<Dependencies, Message>>) =
  Update(this, effects)

fun <Message, Dependencies> noEffects() = emptySet<Effect<Dependencies, Message>>()
