package BoardComponents;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import GUI.GameGUI;
import Information.Tag;
import Information.Tag.Side;
import Pieces.Piece;

public abstract class Board extends JPanel implements MouseListener {
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * Tag.SIZE_MAX, (Tag.IMAGE_HEIGHT + 10) * Tag.SIZE_MAX);

    protected boolean saved;
    protected int colorSet;
    protected Side turn;
    protected GameGUI gameGUI;
    protected Position[][] gameBoard;
    protected Piece selectedPiece;
    public List<Position> selectedMovablePositions;
    
    /***
     * this is the basic constructor, creates a brand new board and initializes the board display
     * @param gui - GameGUI that created this board, stored so that board can output text on GameGUI
     * @param colorSet - color of the board, colorSet is index of 2D array of Colors that board will use, stored in Tag.Java
     */
    public Board(GameGUI gui, int colorSet) {
        this.setGameGUI(gui);
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        setLayout(new GridLayout(Tag.SIZE_MAX, Tag.SIZE_MAX, 0, 0));
        this.colorSet = colorSet;
        this.addMouseListener(this);
        this.createNewBoardPositions();
        this.initializePiecesToBoard();
        this.setPanelDimensions(FRA_DIMENSION);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.saved = true;
    }

    /***
     * this constructor initializes a board from the copy of another board as a string
     * does not do anything with a gui because it is used to test check in the background
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and en passant piece
     */
    public Board(String[] pieces)
    {
        this.colorSet = Integer.valueOf(pieces[2]); //0 and 1 are player/side names
        if (pieces[3].equals("white"))
            this.setTurn(Side.WHITE);
        else if (pieces[3].equals("red"))
            this.setTurn(Side.RED);
        else //black
            this.setTurn(Side.BLACK);
        this.setGameBoard(new Position[Tag.SIZE_MAX][Tag.SIZE_MAX]);
        setLayout(new GridLayout(Tag.SIZE_MAX, Tag.SIZE_MAX, 0, 0));
        createNewBoardPositions();
        initializePiecesToBoard(pieces);
    }

    /***
     * this constructor initializes a board from a previous save and creates the display, calls Board(pieces) to handle board creation and then does the additional UI stuff after
     * @param gui - GameGUI that created this board, stored so that board can output text on GameGUI
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and en passant piece
     */
    public Board(GameGUI gui, String[] pieces)
    {
        this(pieces); //this constructor intializes board but does not create the display
        this.setGameGUI(gui);
        this.addMouseListener(this);
        this.setPanelDimensions(FRA_DIMENSION);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.saved = true;
    }

    /***
     * fills gameboard and this panel with all 64 positions
     */
    protected void createNewBoardPositions() {
        for(int i = 0; i < Tag.SIZE_MAX; i++) {
            for(int j = 0; j < Tag.SIZE_MAX; j++){
                if(((i % 2) == 0 && (j % 2) == 0) || ((i % 2) == 1 && (j % 2) == 1)) {
                    this.gameBoard[i][j] = new Position(j, i, true, 20, colorSet);
                    this.add(gameBoard[i][j]);
                } else {
                    this.gameBoard[i][j] = new Position(j, i, false, 20, colorSet);
                    this.add(gameBoard[i][j]);
                }
            }
        }
    }

    /***
     * creates all pieces and assigns them to corresponding positions on the board
     */
    protected abstract void initializePiecesToBoard();

    /***
     * initializes all pieces from copied board onto new board in their respective positions
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations
     */
    protected abstract void initializePiecesToBoard(String[] pieces);
    
    /***
     * calls all relevant JPanel methods to set the size of the board
     * @param size - dimension that panel should be set to
     */
    private void setPanelDimensions(Dimension size){
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setSize(size);
    }

    // setter
    public void setGameBoard(Position[][] board) { this.gameBoard = board; }
    public void setGameGUI(GameGUI gui) { this.gameGUI = gui; }
    public void setTurn(Side side) { this.turn = side; }
    public void setSaved() { this.saved = true;}
    public void setSelectedPiece(Piece selected) { this.selectedPiece = selected; }
    public void setSelectedMovablePositions(Piece piece) { this.selectedMovablePositions = piece.getLegalMoves(this.gameBoard); }

    /***
     * swaps current turn, updates gameGUI
     */
    protected abstract void nextTurn();

    // getter
    public Side getTurn() { return this.turn; }
    public boolean getSaved() { return this.saved; }
    public GameGUI getGameGUI() { return this.gameGUI; }
    public Position[][] getGameBoard() { return this.gameBoard; }
    public Piece getSelectedPiece() { return this.selectedPiece; }
    public List<Position> getMovablePositions() { return this.selectedMovablePositions; }

    /***
     * checks if board can currently be saved
     * @return - returns true if board can be saved, false if it can not
     */
    public abstract boolean canSave();

    /***
     * stores relevant board information as a string, called by save button
     * @return - board color, current turn, all piece names and current locations
     */
    public abstract String asString();


    public abstract void updateBoardGUI();

    /***
     * highlights all moves the selected piece can potentially make, highlighted positions are moves the piece can potentially make, ignoring whether player is moving themself into check
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    protected void highlightLegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(true);
        repaint();
    }

    /***
     * unhighlights potential moves, called when player unselects piece or makes move
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    protected void dehighlightlegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(false);
        repaint();
    }

    

    /***
     * this method sets the selected piece, responsible for highlighting selected piece's position and legal moves
     * @param piece - piece that was selected
     */
    protected abstract void selectPiece(Piece piece);

    /***
     * this method unselects the piece and unhighlights the respective positions
     */
    protected abstract void deselectPiece();

    //mouseClicked and speechCalled convert click or speech to piece selection and then call attemptMove()
    @Override
    public void mouseClicked(MouseEvent e) {
        gameGUI.clearSpeechOutput(); //dont leave output up if user decides to use mouse instead   
        Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
        if(e.getButton() == MouseEvent.BUTTON1 && selectedPiece == null) 
        {
            if(!clickedPosition.isFree() && clickedPosition.getPiece().getSide() == turn)
                selectPiece(clickedPosition.getPiece());
            else
            {
                if (clickedPosition.isFree()) //no piece
                    gameGUI.updateInvalidMove("No piece to select");
                else //piece is wrong side
                    gameGUI.updateInvalidMove("Piece is wrong side");
                deselectPiece();
            }
        } 
        else if (e.getButton() == MouseEvent.BUTTON1 && selectedPiece != null) 
            attemptMove(clickedPosition);
        else
            deselectPiece();
        repaint();
    }

    /*** 
     * this method is called from speechrecognizermain after speech button is pressed
     * @param speechReceived - what speech recognizer heard
     */
    public void speechCalled(String speechReceived)
    {
        gameGUI.updateSpeechOutput(speechReceived);
    	if (speechReceived.equals("clear")) //say clear to unselect piece, clear because sphinx 4 cant understand unselect
    	{
    		deselectPiece();
    		return;
    	}
    	else if (speechReceived.equals("<unk>")) //<unk> means recognizer did not understand speech, will tell user using boardGUI
            return;
    	String[] coordinates = speechReceived.split(" ");
        if (coordinates.length == 1)
        {
        	//rarely passes in single word that is not unk, should only be passing in two words, prevents out of bounds error by accessing coordinates[1] below
        	System.out.println("I did not understand what you said");
    		return;
        }
        String[] xcoords = {"alpha", "bravo", "charlie", "delta", "echo",
                            "foxtrot", "golf", "hotel"};
        String[] ycoords = {"one", "two", "three", "four", "five", "six",
                            "seven", "eight"};
        //save index of identified words (from 0 to 7, like gameBoard which is 2D with range 0 to 7), alpha and one = 0, hotel and eight = 7
        int x = 0;
        int y = 0;
        for (int i = 0; i < 8; i++)
        {
            if (xcoords[i].equals(coordinates[0]))
                x = i;
            if (ycoords[i].equals(coordinates[1]))
                y = i;
        }

        //squares are labelled as x y (alpha one) but gameBoard is [y][x] so access as one alpha, top row of gameBoard (row 0) is row 8 on GUI and bottom row (row 7) is 1 on GUI so subtract yCoords index from 7 to find corresponding position
        Position spokenPosition = gameBoard[7 - y][x];

        if(selectedPiece == null) 
        {
            if(!spokenPosition.isFree() && spokenPosition.getPiece().getSide() == turn)
                selectPiece(spokenPosition.getPiece());
            else
            {
                if (spokenPosition.isFree())
                    gameGUI.updateInvalidMove("No piece to select");
                else
                    gameGUI.updateInvalidMove("Piece is wrong side");
                deselectPiece();
            }
        } 
        else if (selectedPiece != null)
            attemptMove(spokenPosition);
        else
            deselectPiece();
        repaint();
    }

    protected abstract void attemptMove(Position chosen);

    protected abstract void moveAndUnhighlight(Position chosen);

    /***
     * used for debugging, mostly for check/checkmate tests since I can't otherwise see those boards, should not be called in finished project
     */
    public void terminalPrint()
    {
        for (int y = 0; y < 8; y++)
        {
            for (int x = 0; x < 8; x++)
            {
                if (!gameBoard[y][x].isFree())
                    System.out.print(gameBoard[y][x].getPiece().name() + "  ");
                else
                    System.out.print("null ");
            }
            System.out.println();
        }
    }

    /**
     * since the board implements MouseListner, 
     * the following methods have to be overridden. 
     * currently left empty as they are not needed
     */
    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

}