# fiz Syntax

## Code Blocks
code blocks with a new limited scope for variables can be constructed with `{` and `}`

The syntax looks like this:
```fiz
{
    // some code here
}
```
Example:
```fiz
{
    i32 a = 10;
    a += 5;
    assert a == 15;
}
```

## Primitive Types

### Booleans
`boolean` contains either `true` or `false`

### Numbers

| size (bits) | signed integer | unsigned integer | float |
|-------------|----------------|------------------|-------|
| 8           | i8             | u8               |       |
| 16          | i16            | u16              |       |
| 32          | i32            | u32              | f32   |
| 64          | i64            | u64              | f64   |

### Characters & Strings
* `char` represents a utf code point
* `string` a utf-8 encoded string

## Method Definitions

A Method Definition is made of 2 parts:
1. Header
2. Code Block

A Method Header is made of 3 parts:
1. Return Type
2. Method Name
3. Parameters

Parameters are separated by `,` and consist of 3 parts:

1. Type
2. Name
3. Default Value (optional)

The syntax looks like this:
```fiz
ReturnType methodName(ParameterType1 parameterName = defaultValue, ParameterType2 parameterName2) {
    // some code here
}
```
Example:
```fiz
i32 add(i32 a, i32 b) {
    return a + b;
}
```
If there is no return type, or the method returns `void`, the return type is omitted.

## While Loop

### Test Before
```fiz
while condition {
    // some code here
}
```
Example:
```fiz
i32 a = 0;
while a < 5 {
    a += 2;
}
```

### Test After
```fiz
do {
    // some code here
} while condition;
```
Example:
```fiz
i32 a = 0;
do {
    a += 2;
} while a < 5;
```

## Imports
```fiz
import {
    // qualified names here
}
```
Example:
```fiz
import {
    java.lang.System
    java.io.File
}
```

## Keywords
* `type`
* `enum` enumeration
* `assert` presents an error during runtime if the following expression is false
* `const` compile-time constant
* `for` a loop to iterate through a collection
* `main` entry point for a program
* `package` namespace for the program
* `return` return to the calling method
* `if` enter a code block if the following expression evaluates to true
* `else` alternate branch after if
* `null` the absence of data
* `import` import the types and header info from another file
* `while` loop depending on condition
