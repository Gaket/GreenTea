package ru.gaket.tea.runtime.coroutines

data class Update<State, Message, Dependencies>(
  val state: State,
  val commands: Set<Command<Dependencies, Message>> = emptySet()
)

infix fun <State, Message, Dependencies> State.with(command: Command<Dependencies, Message>) =
  Update(this, setOf(command))

infix fun <State, Message, Dependencies> State.with(command: Set<Command<Dependencies, Message>>) =
  Update(this, command)

fun <Message, Dependencies> noCommands() = emptySet<Command<Dependencies, Message>>()
