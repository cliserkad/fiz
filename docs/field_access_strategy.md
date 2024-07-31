# Deep Chaining of Field Access in Expressions

Some expressions require a lot of expansion to JVM bytecode.

For Example:

```fiz
System.out.println("Hello World")
```

ByteCode Operations

```bytecode
GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
LDC "Hello World"
INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/String;)V
```

Here we have 3 operations to print a string, even though it only appears as one method call.

The access operator `.` is being chained to access the `println` method on the `out` field of the `System` class.
