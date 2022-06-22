# GreenTea 

Green Tea is a library providing a Runtime and base classes that can help you building your Android App based on the Model-View-Update pattern. Heavily inspired by The Elm Architecture [TEA](https://guide.elm-lang.org/architecture/)

# TEA

The Elm Architecture is a pattern of building an app.

## Why?

Here are the meain features of TEA:
*   Unidirectional dataflow
*   Immutable state
*   Pure functions for business / presentation logic
*   Managed side effects
*   Simple View and Side effects that don't contain logic

These features lead to the next system qualities:
* It is easy to navigate the code
* It is easy to reason about what is the current state
* It is easy to test
We'll go through a few examples below.

## How?

The architecture suggests splitting your code into 3 main parts:
* **State** (Model) - literally, what is the state that should be displayed
* **View** - mapping of the state to something that a user can see. Eg populating Android Views or Jetpack Compose functions
* **Update** - some messages that come from a user or from "outside world" that lead to changes in the state

In addition, there is actually one more important piece:
* Side Effects - anything that makes our pure and testable functions into something less predictable, for example IO calls

## So, why is it good?

First, you always know what is the state of your feature. For simplicity, let's say we have a feature per screen. Then, just knowing the current State you know if the user is going to see what's expected or not.

Ideally, everything goes right. And you can always write pure unit tests without any mocks to assure it.

If something still goes wrong, you always know where to find the issue. If a proper state came to the View, then the problem is somewhere in the View. If the state is not the one that you expect - you just check what was the last Message that changed the state - and you can understand, what went wrong, what brings us to...

Time travel. As all the State changes and Messages that led to the State change are logged, it's possible to go through all the changes from a Feature init to the moment where you are at the moment. This feature makes debugging much easier.

## How it works?

All you need to create a new Feature is to define the next parts:
1. State. Everything that you need to display to a user.
1. Messages. What are the things that can change the State? For example "OnRefreshClicked".
1. Update function. Pure function that gets current State, incoming Message, and returns updated State and, potentially, a set of Side Effects.
1. Side Effects. Do you need to make a network request? Declare it here. Result arrived? Send it back to Update as a Message - and the flow is looped! Navigation should also be treated as a Side Effect.
1. Initial Update. How does your Feature start? What is the default State? Should there be any Effects happening right on Start?

And that's how you define a full-blown Feature. What's left?
1. Create a View. Most often it will be a Fragment with some `render(state)` function that needs to be implemented.
2. Connect Feature and View with help of a tea-runtime. This projects presents a Coroutine-based runtime, but implementations may vary.

## This project

This project shows you and example of how a feature, written in MVVM may be migrated to MVU. It contains the Tea Runtime class and a few base classes that make creation of Features very easy and straightforward process.

## Latest version

The current version is in Beta, more tests and real-life example cases are going to be added soon.

## Contacts

For any questions, feel free to reach me out in [Twitter](https://twitter.com/ArtursTwit) or [LinkedIn](https://www.linkedin.com/in/gaket/)

## Readings

[The Official Guide on The Elm Architecture](https://guide.elm-lang.org/architecture/)
[A series of blog posts about ELM on Android](https://proandroiddev.com/taming-state-in-android-with-elm-architecture-and-kotlin-part-1-566caae0f706)
