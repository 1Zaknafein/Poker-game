# Desktop multiplayer Poker application based on Sockets in Java

The system has been designed in a way to allow multiple players to play a game of Texas
Hold’em, a Poker variant, together. It consists of two separate programs; one to be used as a
server that players connect to, and second to be used by the players, with a graphical user
interface that allows players to interact with it. The program used as a server has no graphical
user interface, and should be run in a terminal or command prompt, where system messages
can be seen, but could be run in the usual way as well.

Using the system requires both players and the server to have an internet connection, or be in
the same network, with appropriate IP address assigned. In order to play a game of Poker,
players are required to use a separate program, which consists of a graphical user interface
and is able to exchange data with the server.

The system implements the Texas Hold’em variant according to its definition and rules.
There are four rounds in the game, with the maximum of five community cards being
available to all players, followed by two player-specific cards, which other players are unable
to see until a winner has been picked in the last round. The best possible hand - a set of five
cards - is automatically selected from a set of community cards (5) and player’s cards (2), and
then comparing them against other players’ hands. The winner is selected based on their hand
ranking, which has been created according to the Poker’s Texas Hold’em rules.

## Server side
Server side handles most data processing, and is responsible for holding the state of tables,
ongoing games and their logic, player and communication data. To achieve this with optimal
performance, the system uses multithreading, with each thread having a specific function. 
The number of server threads equals Np + Nt + 1.
(Np = number of players, Nt = number of tables)

## Client side
Client side handles the display of the graphical user interface (GUI) and the information
received from the server, as well as sending data to the server in a form of ‘commands’.
The GUI has been made resizable, in order to allow players with various screen sizes to enjoy
the game. To ensure high performance, multithreading is used for client side as well, but only
two threads are used; GUI and handler thread.

## Things not yet implemented

- Player account system and saving data
- use of voice commands and webcams

## Usage requirements

In order to use the system, the following requirements must be met:
- Both JAR files - server and client - must be run on a platform that is able to run JAR files.
- The system where files are run requires Java’s JDK 16.
- TCP/IP protocol on port 8888 has to be allowed

## How to use

Firstly run the server (PokerGame.jar) file on a machine you wish to be the server/host. It will be then start listening for incoming connections on port 8888.
Then clients can connect via PokerClient.jar file, by inputting their player name and setting server IP address. The default IP address is localhost/local machine. 


