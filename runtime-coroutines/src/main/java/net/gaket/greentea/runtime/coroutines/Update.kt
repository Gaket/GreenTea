package net.gaket.greentea.runtime.coroutines


data class Update<State, Message, Dependencies>(
  val state: State,
  val effects: Set<Command<Dependencies, Message>> = emptySet()
)

infix fun <State, Message, Dependencies> State.with(effect: Command<Dependencies, Message>) =
  Update(this, setOf(effect))

infix fun <State, Message, Dependencies> State.with(effects: Set<Command<Dependencies, Message>>) =
  Update(this, effects)

fun <Message, Dependencies> noCommands() = emptySet<Command<Dependencies, Message>>()
