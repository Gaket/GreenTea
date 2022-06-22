package ca.gaket.tea.runtime.coroutines

data class Update<State, Message, Dependencies>(
  val state: State,
  val commands: Set<Effect<Dependencies, Message>> = emptySet()
)

infix fun <State, Message, Dependencies> State.with(command: Effect<Dependencies, Message>) =
  Update(this, setOf(command))

infix fun <State, Message, Dependencies> State.with(effect: Set<Effect<Dependencies, Message>>) =
  Update(this, effect)

fun <Message, Dependencies> noCommands() = emptySet<Effect<Dependencies, Message>>()
