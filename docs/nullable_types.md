# fiz Nullable Types

All types are non-nullable by default. To make a type nullable, add a `?` after the type name.
Nullables are explicit to avoid the [billion dollar mistake](https://en.wikipedia.org/wiki/Null_pointer).

## Example

In this example, messages can be exchanged by users. Messages must have a recipient and text, but the sender may be null. This is a simple model for allowing anonymous messages.

```fiz
type Message {
    User? sender
    User recipient
    string text
    
    init(User? sender, User recipient, string text) {
        this.sender = sender
        this.recipient = recipient
        this.text = text
    }
    
    /**
     * Returns a string representation of the message.
     * If the sender is null, the message is anonymous.
     */
    string toString(this) {
        if sender == null {
            return "anonymous -> $recipient: $text"
        } else {
            return "$sender -> $recipient: $text"
        }
    }
}
```
