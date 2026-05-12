# Card Games Android App

A simple and interactive **Card game app** built in **Android Studio**. This project is designed to bring card games to mobile devices with an easy-to-use interface and smooth gameplay.

# APK
https://drive.google.com/file/d/1J7DnJhO0x8eWhRfQQ38jGdiv_PUG3tp6/view?usp=sharing

# Group 11 Backlog 
https://docs.google.com/spreadsheets/d/1SNjqR1fdunVrIrveGRwISWv3dDw8EMh-zkdc3s8yrRY/edit?gid=0#gid=0

# Final Report
https://docs.google.com/document/d/1N4FxgHcsZO1WT4UF8jLDBbBlf7-u8eCuXXCtXD3-d7w/edit?tab=t.0

## Features

- Play classic **Blackjack**, **13**, and **War**
- User-friendly Android interface
- Randomized card dealing
- Hit / Stand gameplay options
- Automatic dealer logic
- Win / Lose / Tie result detection
- Score tracking system
- Restart and replay functionality

## How the Game Works

Blackjack is a card game where the goal is to get as close to **21** as possible without going over.
Thirteen is a card game where the goal is to play all your cards before the other player.
War is a card game where you go head to head to see which card is higher.

### Blackjack Basic Rules
- Number cards are worth their face value
- Face cards (**Jack, Queen, King**) are worth **10**
- **Ace** can count as **1 or 11**
- The player starts with two cards
- The dealer also starts with two cards
- The player can choose to:
  - **Hit** → draw another card
  - **Stand** → keep their current hand
- The dealer draws cards until reaching at least **17**
- The winner is the hand closest to **21** without busting

### Thirteen Basic Rules
- Order of cards goes: 3 4 5 6 7 8 9 10 Jack Queen King Ace 2
- Order of suits goes Spades Clubs Diamonds Hearts
- Both players start with 13 cards
- The player can choose to:
  - **Pass** → Pass their turn
  - **Play** → Play the selected cards
- The player must make a combination of either:
  - Singles 
  - Doubles
  - Triples
  - A sequence
- The winner is the first player to empty their hand.

### War Basic Rules
- Number cards are worth their face value
- Face cards (**Jack, Queen, King**) are worth the same.
- Each player draws one card
- The winner is whoever has the highest card.

## Tech Stack

- **Language:** Java / Kotlin
- **IDE:** Android Studio
- **Platform:** Android
- **UI:** XML layouts
- **Logic:** Object-oriented game design

## Project Structure

```plaintext
CardGameApp/
│── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/cardgameapp/
│   │   │   │   ├── MainActivity
│   │   │   │   ├── GameManager
│   │   │   │   ├── Deck
│   │   │   │   ├── Card
│   │   │   │   ├── Player
│   │   │   │   └── Dealer
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   ├── drawable/
│   │   │   │   └── values/
│── README.md
