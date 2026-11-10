The Controller and the End of the Game

1. Decision Maker and the one that displays
The MoveGenerator.status() is responsible for determining if the game is still in progress or not, whether it is a checkmate, or a draw through checking if the king is safe and whether it still has moves left.
The GameController.handle() displays the results through the BoardView.showMessage.

2. The Dependency Rule
Both Main and GameController are dependent on classes in the view package while the model and engine must not import the view package since the rules and the state of the game must remain independent of how the game is played.

3. Swing View addition
In order to create a graphical Swing interface, I would have to create a new view class to implement the already existing BoardView interface and update Main.java to enable the user choose the new interface.