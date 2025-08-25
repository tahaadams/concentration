# Concentration
A challenging card game.

---

### Game Overview

The Concentration game is implemented using a combination of functional interfaces and utility classes to handle game state, card flipping, matching logic, and rendering. Gameplay is facilitated by event handlers for mouse clicks, key events, and timed ticks, enabling card selection, matching, and game resets. The board and card logic are tested through a comprehensive set of example and test cases provided at the end of the source file.

---

### Comments

The following are documentation comments and key explanations from the source code, providing insight into the architecture and logic of the game:

- **IPred<T>**: Interface for predicate-objects with signature `[T -> Boolean]`.
- **CardSelection**: Returns a boolean determining whether or not the mouse position is within the current card.
- **CardIsFlipped**: Returns a boolean determining whether or not the current card is flipped.
- **CardIsMatched**: Returns a boolean determining whether or not the current card is matched.
- **CardssIsMatched**: Returns a boolean determining whether or not all of the cards are matched.
- **IComparator<T>**: Interface for comparator-objects with signature `[T, T -> int]`.
- **IsMatching**: Returns an integer determining whether or not the two cards match.
- **IFunc<A, R>**: Interface for one-argument function-object with signature `[A -> R]`.
- **FlipCard**: Returns an updated flipped card.
- **FlipCards**: Returns a list of cards with updated flipped cards.
- **FlipCardss**: Returns a list of list of cards with updated flipped cards.
- **FlipCardBack**: Returns a updated flipped card flipped back.
- **FlipCardsBack**: Returns a list of cards with updated flipped cards flipped back.
- **FlipCardssBack**: Returns a list of list of cards with updated flipped cards flipped back.
- **MatchCard**: Returns an updated matched card.
- **MatchCards**: Returns a list of cards with updated matched cards.
- **MatchCardss**: Returns a list of list of cards with updated matched cards.
- **FlippedCards**: Returns a list of cards containing the cards that are currently flipped.
- **MatchedCards**: Returns a list of cards containing the cards that are currently matched.
- **IsMatched**: Returns a new list of list of cards with updated cards, comparing the two that are currently flipped.
- **IFunc2<A1, A2, R>**: Interface for two-argument function-objects with signature `[A1, A2 -> R]`.
- **MergeCards**: Returns a list of cards containing values of both list of cards given.
- **ArrayUtils**: Represent a variety of utility functions:
    - `merge`: EFFECT: Merges two lists together.
    - `map`: Computes the result of mapping the given function over the source list.
    - `ormap`: Computes if at least one of the objects in the list passed the given predicate.
    - `andmap`: Computes if all of the objects in the list passed the given predicate.
    - `foldr`: Computes a function on all objects in the list, and returns the result.
    - `filter`: Produces a new list containing all the items of the given list that pass the predicate.
- **Card**: Represents a Card.
    - `draw()`: Returns an image of a card according to certain booleans in its fields.
- **ConcentrationGame**: Represents a Concentration Game (`extends World`).
    - `makeScene()`: Returns a scene containing the current state of the concentration game.
    - `onMouseClicked(Posn p)`: Flips an unmatched card in the concentration game.
    - `onKeyEvent(String key)`: Refreshes the concentration game by creating a completely new one.
    - `onTick()`: Compares the two cards that are currently flipped and sees if they match.
    - `worldEnds()`: Ends the world when all cards in a concentration game have been matched.
