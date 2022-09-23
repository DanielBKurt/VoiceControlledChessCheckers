package BoardComponents;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import GUI.GameGUI;
import Information.Tag;
import Information.Tag.Side;
import Pieces.Bishop;
import Pieces.King;
import Pieces.Knight;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Queen;
import Pieces.Rook;

public class Board extends JPanel implements MouseListener {
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * Tag.SIZE_MAX, (Tag.IMAGE_HEIGHT + 10) * Tag.SIZE_MAX);

    private boolean saved;
    private int colorSet;
    private Side turn;
    private GameGUI gameGUI;
    private Position[][] gameBoard;
    private Promotion promo;

    private Piece selectedPiece;
    private Piece enPassantPawn;
    private Piece promotionPiece;
    private Piece wKing; //store kings for check and checkmate, assigned during initialize methods
    private Piece bKing;
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
        this.setTurn(Side.WHITE);
        enPassantPawn = null;
        promotionPiece = null;
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
        else
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
    private void createNewBoardPositions() {
        for(int i = 0; i < Tag.SIZE_MAX; i++) {
            for(int j = 0; j < Tag.SIZE_MAX; j++){
                if(((i % 2) == 0 && (j % 2) == 0) || ((i % 2) == 1 && (j % 2) == 1)) {
                    this.gameBoard[i][j] = new Position(j, i, false, 20, colorSet);
                    this.add(gameBoard[i][j]);
                } else {
                    this.gameBoard[i][j] = new Position(j, i, true, 20, colorSet);
                    this.add(gameBoard[i][j]);
                }
            }
        }
    }

    /***
     * creates all pieces and assigns them to corresponding positions on the board
     */
    private void initializePiecesToBoard() {
        // generate rook
        gameBoard[0][0].setPiece(new Rook(Side.BLACK, gameBoard[0][0], Tag.BLACK_ROOK));
        gameBoard[0][7].setPiece(new Rook(Side.BLACK, gameBoard[0][7], Tag.BLACK_ROOK));
        gameBoard[7][0].setPiece(new Rook(Side.WHITE, gameBoard[7][0], Tag.WHITE_ROOK));
        gameBoard[7][7].setPiece(new Rook(Side.WHITE, gameBoard[7][7], Tag.WHITE_ROOK));
        // generate knight
        gameBoard[0][1].setPiece(new Knight(Side.BLACK, gameBoard[0][1], Tag.BLACK_KNIGHT));
        gameBoard[0][6].setPiece(new Knight(Side.BLACK, gameBoard[0][6], Tag.BLACK_KNIGHT));
        gameBoard[7][1].setPiece(new Knight(Side.WHITE, gameBoard[7][1], Tag.WHITE_KNIGHT));
        gameBoard[7][6].setPiece(new Knight(Side.WHITE, gameBoard[7][6], Tag.WHITE_KNIGHT));
        // generate bishop
        gameBoard[0][2].setPiece(new Bishop(Side.BLACK, gameBoard[0][2], Tag.BLACK_BISHOP));
        gameBoard[0][5].setPiece(new Bishop(Side.BLACK, gameBoard[0][5], Tag.BLACK_BISHOP));
        gameBoard[7][2].setPiece(new Bishop(Side.WHITE, gameBoard[7][2], Tag.WHITE_BISHOP));
        gameBoard[7][5].setPiece(new Bishop(Side.WHITE, gameBoard[7][5], Tag.WHITE_BISHOP));
        // generate queen
        gameBoard[0][3].setPiece(new Queen(Side.BLACK, gameBoard[0][3], Tag.BLACK_QUEEN));
        gameBoard[7][3].setPiece(new Queen(Side.WHITE, gameBoard[7][3], Tag.WHITE_QUEEN));
        // generate king
        gameBoard[0][4].setPiece(new King(Side.BLACK, gameBoard[0][4], Tag.BLACK_KING));
        bKing = gameBoard[0][4].getPiece();
        gameBoard[7][4].setPiece(new King(Side.WHITE, gameBoard[7][4], Tag.WHITE_KING));
        wKing = gameBoard[7][4].getPiece();
        // generate Pawn
        for(int i = 0; i < 8; i++) {
            gameBoard[1][i].setPiece(new Pawn(Side.BLACK, gameBoard[1][i], Tag.BLACK_PAWN));
            gameBoard[6][i].setPiece(new Pawn(Side.WHITE, gameBoard[6][i], Tag.WHITE_PAWN));
        }
    }

    /***
     * initializes all pieces from copied board onto new board in their respective positions
     * @param pieces - stores both player names, board color, current turn, all piece names and current locations, and en passant piece
     */
    private void initializePiecesToBoard(String[] pieces)
    {
        for (int i = 4; i < pieces.length - 1; i++) //0 and 1 are player names, 2 is turn, last spot is en passant
        {
            String current = pieces[i];
            int y = current.charAt(4) - '0';
            int x = current.charAt(5) - '0';
            if (current.charAt(3) == 'b') //black
            {
                if (current.charAt(1) == 'K') //king
                {
                    gameBoard[y][x].setPiece(new King(Side.BLACK, gameBoard[y][x], Tag.BLACK_KING));
                    this.bKing = gameBoard[y][x].getPiece();
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'Q')
                    gameBoard[y][x].setPiece(new Queen(Side.BLACK, gameBoard[y][x], Tag.BLACK_QUEEN));
                else if (current.charAt(1) == 'P') //pawn
                {
                    gameBoard[y][x].setPiece(new Pawn(Side.BLACK, gameBoard[y][x], Tag.BLACK_PAWN));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'N') //knight
                    gameBoard[y][x].setPiece(new Knight(Side.BLACK, gameBoard[y][x], Tag.BLACK_KNIGHT));
                else if (current.charAt(1) == 'R') //rook
                {
                    gameBoard[y][x].setPiece(new Rook(Side.BLACK, gameBoard[y][x], Tag.BLACK_ROOK));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else //bishop
                    gameBoard[y][x].setPiece(new Bishop(Side.BLACK, gameBoard[y][x], Tag.BLACK_BISHOP));
            }
            else //white
            {
                if (current.charAt(1) == 'K') //king
                {
                    gameBoard[y][x].setPiece(new King(Side.WHITE, gameBoard[y][x], Tag.WHITE_KING));
                    this.wKing = gameBoard[y][x].getPiece();
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'Q') //queen
                    gameBoard[y][x].setPiece(new Queen(Side.WHITE, gameBoard[y][x], Tag.WHITE_QUEEN));
                else if (current.charAt(1) == 'P') //pawn
                {
                    gameBoard[y][x].setPiece(new Pawn(Side.WHITE, gameBoard[y][x], Tag.WHITE_PAWN));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else if (current.charAt(1) == 'N') //knight
                    gameBoard[y][x].setPiece(new Knight(Side.WHITE, gameBoard[y][x], Tag.WHITE_KNIGHT));
                else if (current.charAt(1) == 'R') //rook
                {
                    gameBoard[y][x].setPiece(new Rook(Side.WHITE, gameBoard[y][x], Tag.WHITE_ROOK));
                    if (current.charAt(6) == 't')
                        gameBoard[y][x].getPiece().setMoved();
                }
                else //bishop
                    gameBoard[y][x].setPiece(new Bishop(Side.WHITE, gameBoard[y][x], Tag.WHITE_BISHOP));
            }
        }
        //en passant is marked simply by just position
        String enPassant = pieces[pieces.length - 1];
        if (enPassant.equals("null"))
            enPassantPawn = null;
        else
        {
            int y = enPassant.charAt(0) - '0';
            int x = enPassant.charAt(1) - '0';
            enPassantPawn = gameBoard[y][x].getPiece();
            if (y == 3)
                gameBoard[2][x].setEnPassant(true);
            else //y == 4
                gameBoard[5][x].setEnPassant(true);
        }
        terminalPrint();
    }
    
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
     * changes current turn, updates GameGUI and tests if player was moved into check
     */
    public void nextTurn() 
    { 
        gameGUI.clearSpeechOutput();
        if (turn == Side.OVER)
            return;
        else
        {
            turn = (this.turn == Side.BLACK) ? Side.WHITE : Side.BLACK;
            gameGUI.updateCurrentTurn(turn);
        }
        if (enPassantPawn != null && enPassantPawn.getSide() == this.turn) //en Passant is valid for only one move
            clearEnPassant(); //en passant for white pawn no longer valid once black moves
        checkHighlight();
    }

    // getter
    public Side getTurn() { return this.turn; }
    public boolean getSaved() { return this.saved; }
    public GameGUI getGameGUI() { return this.gameGUI; }
    public Position[][] getGameBoard() { return this.gameBoard; }
    public Piece getSelectedPiece() { return this.selectedPiece; }
    public List<Position> getMovablePositions() { return this.selectedMovablePositions; }

    /***
     * saves board color, current turn, and all pieces and their locations in a string
     * called by save button to save game or moveLegal to make sure player does not move themself into check
     */
    public String asString()
    {
        String save = "";
        save += String.valueOf(colorSet);
        if (turn == Side.WHITE)
            save += " white";
        else
            save += " black";
        for (int y = 0; y < Tag.SIZE_MAX; y++)
        {
            for (int x = 0; x < Tag.SIZE_MAX; x++)
            {
                if (!gameBoard[y][x].isFree())
                {
                    if (promotionPiece != null && gameBoard[y][x].getPiece() == promotionPiece)
                    {
                        save += " (Q)";
                        if (gameBoard[y][x].getPiece().getSide() == Side.WHITE)
                            save += "w";
                        else
                            save += "b";
                        save += "f";
                        clearPromotion();
                    }
                    else
                    {
                        save += " " + gameBoard[y][x].getPiece().name();
                        if (gameBoard[y][x].getPiece().getSide() == Side.WHITE)
                            save += "w";
                        else
                            save += "b";
                        save += String.valueOf(y) + String.valueOf(x);
                        if (gameBoard[y][x].getPiece().getMoved())
                            save += "t";
                        else
                            save += "f";
                    }
                }
            }
        }
        if (enPassantPawn == null) //when copying board, last index is checked separate from all other indicies so something must be there even if there is not en passant pawn
            save += " null";
        else
            save += " " + String.valueOf(enPassantPawn.getPosition().getPosY()) + String.valueOf(enPassantPawn.getPosition().getPosX()); //en passant pawn itself was already counted in for loop above, simply add position of it separately to identify it
        return save;
    }

    /***
     * highlights all moves the selected piece can potentially make, highlighted positions are moves the piece can potentially make, ignoring whether player is moving themself into check
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    private void highlighedLegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(true);
        repaint();
    }

    /***
     * unhighlights potential moves, called when player unselects piece or makes move
     * @param positions - all positions the selected piece can potentially make, from piece.getLegalMoves()
     */
    private void dehighlightlegalPositions(List<Position> positions) {
        for(int i = 0; i < positions.size(); i++)
            positions.get(i).setHighLight(false);
        repaint();
    }

    /***
     * called after move is made, looks if last move placed the other player in check
     */
    public void checkHighlight()
    {
        //since move is already made, turn has been swapped (if black just moved, turn has already been assigned to white)
        if (turn == Side.WHITE)
        {
            List<Piece> pieces = canBeTaken(Side.BLACK, wKing.getPosition());
            if (pieces.size() != 0)
            {
                if (checkmate(Side.WHITE, pieces))
                {
                    wKing.getPosition().setCheckmate(true);
                    turn = Side.OVER;
                    gameGUI.updateCheckMate(Side.BLACK);
                }
                else
                {
                    wKing.getPosition().setCheck(true);
                    gameGUI.updateTurnCheck();
                }
            }
        }
        else //black
        {
            List<Piece> pieces = canBeTaken(Side.WHITE, bKing.getPosition());
            if (pieces.size() != 0)
            {
                if (checkmate(Side.BLACK, pieces))
                {
                    bKing.getPosition().setCheckmate(true);
                    turn = Side.OVER;
                    gameGUI.updateCheckMate(Side.WHITE);
                }
                else
                {
                    bKing.getPosition().setCheck(true);
                    gameGUI.updateTurnCheck();
                }
            }
        }
        repaint();
    }

    /***
     * this method sets the selected piece, responsible for highlighting selected piece's position and legal moves
     * @param piece - piece that was selected
     */
    private void selectPiece(Piece piece)
    {
        selectedPiece = piece;
        setSelectedMovablePositions(selectedPiece);
        selectedPiece.getPosition().setSelect(true);
        highlighedLegalPositions(selectedMovablePositions);
    }


    /***
     * this method unselects the piece and unhighlights the respective positions
     */
    private void deselectPiece() {
        if(selectedPiece != null) {
            selectedPiece.getPosition().setSelect(false);
            dehighlightlegalPositions(selectedMovablePositions);
            selectedPiece = null;
        }
    }

    /***
     * this method is called when en passant is no longer legal (turn has passed), clears en passant piece and position
     */
    private void clearEnPassant()
    {
        if (enPassantPawn != null)
        {
            int y = (enPassantPawn.getSide() == Side.WHITE) ? 5 : 2;
            int x = enPassantPawn.getPosition().getPosX();
            gameBoard[y][x].setEnPassant(false);
            enPassantPawn = null;
        }
    }

    /***
     * this method assigns the en passant piece on the board and opens the position behind it to be attacked via en passant
     * @param piece - the pawn that can be taken via en passant
     */
    private void setEnPassant(Piece piece)
    {
        clearEnPassant();
        int y = (piece.getSide() == Side.WHITE) ? 5 : 2;
        int x = piece.getPosition().getPosX();
        gameBoard[y][x].setEnPassant(true);
        enPassantPawn = piece;
    }

    /***
     * this method is called to close promotion pop up window and assign promotion variables to null
     */
    private void clearPromotion()
    {
        if (promo != null)
            promo.closePromotion();
        promo = null;
        promotionPiece = null;
    }

    /***
     * this method moves the corresponding rook after king makes castling move
     * @param piece - the king that moved, uses new x position to identify which rook to move
     */
    private void castle(Piece piece)
    {
        int y = piece.getPosition().getPosY(); //get y coord from king
        if (piece.getPosition().getPosX() == 2) //did king go left or right, already moved king when castle is called
        {
            Piece rook = gameBoard[y][0].removePiece();
            gameBoard[y][3].setPiece(rook);
        }
        else if (piece.getPosition().getPosX() == 6)
        {
            Piece rook = gameBoard[y][7].removePiece();
            gameBoard[y][5].setPiece(rook);
        }
        repaint();
    }

    /***
     * this method promotes a pawn to another piece of the player's choosing
     * @param name - name of piece that pawn is promoting to such as (Q) for queen
     */
    public void promote(String name)
    {
        if (promotionPiece != null)
        {
            Side side = promotionPiece.getSide();
            Position temp = promotionPiece.getPosition();
            temp.removePiece();
            clearPromotion();
            if (name.equals("(Q)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Queen(side, temp, Tag.BLACK_QUEEN));
                else
                    temp.setPiece(new Queen(side, temp, Tag.WHITE_QUEEN));
            }
            else if (name.equals("(R)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Rook(side, temp, Tag.BLACK_ROOK));
                else
                    temp.setPiece(new Rook(side, temp, Tag.WHITE_ROOK));
            }
            else if (name.equals("(B)"))
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Bishop(side, temp, Tag.BLACK_BISHOP));
                else
                    temp.setPiece(new Bishop(side, temp, Tag.WHITE_BISHOP));
            }
            else
            {
                if (side == Side.BLACK)
                    temp.setPiece(new Knight(side, temp, Tag.BLACK_KNIGHT));
                else
                    temp.setPiece(new Knight(side, temp, Tag.WHITE_KNIGHT));
            }
            turn = (side == Side.WHITE) ? Side.BLACK : Side.WHITE;
            repaint();
            checkHighlight();
        }
        deselectPiece();
    }

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

    /***
     * this is a helper method for mouseClicked and speechCalled, tries to selected piece to chosen position and handles special rules (en passant, castling, promotion)
     * @param chosen - position that the selected piece (stored as class variable) will move
     */
    public void attemptMove(Position chosen)
    {
        if(chosen.isFree() || chosen.getPiece().getSide() != turn)
        {
            if(selectedMovablePositions.contains(chosen)) 
            {
                boolean couldMove = true;
                //following checks are for unique rules with pawn and king
                //en passant and castling are based on distance moved so they must be checked before piece is moved (2 vs 1 square moved)
                //move is within if statements because I need to check for castling (based on king before move), then move king, then call castle (based on king position after move)
                if (selectedPiece.name().equals("(P)")) //check for en passant
                {
                    if (Math.abs(selectedPiece.getPosition().getPosY() - chosen.getPosY()) == 2) //moving forward two, sets up en passant
                        setEnPassant(selectedPiece);
                    else if (gameBoard[chosen.getPosY()][chosen.getPosX()].getEnPassant()) //not moving forward 2, check if attempting en passant
                    {
                        Position enPassantedPawn = enPassantPawn.getPosition(); //save position so that it can removed after en passant is cleared
                        clearEnPassant();
                        enPassantedPawn.removePiece();
                    }
                    if (moveLegal(selectedPiece, chosen)) //outside of inner if else if because it may be pawn but not using en passant
                        moveAndUnhighlight(chosen);
                    else
                        couldMove = false;
                }
                else if (selectedPiece.name().equals("(K)")) //check for castling
                {
                    if (Math.abs(selectedPiece.getPosition().getPosX() - chosen.getPosX()) == 2)
                    {
                        if (moveLegal(selectedPiece, chosen))
                        {
                            moveAndUnhighlight(chosen); //need to move before calling castle, rook moves to new castled location based on kings new position
                            castle(selectedPiece);
                        }
                        else
                            couldMove = false;
                    }
                    else //selected is king, not moving into castling position
                    {
                        if (moveLegal(selectedPiece, chosen))
                            moveAndUnhighlight(chosen);
                        else
                            couldMove = false;
                    }
                }
                else //default, not pawn or king
                {
                    if (moveLegal(selectedPiece, chosen))
                        moveAndUnhighlight(chosen);
                    else
                        couldMove = false;
                }
                //pawn can take and then be promoted so promotion check comes after move, above check was for en passasnt, below check is for promotion
                if (selectedPiece.name().equals("(P)") && (selectedPiece.getPosition().getPosY() == 7 || selectedPiece.getPosition().getPosY() == 0))
                {
                    promotionPiece = selectedPiece;
                    deselectPiece();
                    turn = Side.PAUSE; //manually pause until promotion is done
                    promo = new Promotion(promotionPiece.getSide(), this, gameGUI.getTurnPlayerName(promotionPiece.getSide()), this.colorSet);
                }
                if (couldMove) //dont unselect and switch turns unless move is actually made
                {
                    deselectPiece();
                    nextTurn();
                }
                else //could not move, either tried to move yourself out of check or already in check and move did not escape it
                {
                    if (bKing.getPosition().isCheck() || wKing.getPosition().isCheck()) //other players last turn placed them in check, square is already red under king
                        gameGUI.updateInvalidMove("Must escape check");
                    else //moved yourself into check
                        gameGUI.updateInvalidMove("Can not move yourself into check");
                }
            }
            else //not in movable positions but either free or taken by enemy piece, selected piece can not move to that position due to its movement limitations (like rook along diagonal or pawn 3 sqaures forward)
                gameGUI.updateInvalidMove("Invalid move for piece");
        }
        else //chosen is not free, occupied by same sided piece
            gameGUI.updateInvalidMove("Can not attack own piece");
    }

    /***
     * this method will create a copy of the board, make the move being attempted on that board, and then ensure the player did not move themself into check, if this move is legal, it will then be made on the actual board in the attemptMove() method
     * @param selected - piece that is being moved
     * @param chosen - position that selected is being moved to
     * @return - true if move is legal (player did not move themself into check), false if it is illegal
     */
    public boolean moveLegal(Piece selected, Position chosen)
    {
        //using ints of positions as positions themselves are tied to this board and tester is a copy with separate positions
        int selectedY = selected.getPosition().getPosY();
        int selectedX = selected.getPosition().getPosX();
        int chosenY = chosen.getPosY();
        int chosenX = chosen.getPosX();
        String boardCopy = "white black " + this.asString(); //added colors in front as place holders for names, similar to how BoardGUI adds player names, makes indicies in boardCopy array consistent with that of saved game
        Board tester = new Board(boardCopy.split(" "));
        //seperation with if else and print statement rather than just return testCheck() is helpful for debug so I'm leaving it but commmenting print out
        if (!tester.testCheck(selectedY, selectedX, chosenY, chosenX))
        {
            //System.out.println("Legal move");
            return true;
        }
        else
        {
            //System.out.println("Illegal move");
            return false;
        }
    }

    /***
     * called by attemptMove if the move is actually legal, makes the move and unhighlights the respective positions
     * @param chosen - the position that the selected piece is being moved to
     */
    public void moveAndUnhighlight(Position chosen)
    {
        selectedPiece.getPosition().setSelect(false);
        wKing.getPosition().setCheck(false);
        bKing.getPosition().setCheck(false);
        selectedPiece.move(chosen);
        saved = false;
    }

    /***
     * this method is called by moveLegal after moveLegal creates a copy of the board, moves piece at selected indicies to chosen indicies and looks if player moved themself into check, takes ints instead of position or piece because position and piece are specific to actual board and therefor not present on copy board within which this method is called
     * @param selectedY - y coordinate of selected piece
     * @param selectedX - x coordinate of selected piece
     * @param chosenY - y coordinate of chosen piece
     * @param chosenX - x coordinate of chosen piece
     * @return - returns true if player is now in check, false if not
     */
    public boolean testCheck(int selectedY, int selectedX, int chosenY, int chosenX)
    {
        gameBoard[selectedY][selectedX].getPiece().move(gameBoard[chosenY][chosenX]);
        terminalPrint();
        if (turn == Side.WHITE) //white moved, turn has not yet been reassigned, make sure white did not move themself into check
            return (canBeTaken(Side.BLACK, wKing.getPosition()).size() != 0); //zero if nothing can attack king
        else //black
            return (canBeTaken(Side.WHITE, bKing.getPosition()).size() != 0);
    }

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
    
    /***
     * this method is used for check/checkmate detection, tests is a given position can be taken by a given side, called with kings position and enemy side to see if king is in check, can be called on checking pieces or in positions in between checking piece and king to try to take or block respectively
     * @param side - side attempting to take position
     * @param initial - position for side to take, position is either empty or occupied by opposite side of side passed in
     * @return
     */
    public List<Piece> canBeTaken(Side side, Position initial)
    {
        List<Piece> pieces = new ArrayList<Piece>();
        //check along all lines
        for (int y = -1; y < 2; y++) //-1, 0, 1
        {
            for (int x = -1; x < 2; x++)
            {
                if (y == 0 && x == 0) //no direction for line
                    continue;
                Piece lineChecked = checkLine(side, initial, y, x);
                if (lineChecked != null)
                    pieces.add(lineChecked);
            }
        }
        //the following checks are not separate methods because each line has only one potential taker, there could be multiple knights or pawns in en passant positions
        //check all potential knight locations
        int[][] knights = {{1, 2}, {1, -2}, {2, 1}, {2, -1}, {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}};
        for (int[] shift : knights)
        {
            //shift to each potential knight location
            int y = initial.getPosY() + shift[1];
            int x = initial.getPosX() + shift[0];
            if (y > -1 && y < 8 && x > -1 && x < 8) //check board bounds
            {
                if (!gameBoard[y][x].isFree()) //check if piece is there
                {
                    Piece potential = gameBoard[y][x].getPiece();
                    if (potential.getSide() == side && potential.name().equals("(N)")) //right side and piece type
                        pieces.add(potential);
                }
            }
        }
        return pieces;
    }

    /***
     * helper method for canBeTaken, looks at line indiciated from yShift and xShift for piece that can take initial, pass in 0 for x for vertical line, 0 for y for horizontal line, or no zeroes for diagonal
     * @param side - side attempting to take position
     * @param initial - position being taken
     * @param yShift - direction on y-axis being checked
     * @param xShift - direction on x-axis being checked
     * @return - returns piece if piece matching passed in side on line can take initial position, null otherwise
     */
    public Piece checkLine(Side side, Position initial, int yShift, int xShift)
    {
        //shift y and x from the start to avoid comparing initial position
        int y = initial.getPosY() + yShift;
        int x = initial.getPosX() + xShift;
        boolean oneShift = true; //true on first loop, within range of king and pawn
        while (y < 8 && y > -1 && x < 8 && x > -1)
        {
            if (gameBoard[y][x].isFree()) //square is empty
            {
                //keep incrementing until a piece is found or out of bounds
                y += yShift;
                x += xShift;
                oneShift = false;
            }
            else //square is taken
            {
                Piece occupyingPiece = gameBoard[y][x].getPiece();
                String name = occupyingPiece.name();
                if (occupyingPiece.getSide() == side) //piece is side I'm looking for
                {
                    if (xShift != 0 && yShift != 0) //both are shifting, diagonal line
                    {
                        if (name.equals("(B)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                            return occupyingPiece;
                        else if (name.equals("(P)") && oneShift) //pawn and within pawn range
                        {
                            //make sure pawn can move in that direction, separate checks because pawn can only move diagonally when attacking unlike pieces above
                            //white pawns start at y = 6, can only move in decreasing y direction
                            //black pawns start at y = 1, can only move in increasing y direction
                            if ((occupyingPiece.getSide() == Side.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Side.BLACK && initial.getPosY() > y))
                            {
                                if (!initial.isFree() || initial.getEnPassant()) //occupyingPiece can take initial because it has a piece or because it is empty but can be taken with en passant
                                    return occupyingPiece;
                            }
                        }
                    }
                    else //only x or y shifting, vertical or horizontal line
                    {
                        if (name.equals("(R)") || name.equals("(Q)") || (name.equals("(K)") && oneShift))
                            return occupyingPiece;
                        else if (name.equals("(P)") && xShift == 0 && initial.isFree()) //pawn can only move forward along y axis to open squares
                        {
                            //ensure that pawn can move in that y direction
                            if ((occupyingPiece.getSide() == Side.WHITE && initial.getPosY() < y) || (occupyingPiece.getSide() == Side.BLACK && initial.getPosY() > y))
                            {
                                if (oneShift || (Math.abs(y - initial.getPosY()) == 2 && !occupyingPiece.getMoved())) //moving forward one square, or has not moved yet amd initial is 2 squares in front of pawn
                                    return occupyingPiece;
                            }
                        }
                    }
                }
                break; //piece will block initial from other pieces further along that line, regardless of type or side of piece
            }
        }
        return null; //no piece on this line can take given position
    }

    /***
     * this method is called after turn is made and player is moved into check, tests if there is any way to escape check
     * @param side - side in check
     * @param pieces - pieces placing the king in check
     * @return - true if player is in checkmate, false if there is a way out of check
     */
    public boolean checkmate(Side side, List<Piece> pieces)
    {
        //start by trying to move king
        Piece king;
        if (side == Side.WHITE)
            king = wKing;
        else
            king = bKing;
        List<Position> kingMoves = king.getLegalMoves(this.gameBoard); //all possible king moves
        for (int i = 0; i < kingMoves.size(); i++)
        {
            if (moveLegal(king, kingMoves.get(i))) //if there is a single move that is legal, it is not checkmate
                return false;
        }
        //can't move king, try taking
        if (pieces.size() > 1) //can't take or block more than one piece per turn, if moving king does not work and there are multiple pieces checking, it is checkmate
            return true;
        Piece checkingPiece = pieces.get(0); //there is only one piece checking king (if statement would return if size > 1) so .get(0) is only checking piece
        List<Piece> takeCheckingPiece = canBeTaken(side, checkingPiece.getPosition()); //what friendly pieces can take checking piece
        for (int i = 0; i < takeCheckingPiece.size(); i++)
        {
            if (moveLegal(takeCheckingPiece.get(i), checkingPiece.getPosition())) //if there is a single way to take the checking piece legally, it is not checkmate
                return false;
        }
        //cannot move king or take checking piece, try blocking piece
        if (checkingPiece.name().equals("(N)")) //cannot block knight
            return true;
        int xShift = 0; //default to 0, assign as positive or negative one if x or y changes
        int yShift = 0;
        if (king.getPosition().getPosX() > checkingPiece.getPosition().getPosX())
            xShift = -1;
        else if (king.getPosition().getPosX() < checkingPiece.getPosition().getPosX())
            xShift = 1;
        if (king.getPosition().getPosY() > checkingPiece.getPosition().getPosY())
            yShift = -1;
        else if (king.getPosition().getPosY() < checkingPiece.getPosition().getPosY())
            yShift = 1;
        int y = king.getPosition().getPosY() + yShift;
        int x = king.getPosition().getPosX() + xShift;
        //start with values shifted along line between king and piece, loop while [y][x] != checkingPiece position because that was already checked when attempting to take checkingPiece
        //look at every square between king and checkingPiece (exclusive), || not && because x == getPosX on vertical and y == getPosY on horizontal lines
        while (y != checkingPiece.getPosition().getPosY() || x != checkingPiece.getPosition().getPosX())
        {
            List<Piece> blockable = canBeTaken(side, gameBoard[y][x]);
            for (int i = 0; i < blockable.size(); i++)
            {
                if (moveLegal(blockable.get(i), gameBoard[y][x])) //if there is a single way to block checking piece legally, it is not checkmate
                    return false;
            }
            y += yShift;
            x += xShift;
        }
        return true;
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