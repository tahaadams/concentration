import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// Interface for predicate-objects with siganture [T -> Boolean]
interface IPred<T> {
  boolean apply(T t);
}

// Returns a boolean determining whether or not the mouse position is within the current card
class CardSelection implements IPred<Posn> {
  Posn mousePosn;

  CardSelection(Posn mousePosn) {
    this.mousePosn = mousePosn;
  }

  public boolean apply(Posn indexPosn) {
    return ((this.mousePosn.x) < (indexPosn.x + 50)) && ((this.mousePosn.x) > (indexPosn.x - 50))
        && ((this.mousePosn.y) < (indexPosn.y + 70)) && ((this.mousePosn.y) > (indexPosn.y - 70));
  }

}

// Returns a boolean determining whether or not the current card is flipped
class CardIsFlipped implements IPred<Card> {

  public boolean apply(Card c) {
    return c.flip;
  }
}

// Returns a boolean determining whether or not the current card is matched
class CardIsMatched implements IPred<Card> {

  public boolean apply(Card card) {
    return card.matched;
  }
}

// Returns a boolean determining whether or not all of the cards are matched
class CardssIsMatched implements IPred<ArrayList<ArrayList<Card>>> {

  public boolean apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().andmap(
        new ArrayUtils().foldr(cardss, new MergeCards(), new ArrayList<Card>()),
        new CardIsMatched());
  }
}

// Interface for comparator-objects with siganture [T -> Boolean]
interface IComparator<T> {
  int apply(T t1, T t2);
}

// Returns an integer determining whether or not the two cards match
class IsMatching implements IComparator<Card> {

  public int apply(Card c1, Card c2) {
    if (c1.suit.equals(c2.suit)) {
      return c1.rank - (c2.rank);
    }
    else if ((c1.suit.equals("♥")) && (c2.suit.equals("♦"))) {
      return c1.rank - (c2.rank);
    }
    else if ((c1.suit.equals("♦")) && (c2.suit.equals("♥"))) {
      return c1.rank - (c2.rank);
    }
    else if ((c1.suit.equals("♣")) && (c2.suit.equals("♠"))) {
      return c1.rank - (c2.rank);
    }
    else if ((c1.suit.equals("♠")) && (c2.suit.equals("♣"))) {
      return c1.rank - (c2.rank);
    }
    else {
      return 1;
    }
  }
}

// Interface for one-argument function-object with signature [A -> R]
interface IFunc<A, R> {
  R apply(A arg);
}

// Returns a updated flipped card
class FlipCard implements IFunc<Card, Card> {
  Posn mousePosn;

  FlipCard(Posn mousePosn) {
    this.mousePosn = mousePosn;
  }

  public Card apply(Card c) {
    if (c.matched) {
      return c;
    }
    else if (new CardSelection(this.mousePosn).apply(c.pinHole)) {
      return new Card(c.rank, c.suit, !c.flip, c.matched, c.pinHole);
    }
    else {
      return c;
    }
  }

}

// Returns a list of cards with updated flipped cards
class FlipCards implements IFunc<ArrayList<Card>, ArrayList<Card>> {
  Posn mousePosn;

  FlipCards(Posn mousePosn) {
    this.mousePosn = mousePosn;
  }

  public ArrayList<Card> apply(ArrayList<Card> cards) {
    return new ArrayUtils().map(cards, new FlipCard(this.mousePosn));
  }

}

// Returns a list of list of cards with updated flipped cards
class FlipCardss implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<ArrayList<Card>>> {
  Posn mousePosn;

  FlipCardss(Posn mousePosn) {
    this.mousePosn = mousePosn;
  }

  public ArrayList<ArrayList<Card>> apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().map(cardss, new FlipCards(this.mousePosn));
  }

}

// Returns a updated flipped card flipped back
class FlipCardBack implements IFunc<Card, Card> {

  public Card apply(Card c) {
    if (c.flip) {
      return new Card(c.rank, c.suit, false, c.matched, c.pinHole);
    }
    else {
      return c;
    }
  }

}

// Returns a list of cards with updated flipped cards flipped back
class FlipCardsBack implements IFunc<ArrayList<Card>, ArrayList<Card>> {

  public ArrayList<Card> apply(ArrayList<Card> cards) {
    return new ArrayUtils().map(cards, new FlipCardBack());
  }

}

// Returns a list of list of cards with updated flipped cards flipped back
class FlipCardssBack implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<ArrayList<Card>>> {

  public ArrayList<ArrayList<Card>> apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().map(cardss, new FlipCardsBack());
  }

}

// Returns a updated matched card
class MatchCard implements IFunc<Card, Card> {

  public Card apply(Card c) {
    if (c.flip) {
      return new Card(c.rank, c.suit, !c.flip, !c.matched, c.pinHole);
    }
    else {
      return c;
    }
  }

}

// Returns a list of cards with updated matched cards
class MatchCards implements IFunc<ArrayList<Card>, ArrayList<Card>> {

  public ArrayList<Card> apply(ArrayList<Card> cards) {
    return new ArrayUtils().map(cards, new MatchCard());
  }

}

// Returns a list of list of cards with updated matched cards
class MatchCardss implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<ArrayList<Card>>> {

  public ArrayList<ArrayList<Card>> apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().map(cardss, new MatchCards());
  }

}

// Returns a list of cards containing the cards that are currently flipped
class FlippedCards implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<Card>> {

  public ArrayList<Card> apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().filter(
        new ArrayUtils().foldr(cardss, new MergeCards(), new ArrayList<Card>()),
        new CardIsFlipped());
  }

}

//Returns a list of cards containing the cards that are currently flipped
class MatchedCards implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<Card>> {

  public ArrayList<Card> apply(ArrayList<ArrayList<Card>> cardss) {
    return new ArrayUtils().filter(
        new ArrayUtils().foldr(cardss, new MergeCards(), new ArrayList<Card>()),
        new CardIsMatched());
  }

}

// Returns a new list of list of cards with updated cards
class IsMatched implements IFunc<ArrayList<ArrayList<Card>>, ArrayList<ArrayList<Card>>> {

  public ArrayList<ArrayList<Card>> apply(ArrayList<ArrayList<Card>> cardss) {
    if (new FlippedCards().apply(cardss).size() == 2) {
      if (new IsMatching().apply(new FlippedCards().apply(cardss).get(0),
          new FlippedCards().apply(cardss).get(1)) == 0) {
        return new MatchCardss().apply(cardss);
      }
      else {
        return new FlipCardssBack().apply(cardss);
      }
    }
    else {
      return cardss;
    }
  }

}

// Interface for two-argument function-objects with signature [A1, A2 -> R]
interface IFunc2<A1, A2, R> {
  R apply(A1 arg1, A2 arg2);
}

// Returns a list of cards containing values of both list of cards given
class MergeCards implements IFunc2<ArrayList<Card>, ArrayList<Card>, ArrayList<Card>> {

  public ArrayList<Card> apply(ArrayList<Card> arr1, ArrayList<Card> arr2) {
    return new ArrayUtils().merge(arr1, arr2);
  }
}

// Represent a variety of Utility Funcitons
class ArrayUtils {

  // EFFECT: Merges two lists together
  <T> ArrayList<T> merge(ArrayList<T> arr1, ArrayList<T> arr2) {
    for (int i = 0; i < arr1.size(); i++) {
      arr2.add(arr1.get(i));
    }
    return arr2;
  }

  // Computes the result of mapping the given function over the source list
  // from the given current index to the end of the list, and returns the
  // given destination list
  // EFFECT: modifies the destination list to contain the mapped results

  <T, U> ArrayList<U> map(ArrayList<T> arr, IFunc<T, U> func) {
    ArrayList<U> result = new ArrayList<U>();
    for (T t : arr) {
      result.add(func.apply(t));
    }
    return result;
  }

  // Computes the result of mapping the given predicate over the source list
  // from the given current index to the end of the list, and returns a
  // boolean determining whether or not at least one of the objects in the
  // list passed the given predicate
  <T> boolean ormap(ArrayList<T> arr, IPred<T> func) {
    return this.ormapHelp(arr, func, 0, false);
  }

  <T> boolean ormapHelp(ArrayList<T> source, IPred<T> func, int curIdx, boolean base) {
    if (curIdx >= source.size()) {
      return base;
    }
    else {
      return func.apply(source.get(curIdx)) || this.ormapHelp(source, func, curIdx + 1, base);
    }
  }

  // Computes the result of mapping the given predicate over the source list
  // from the given current index to the end of the list, and returns a
  // boolean determining whether or not all of the objects in the list
  // passed the given predicate
  <T> boolean andmap(ArrayList<T> arr, IPred<T> func) {
    return this.andmapHelp(arr, func, 0, true);
  }

  <T> boolean andmapHelp(ArrayList<T> source, IPred<T> func, int curIdx, boolean base) {
    if (curIdx >= source.size()) {
      return base;
    }
    else {
      return func.apply(source.get(curIdx)) && this.andmapHelp(source, func, curIdx + 1, base);
    }
  }

  // Computes a function on all objects in the list, and returns the result of
  // said objects
  <T, U> U foldr(ArrayList<T> arr, IFunc2<T, U, U> func, U base) {
    return this.foldrHelp(arr, func, 0, base);
  }

  <T, U> U foldrHelp(ArrayList<T> source, IFunc2<T, U, U> func, int curIdx, U dest) {
    if (curIdx >= source.size()) {
      return dest;
    }
    else {
      return func.apply(source.get(curIdx), this.foldrHelp(source, func, curIdx + 1, dest));
    }
  }

  // Produces a new ArrayList<T> containing all the items of the given list that
  // pass the predicate
  <T> ArrayList<T> filter(ArrayList<T> arr, IPred<T> pred) {
    ArrayList<T> dest = new ArrayList<T>();
    for (int i = 0; i < arr.size(); i++) {
      T item = arr.get(i);
      if (pred.apply(item)) {
        dest.add(item);
      }
    }
    return dest;
  }

}

// Represent a Card
class Card {

  int rank;
  String suit;
  boolean flip;
  boolean matched;
  Posn pinHole;

  Card(int rank, String suit, boolean flip, boolean matched, Posn pinHole) {
    this.rank = rank;
    this.suit = suit;
    this.flip = flip;
    this.matched = matched;
    this.pinHole = pinHole;
  }

  // Returns a image of a card according to certain booleans in its fields
  WorldImage draw() {
    RectangleImage card = new RectangleImage(100, 140, OutlineMode.OUTLINE, Color.BLACK);
    RectangleImage drac = new RectangleImage(100, 140, OutlineMode.SOLID, Color.BLACK);
    TextImage cardvalr = new TextImage(rank + suit, 25, Color.RED);
    TextImage cardvalb = new TextImage(rank + suit, 25, Color.BLACK);
    if (this.matched) {
      return drac;
    }
    else if (this.flip) {
      if ((this.suit.equals("♥")) || (this.suit.equals("♦"))) {
        return new OverlayImage(cardvalr, card);
      }
      else {
        return new OverlayImage(cardvalb, card);
      }
    }
    else {
      return card;
    }
  }

}

// Represent a Concentration Game
class ConcentrationGame extends World {
  ArrayList<ArrayList<Card>> cards;
  Random rand;
  int steps;
  int clicks;
  double tick;
  double tock;

  ConcentrationGame(Random rand, ArrayList<ArrayList<Card>> cards, int steps, int clicks,
      double tick, double tock) {
    this.rand = rand;
    this.cards = cards;
    this.steps = steps;
    this.clicks = clicks;
    this.tick = tick;
    this.tock = tock;
  }

  ConcentrationGame(Random rand, int steps, int clicks, double tick, double tock) {
    this.rand = rand;
    this.cards = new ArrayList<ArrayList<Card>>();
    ArrayList<String> str = new ArrayList<String>(Arrays.asList("01♣", "02♣", "03♣", "04♣", "05♣",
        "06♣", "07♣", "08♣", "09♣", "10♣", "11♣", "12♣", "13♣", "01♦", "02♦", "03♦", "04♦", "05♦",
        "06♦", "07♦", "08♦", "09♦", "10♦", "11♦", "12♦", "13♦", "01♥", "02♥", "03♥", "04♥", "05♥",
        "06♥", "07♥", "08♥", "09♥", "10♥", "11♥", "12♥", "13♥", "01♠", "02♠", "03♠", "04♠", "05♠",
        "06♠", "07♠", "08♠", "09♠", "10♠", "11♠", "12♠", "13♠"));
    // index of the row
    for (int i = 0; i < 4; i++) {
      ArrayList<Card> row = new ArrayList<Card>();
      // index within the row
      for (int j = 0; j < 13; j++) {
        // getting a random index
        int index = rand.nextInt(str.size());
        // getting the value
        String value = str.get(index);
        row.add(new Card(Integer.valueOf(value.substring(0, 2)), value.substring(2, 3), false,
            false, new Posn((50 + (j * 100)), (170 + i * 140))));
        str.remove(index);
      }
      this.cards.add(row);
    }
    this.steps = steps;
    this.clicks = clicks;
    this.tick = tick;
    this.tock = tock;
  }

  ConcentrationGame(int steps, double tick) {
    this(new Random(), steps, 0, tick, 0);
  }

  ConcentrationGame(double tick, int steps) {
    this(new Random(), steps, 0, tick, 0);
  }

  // Returns a scene containing the current state of the concentration game
  public WorldScene makeScene() {
    WorldImage board = new EmptyImage();
    for (int i = 0; i < 4; i++) {
      WorldImage row = new EmptyImage();
      for (int j = 0; j < 13; j++) {
        Card curr = this.cards.get(i).get(j);
        row = new BesideImage(row, curr.draw());
      }
      board = new AboveImage(board, row);
    }
    WorldImage header = new TextImage(
        "Score : " + (26 - (new MatchedCards().apply(this.cards).size()) / 2 + "    Steps Left : "
            + this.steps + "    Time Left : " + this.tick),
        50, Color.WHITE);
    WorldImage headerWin = new TextImage("YOU WIN!", 50, Color.GREEN);
    WorldImage headerStep = new TextImage("OUT OF STEPS!", 50, Color.RED);
    WorldImage headerTime = new TextImage("TIMES UP!", 50, Color.RED);
    WorldImage headerDropbox = new RectangleImage(1300, 100, OutlineMode.SOLID, Color.BLACK);
    WorldImage gameheader = new OverlayImage(header, headerDropbox);
    WorldImage gameheaderWin = new OverlayImage(headerWin, headerDropbox);
    WorldImage gameheaderStep = new OverlayImage(headerStep, headerDropbox);
    WorldImage gameheaderTime = new OverlayImage(headerTime, headerDropbox);
    WorldImage game = new EmptyImage();
    game = new AboveImage(gameheader, board);
    WorldImage gameW = new EmptyImage();
    gameW = new AboveImage(gameheaderWin, board);
    WorldImage gameS = new EmptyImage();
    gameS = new AboveImage(gameheaderStep, board);
    WorldImage gameT = new EmptyImage();
    WorldScene scene = new WorldScene(1300, 660);
    scene.placeImageXY(game, 650, 330);
    gameT = new AboveImage(gameheaderTime, board);
    WorldScene sceneW = new WorldScene(1300, 660);
    sceneW.placeImageXY(gameW, 650, 330);
    WorldScene sceneS = new WorldScene(1300, 660);
    sceneS.placeImageXY(gameS, 650, 330);
    WorldScene sceneT = new WorldScene(1300, 660);
    sceneT.placeImageXY(gameT, 650, 330);
    if (new CardssIsMatched().apply(cards)) {
      return sceneW;
    }
    else if (this.steps <= 0) {
      return sceneS;
    }
    else if (this.tick <= 0) {
      return sceneT;
    }
    else {
      return scene;
    }
  }

  // flips an unmatched card in the concentration game
  public void onMouseClicked(Posn p) {
    if (new FlippedCards().apply(this.cards).size() != 2) {
      if (!(new FlipCardss(p).apply(this.cards).equals(this.cards))) {
        this.clicks = this.clicks + 1;
        if (this.clicks % 2 == 0) {
          this.steps = this.steps - 1;
        }
      }
      this.cards = new FlipCardss(p).apply(cards);
    }
  }

  // Refreshes the concentration game by creating a completely new one
  public void onKeyEvent(String key) {
    if (key.equals("r")) {
      ConcentrationGame reset = new ConcentrationGame(new Random(), this.steps + (this.clicks / 2),
          0, this.tick + Math.floor(this.tock / 4), 0);
      this.steps = reset.steps;
      this.clicks = reset.clicks;
      this.cards = reset.cards;
      this.tick = reset.tick;
      this.tock = reset.tock;
    }
  }

  // Compares the two cards that are currently flipped and sees if they match
  public void onTick() {
    this.cards = new IsMatched().apply(cards);
    this.tock = this.tock + 1;
    if (this.tock % 4 == 0) {
      this.tick = this.tick - 1;
    }
  }

  // Ends the world when all cards in a concentration game have been matched
  public WorldEnd worldEnds() {
    if (new CardssIsMatched().apply(cards)) {
      return new WorldEnd(true, this.makeScene());
    }
    else if (this.steps <= 0) {
      return new WorldEnd(true, this.makeScene());
    }
    else if (this.tick <= 0) {
      return new WorldEnd(true, this.makeScene());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

}

class ExamplesConcentration {

  ConcentrationGame game = new ConcentrationGame(500, 600.0);

  /*
   * void testWorld(Tester t) { game.bigBang(1300, 660, 0.25); }
   */

  Random rand;
  ConcentrationGame testGame;
  Card s06;
  Card h12;
  Card d07;
  Card c04;
  Card s04;
  Card s07;

  Card c04M;
  Card s04M;

  ArrayList<Card> arr0;
  ArrayList<Card> arr1;
  ArrayList<Card> arr2;
  ArrayList<Card> arr3;
  ArrayList<Card> arr3M;
  ArrayList<Card> arrM;
  ArrayList<ArrayList<Card>> arrArr0;
  ArrayList<ArrayList<Card>> arrArr;
  ArrayList<ArrayList<Card>> arrArrM;
  ArrayUtils u;

  ArrayList<ArrayList<Card>> arrArrMakeGame;
  ArrayList<ArrayList<Card>> arrArrMakeGameDone;

  void init() {
    this.rand = new Random();
    this.testGame = new ConcentrationGame(125, 700.0);

    // cards
    this.s06 = new Card(6, "♠", false, false, new Posn(650, 590));
    this.h12 = new Card(12, "♥", false, false, new Posn(450, 310));
    this.d07 = new Card(7, "♦", false, false, new Posn(250, 170));
    this.c04 = new Card(4, "♣", true, false, new Posn(1250, 590));
    this.s04 = new Card(4, "♠", true, false, new Posn(950, 310));
    this.s07 = new Card(7, "♠", false, false, new Posn(350, 170));

    // matched cards
    this.c04M = new Card(4, "♣", false, true, new Posn(1250, 590));
    this.s04M = new Card(4, "♠", false, true, new Posn(950, 310));

    // arrays for testing
    this.arr0 = new ArrayList<Card>();
    this.arr1 = new ArrayList<Card>(Arrays.asList(this.s06, this.h12, this.d07));
    this.arr2 = new ArrayList<Card>(Arrays.asList(this.c04, this.s04, this.s07));
    this.arr3 = new ArrayList<Card>(
        Arrays.asList(this.c04, this.s04, this.s07, this.s06, this.h12, this.d07));
    this.arr3M = new ArrayList<Card>(
        Arrays.asList(this.c04M, this.s04M, this.s07, this.s06, this.h12, this.d07));
    this.arrM = new ArrayList<Card>(Arrays.asList(this.c04M, this.s04M));
    this.arrArr0 = new ArrayList<ArrayList<Card>>();
    this.arrArr = new ArrayList<ArrayList<Card>>(Arrays.asList(this.arr1, this.arr2));
    this.arrArrM = new ArrayList<ArrayList<Card>>(Arrays.asList(this.arrM));

    this.arrArrMakeGame = new ArrayList<ArrayList<Card>>(Arrays.asList(
        new ArrayList<Card>(Arrays.asList(new Card(2, "♥", false, false, new Posn(50, 170)),
            new Card(12, "♥", false, false, new Posn(150, 170)),
            new Card(4, "♠", false, false, new Posn(250, 170)),
            new Card(9, "♠", false, false, new Posn(350, 170)),
            new Card(3, "♦", false, false, new Posn(450, 170)),
            new Card(11, "♣", false, false, new Posn(550, 170)),
            new Card(4, "♦", false, false, new Posn(650, 170)),
            new Card(9, "♣", false, false, new Posn(750, 170)),
            new Card(6, "♣", false, false, new Posn(850, 170)),
            new Card(8, "♦", false, false, new Posn(950, 170)),
            new Card(3, "♣", false, false, new Posn(1050, 170)),
            new Card(9, "♦", false, false, new Posn(1150, 170)),
            new Card(2, "♣", false, false, new Posn(1250, 170)))),
        new ArrayList<Card>(Arrays.asList(new Card(8, "♠", false, false, new Posn(50, 310)),
            new Card(8, "♥", false, false, new Posn(150, 310)),
            new Card(13, "♥", false, false, new Posn(250, 310)),
            new Card(7, "♥", false, false, new Posn(350, 310)),
            new Card(10, "♥", false, false, new Posn(450, 310)),
            new Card(11, "♠", false, false, new Posn(550, 310)),
            new Card(6, "♠", false, false, new Posn(650, 310)),
            new Card(10, "♣", false, false, new Posn(750, 310)),
            new Card(6, "♦", false, false, new Posn(850, 310)),
            new Card(4, "♥", false, false, new Posn(950, 310)),
            new Card(2, "♦", false, false, new Posn(1050, 310)),
            new Card(7, "♣", false, false, new Posn(1150, 310)),
            new Card(8, "♣", false, false, new Posn(1250, 310)))),
        new ArrayList<Card>(Arrays.asList(new Card(12, "♣", false, false, new Posn(50, 450)),
            new Card(3, "♠", false, false, new Posn(150, 450)),
            new Card(10, "♠", false, false, new Posn(250, 450)),
            new Card(6, "♥", false, false, new Posn(350, 450)),
            new Card(1, "♣", false, false, new Posn(450, 450)),
            new Card(12, "♦", false, false, new Posn(550, 450)),
            new Card(1, "♦", false, false, new Posn(650, 450)),
            new Card(13, "♠", false, false, new Posn(750, 450)),
            new Card(5, "♥", false, false, new Posn(850, 450)),
            new Card(10, "♠", false, false, new Posn(950, 450)),
            new Card(1, "♥", false, false, new Posn(1050, 450)),
            new Card(13, "♣", false, false, new Posn(1150, 450)),
            new Card(9, "♥", false, false, new Posn(1250, 450)))),
        new ArrayList<Card>(Arrays.asList(new Card(1, "♠", false, false, new Posn(50, 590)),
            new Card(13, "♦", false, false, new Posn(150, 590)),
            new Card(11, "♦", false, false, new Posn(250, 590)),
            new Card(5, "♣", false, false, new Posn(350, 590)),
            new Card(7, "♦", false, false, new Posn(450, 590)),
            new Card(12, "♠", false, false, new Posn(550, 590)),
            new Card(3, "♥", false, false, new Posn(650, 590)),
            new Card(5, "♠", false, false, new Posn(750, 590)),
            new Card(7, "♠", false, false, new Posn(850, 590)),
            new Card(4, "♣", false, false, new Posn(950, 590)),
            new Card(5, "♦", false, false, new Posn(1050, 590)),
            new Card(2, "♠", false, false, new Posn(1150, 590)),
            new Card(11, "♥", false, false, new Posn(1250, 590))))));

    this.arrArrMakeGameDone = new ArrayList<ArrayList<Card>>(Arrays.asList(
        new ArrayList<Card>(Arrays.asList(new Card(2, "♥", false, true, new Posn(50, 170)),
            new Card(12, "♥", false, true, new Posn(150, 170)),
            new Card(4, "♠", false, true, new Posn(250, 170)),
            new Card(9, "♠", false, true, new Posn(350, 170)),
            new Card(3, "♦", false, true, new Posn(450, 170)),
            new Card(11, "♣", false, true, new Posn(550, 170)),
            new Card(4, "♦", false, true, new Posn(650, 170)),
            new Card(9, "♣", false, true, new Posn(750, 170)),
            new Card(6, "♣", false, true, new Posn(850, 170)),
            new Card(8, "♦", false, true, new Posn(950, 170)),
            new Card(3, "♣", false, true, new Posn(1050, 170)),
            new Card(9, "♦", false, true, new Posn(1150, 170)),
            new Card(2, "♣", false, true, new Posn(1250, 170)))),
        new ArrayList<Card>(Arrays.asList(new Card(8, "♠", false, true, new Posn(50, 310)),
            new Card(8, "♥", false, true, new Posn(150, 310)),
            new Card(13, "♥", false, true, new Posn(250, 310)),
            new Card(7, "♥", false, true, new Posn(350, 310)),
            new Card(10, "♥", false, true, new Posn(450, 310)),
            new Card(11, "♠", false, true, new Posn(550, 310)),
            new Card(6, "♠", false, true, new Posn(650, 310)),
            new Card(10, "♣", false, true, new Posn(750, 310)),
            new Card(6, "♦", false, true, new Posn(850, 310)),
            new Card(4, "♥", false, true, new Posn(950, 310)),
            new Card(2, "♦", false, true, new Posn(1050, 310)),
            new Card(7, "♣", false, true, new Posn(1150, 310)),
            new Card(8, "♣", false, true, new Posn(1250, 310)))),
        new ArrayList<Card>(Arrays.asList(new Card(12, "♣", false, true, new Posn(50, 450)),
            new Card(3, "♠", false, true, new Posn(150, 450)),
            new Card(10, "♠", false, true, new Posn(250, 450)),
            new Card(6, "♥", false, true, new Posn(350, 450)),
            new Card(1, "♣", false, true, new Posn(450, 450)),
            new Card(12, "♦", false, true, new Posn(550, 450)),
            new Card(1, "♦", false, true, new Posn(650, 450)),
            new Card(13, "♠", false, true, new Posn(750, 450)),
            new Card(5, "♥", false, true, new Posn(850, 450)),
            new Card(10, "♠", false, true, new Posn(950, 450)),
            new Card(1, "♥", false, true, new Posn(1050, 450)),
            new Card(13, "♣", false, true, new Posn(1150, 450)),
            new Card(9, "♥", false, true, new Posn(1250, 450)))),
        new ArrayList<Card>(Arrays.asList(new Card(1, "♠", false, false, new Posn(50, 590)),
            new Card(13, "♦", false, false, new Posn(150, 590)),
            new Card(11, "♦", false, false, new Posn(250, 590)),
            new Card(5, "♣", false, false, new Posn(350, 590)),
            new Card(7, "♦", false, false, new Posn(450, 590)),
            new Card(12, "♠", false, false, new Posn(550, 590)),
            new Card(3, "♥", false, false, new Posn(650, 590)),
            new Card(5, "♠", false, false, new Posn(750, 590)),
            new Card(7, "♠", false, false, new Posn(850, 590)),
            new Card(4, "♣", false, false, new Posn(950, 590)),
            new Card(5, "♦", false, false, new Posn(1050, 590)),
            new Card(2, "♠", false, false, new Posn(1150, 590)),
            new Card(11, "♥", false, false, new Posn(1250, 590))))));

    // utils
    this.u = new ArrayUtils();

  }

  // tests randomness
  void testRandomness(Tester t) {
    // initializes data
    init();

    /*
     * This checks the randomness of the board. The game in init() will be different
     * than the game being called because the games are randomly generated in the
     * contructor
     */
    t.checkFail(this.testGame, this.game);
  }

  // test if all cards are present
  void testAllCards(Tester t) {
    // initializes data
    init();

    // checks if size is 52, bc then all cards are present
    // t.checkExpect(this.testGame.str.size(), 52);
    // t.checkExpect(this.game.str.size(), 52);
  }

  // tests draw
  void testDraw(Tester t) {
    // initializes data
    init();

    // checks width and height of cards, to see if drawn correctly
    t.checkExpect(this.c04.draw().getWidth(), 100.0);
    t.checkExpect(this.c04.draw().getHeight(), 140.0);
  }
  
  // tests onMouseClicked
  void testOnMouseClicked(Tester t) {
    // initializes data
    init();

    // game just started, no click
    t.checkExpect(new FlippedCards().apply(testGame.cards).size(), 0);
    t.checkExpect(this.testGame.steps, 125);
    t.checkExpect(this.testGame.clicks, 0);

    // clicks on a card
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);

    // checks if card card be floipped back down
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // makes sure clicking edge does not click card
    this.testGame.onMouseClicked(new Posn(100, 240));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on bar, does nothinh
    this.testGame.onMouseClicked(new Posn(1, 1));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on two card to see see if clicks go up
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);
    this.testGame.onMouseClicked(new Posn(950, 310));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 2);

    // after all the clicks
    t.checkExpect(this.testGame.steps, 123);
    t.checkExpect(this.testGame.clicks, 4);
  }

  // tests onTick
  void testOnTick(Tester t) {
    // initializes data
    init();

    // time has yet to pass
    t.checkExpect(this.testGame.tick, 700.0);
    t.checkExpect(this.testGame.tock, 0.0);

    // simulates time passing
    this.testGame.onTick();
    this.testGame.onTick();
    this.testGame.onTick();
    this.testGame.onTick();

    // shows that time has passed
    t.checkExpect(this.testGame.tick, 699.0);
    t.checkExpect(this.testGame.tock, 4.0);

  }

  // tests onKeyEvent
  void testOnKeyEvent(Tester t) {
    // initializes data
    init();

    // game just started, no click
    t.checkExpect(new FlippedCards().apply(testGame.cards).size(), 0);
    t.checkExpect(this.testGame.steps, 125);
    t.checkExpect(this.testGame.clicks, 0);

    // time has yet to pass
    t.checkExpect(this.testGame.tick, 700.0);
    t.checkExpect(this.testGame.tock, 0.0);

    // clicks on a card
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);

    // checks if card card be flipped back down
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // makes sure clicking edge does not click card
    this.testGame.onMouseClicked(new Posn(100, 240));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on bar, does nothing
    this.testGame.onMouseClicked(new Posn(1, 1));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on two card to see see if clicks go up
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);
    this.testGame.onMouseClicked(new Posn(950, 310));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 2);

    // after all the clicks
    t.checkExpect(this.testGame.steps, 123);
    t.checkExpect(this.testGame.clicks, 4);

    // simulates time passing
    this.testGame.onTick();
    this.testGame.onTick();
    this.testGame.onTick();
    this.testGame.onTick();

    // shows that time has passed
    t.checkExpect(this.testGame.tick, 699.0);
    t.checkExpect(this.testGame.tock, 4.0);

    // resets the game from the above
    this.testGame.onKeyEvent("r");

    // everything is back to initial conditions (randomness has already been tested)
    t.checkExpect(this.testGame.steps, 125);
    t.checkExpect(this.testGame.clicks, 0);
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);
    t.checkExpect(this.testGame.tick, 700.0);
    t.checkExpect(this.testGame.tock, 0.0);

  }

  // tests merge
  void testMerge(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.merge(this.arr0, this.arr0), this.arr0);
    t.checkExpect(u.merge(this.arr1, this.arr2), this.arr3);
    t.checkExpect(u.merge(this.arr0, this.arr2), this.arr2);
    t.checkExpect(u.merge(this.arr1, this.arr0), this.arr1);
  }

  // tests map
  void testMap(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.map(this.arr3, new MatchCard()), this.arr3M);
    t.checkExpect(u.map(this.arr0, new MatchCard()), this.arr0);
  }

  // tests ormap
  void testOrMap(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.ormap(this.arr3M, new CardIsMatched()), true);
    t.checkExpect(u.ormap(this.arr1, new CardIsMatched()), false);
    t.checkExpect(u.ormap(this.arr0, new CardIsMatched()), false);
  }

  // tests ormapHelp
  void testOrMapHelp(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.ormapHelp(this.arr3M, new CardIsMatched(), 0, false), true);
    t.checkExpect(u.ormapHelp(this.arr1, new CardIsMatched(), 0, false), false);
    t.checkExpect(u.ormapHelp(this.arr0, new CardIsMatched(), 0, false), false);
  }

  // test andmap
  void testAndMap(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.andmap(this.arrM, new CardIsMatched()), true);
    t.checkExpect(u.andmap(this.arr0, new CardIsMatched()), true);
    t.checkExpect(u.andmap(this.arr1, new CardIsMatched()), false);
  }

  // test andmapHelp
  void testAndMapHelp(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.andmapHelp(this.arrM, new CardIsMatched(), 0, true), true);
    t.checkExpect(u.andmapHelp(this.arr0, new CardIsMatched(), 0, true), true);
    t.checkExpect(u.andmapHelp(this.arr1, new CardIsMatched(), 0, true), false);
  }

  // test foldr
  void testFoldr(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.foldr(this.arrArr, new MergeCards(), new ArrayList<Card>()), this.arr3);
    t.checkExpect(u.foldr(this.arrArr0, new MergeCards(), new ArrayList<Card>()), this.arr0);
  }

  // test foldrHelp
  void testFoldrHelp(Tester t) {
    // initializes data
    init();

    t.checkExpect(u.foldrHelp(this.arrArr, new MergeCards(), 0, new ArrayList<Card>()), this.arr3);
    t.checkExpect(u.foldrHelp(this.arrArr0, new MergeCards(), 0, new ArrayList<Card>()), this.arr0);
  }

  // tests apply(T t)
  void testApplyPredicate(Tester t) {
    // initializes data
    init();

    // CardSelection - selects a cards
    t.checkExpect(new CardSelection(new Posn(100, 70)).apply(new Posn(75, 70)), true);
    t.checkExpect(new CardSelection(new Posn(300, 70)).apply(new Posn(75, 70)), false);

    // CardIsFlipped - checks if card is flipped
    t.checkExpect(new CardIsFlipped().apply(this.s06), false);
    t.checkExpect(new CardIsFlipped().apply(this.s04), true);

    // CardIsMatched - checks is card is matched
    t.checkExpect(new CardIsMatched().apply(this.c04M), true);
    t.checkExpect(new CardIsMatched().apply(this.s06), false);

    // CardssIsMatched - checks if all cards are matched
    t.checkExpect(new CardssIsMatched().apply(this.arrArrM), true);
    t.checkExpect(new CardssIsMatched().apply(this.arrArr), false);
    t.checkExpect(new CardssIsMatched().apply(this.arrArr0), true);
  }

  // tests apply(T t1, T t2)
  void testApplyComparator(Tester t) {
    // initializes data
    init();

    // IsMatching checks if card can be matched
    t.checkExpect(new IsMatching().apply(this.c04, this.s04), 0);
    t.checkExpect(new IsMatching().apply(this.s06, this.s07), -1);
    t.checkExpect(new IsMatching().apply(this.d07, this.s07), 1);
    t.checkExpect(new IsMatching().apply(this.h12, this.s04), 1);
  }

  // tests apply(A arg)
  void testApplyFunc(Tester t) {
    // initializes data
    init();

    // FlipCard - flips the card
    t.checkExpect(new FlipCard(new Posn(1200, 530)).apply(this.c04M), this.c04M);
    t.checkExpect(new FlipCard(new Posn(640, 530)).apply(this.s06),
        new Card(6, "♠", true, false, new Posn(650, 590)));

    // FlipCards - new list of cards with flipped cards
    t.checkExpect(new FlipCards(new Posn(1200, 100)).apply(this.arr0), this.arr0);
    t.checkExpect(new FlipCards(new Posn(1200, 100)).apply(this.arr1), this.arr1);
    t.checkExpect(new FlipCards(new Posn(630, 530)).apply(this.arr1), new ArrayList<Card>(
        Arrays.asList(new Card(6, "♠", true, false, new Posn(650, 590)), this.h12, this.d07)));

    // FlipCardss - new list of list of flipped cards
    t.checkExpect(new FlipCardss(new Posn(1200, 100)).apply(this.arrArr0), this.arrArr0);
    t.checkExpect(new FlipCardss(new Posn(1200, 100)).apply(this.arrArrM), this.arrArrM);
    t.checkExpect(new FlipCardss(new Posn(630, 530)).apply(this.arrArr),
        new ArrayList<ArrayList<Card>>(Arrays.asList(new ArrayList<Card>(
            Arrays.asList(new Card(6, "♠", true, false, new Posn(650, 590)), this.h12, this.d07)),
            this.arr2)));

    // FlipCardBack - flips card back over
    t.checkExpect(new FlipCardBack().apply(this.c04),
        new Card(4, "♣", false, false, new Posn(1250, 590)));
    t.checkExpect(new FlipCardBack().apply(this.s06), this.s06);

    // FlipCardsBack - returns list with all cards flipped over
    t.checkExpect(new FlipCardsBack().apply(this.arr0), this.arr0);
    t.checkExpect(new FlipCardsBack().apply(this.arr1), this.arr1);
    t.checkExpect(new FlipCardsBack().apply(this.arr2),
        new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, false, new Posn(1250, 590)),
            new Card(4, "♠", false, false, new Posn(950, 310)), this.s07)));

    // FlipCardssBack - returns list of list with all cards flipped
    t.checkExpect(new FlipCardssBack().apply(this.arrArr0), this.arrArr0);
    t.checkExpect(new FlipCardssBack().apply(this.arrArrM), this.arrArrM);
    t.checkExpect(new FlipCardssBack().apply(this.arrArr),
        new ArrayList<ArrayList<Card>>(Arrays.asList(this.arr1,
            new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, false, new Posn(1250, 590)),
                new Card(4, "♠", false, false, new Posn(950, 310)), this.s07)))));

    // MatchCard - matches the card
    t.checkExpect(new MatchCard().apply(this.c04M), this.c04M);
    t.checkExpect(new MatchCard().apply(this.c04),
        new Card(4, "♣", false, true, new Posn(1250, 590)));
    t.checkExpect(new MatchCard().apply(this.s04),
        new Card(4, "♠", false, true, new Posn(950, 310)));

    // MatchCards - new list of cards with matched cards
    t.checkExpect(new MatchCards().apply(this.arr0), this.arr0);
    t.checkExpect(new MatchCards().apply(this.arr1), this.arr1);
    t.checkExpect(new MatchCards().apply(this.arr2),
        new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, true, new Posn(1250, 590)),
            new Card(4, "♠", false, true, new Posn(950, 310)), this.s07)));

    // MatchCardss - new list of list of matched cards
    t.checkExpect(new MatchCardss().apply(this.arrArr0), this.arrArr0);
    t.checkExpect(new MatchCardss().apply(this.arrArr),
        new ArrayList<ArrayList<Card>>(Arrays.asList(this.arr1,
            new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, true, new Posn(1250, 590)),
                new Card(4, "♠", false, true, new Posn(950, 310)), this.s07)))));
    t.checkExpect(new MatchCardss().apply(this.arrArr),
        new ArrayList<ArrayList<Card>>(Arrays.asList(this.arr1,
            new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, true, new Posn(1250, 590)),
                new Card(4, "♠", false, true, new Posn(950, 310)), this.s07)))));

    // MatchedCards - returns list of matched cards
    t.checkExpect(new MatchedCards().apply(this.arrArr0), this.arrArr0);
    t.checkExpect(new MatchedCards().apply(this.arrArr), this.arrArr0);
    t.checkExpect(new MatchedCards().apply(this.arrArrM), this.arrM);

    // IsMatched - returns an updated list of list of cards
    t.checkExpect(new IsMatched().apply(this.arrArr0), this.arrArr0);
    t.checkExpect(new IsMatched().apply(this.arrArr),
        new ArrayList<ArrayList<Card>>(Arrays.asList(this.arr1,
            new ArrayList<Card>(Arrays.asList(new Card(4, "♣", false, true, new Posn(1250, 590)),
                new Card(4, "♠", false, true, new Posn(950, 310)), this.s07)))));
    t.checkExpect(new IsMatched().apply(this.arrArrM), this.arrArrM);

  }

  // tests apply(A1 arg1, A2 arg2)
  void testApplyFunc2(Tester t) {
    // initialized data
    init();

    // MergeCards - merges lists of cards
    t.checkExpect(new MergeCards().apply(this.arr0, this.arr0), this.arr0);
    t.checkExpect(new MergeCards().apply(this.arr1, this.arr2), this.arr3);
    t.checkExpect(new MergeCards().apply(this.arr0, this.arr2), this.arr2);
    t.checkExpect(new MergeCards().apply(this.arr1, this.arr0), this.arr1);
  }

  // own void because mutation
  void testFlippedCards(Tester t) {
    // initializes data
    init();

    // game just started, no click
    t.checkExpect(new FlippedCards().apply(testGame.cards).size(), 0);
    t.checkExpect(this.testGame.steps, 125);
    t.checkExpect(this.testGame.clicks, 0);

    // clicks on a card
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);

    // checks if card card be floipped back down
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // makes sure clicking edge does not click card
    this.testGame.onMouseClicked(new Posn(100, 240));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on bar, does nothinh
    this.testGame.onMouseClicked(new Posn(1, 1));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 0);

    // clicks on two card to see see if clicks go up
    this.testGame.onMouseClicked(new Posn(1250, 590));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 1);
    this.testGame.onMouseClicked(new Posn(950, 310));
    t.checkExpect(new FlippedCards().apply(this.testGame.cards).size(), 2);
  }

}