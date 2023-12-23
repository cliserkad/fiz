# fiz

[fiz.dev](https://fiz.dev)

Why another programming language? Because it's fun!
fiz is a programming language that compiles to JVM bytecode. It's a statically typed language with a focus on simplicity and readability.
The overall goal for fiz is to extend and improve Java for increased code velocity and reduced technical debt.

## Planned Features

Default values for function parameters
```fiz
printWelcomeMessage(string name = "World") {
    println("Hello, $name")
}
```

Named parameters
```fiz
main {
    printWelcomeMessage(name: "Earth")
}
```

Non-Null by default
```fiz
main {
    string name = null // Compile error
}
```
