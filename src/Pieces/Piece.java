package Pieces;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import BoardComponents.Position;
import Information.Tag;
import Information.Tag.Side;

public abstract class Piece {
    private boolean alive;
    private Side side;
    private Position position;
    private BufferedImage image;

    public Piece(Side side, Position start, String imageFileName) {
        setAlive();
        setSide(side);
        setPosition(start);
        setImage(imageFileName);
    }

    // setters
    public void setAlive() { this.alive = true; }
    public void setDead() { this.alive = false; }
    public void setSide(Side side) { this.side = side; }
    public void setPosition(Position position) { this.position = position; }
    
    public void setImage(String imageFileName) { 
        if (this.image == null)
            try { this.image = ImageIO.read(new File(imageFileName)); //try catch because different java versions require different paths to find and render image
            } 
            catch (IOException e) { 
                try { 
                    this.image = ImageIO.read(new File("VoiceControlChess\\" + imageFileName));
                } catch (IOException a) { a.printStackTrace(); }
        }
    }

    // getters
    public Side getSide() { return this.side; }
    public boolean isAlive() { return this.alive == true; }
    public boolean isDead() { return this.alive == false; }
    public Position getPosition() { return this.position; }
    public Image getImage() { return this.image; }
    public void draw(Graphics g) { g.drawImage(this.getImage(), this.getPosition().getPosX(), this.getPosition().getPosY(), null); }

    // methods handling move
    public boolean move(Position desPosition) {
        boolean canMove = true;
        Piece desPiece = desPosition.getPiece();

        if(desPiece != null) {
            if(desPiece.getSide() == this.side) {
                canMove = false;
            } else {
                desPiece = null;
                desPosition.setPiece(this.position.removePiece());
            }
        } else {
            desPosition.setPiece(this.position.removePiece());
        }
        if (canMove)
            this.setMoved();
        return canMove;
    }


    // methods handling legal check
    public boolean positionInBounds(int value) {
        return (value >= Tag.SIZE_MIN && value < Tag.SIZE_MAX);
    }

    //spot is open or occupied by enemy piece, used by king and knight
    public boolean basicLegalPosition(Position[][] gameBoard, int y, int x) {
        return (gameBoard[y][x].isFree() || gameBoard[y][x].getPiece().getSide() != this.getSide());
    }

    //spot is occupied by enemy piece, used for pawn as pawn is only piece with separate moves and attacks
    public boolean complexLegalPostion(Position[][] gameBoard, int y, int x) {
        return (!gameBoard[y][x].isFree() && gameBoard[y][x].getPiece().getSide() != this.getSide());
    }

    //spot can be taken by en passant, called by pawn
    public boolean legalEnPassant(Position[][] gameBoard, int y, int x)
    {
        return gameBoard[y][x].getEnPassant();
    }

    //called by king, checks if it can castle and move to specific x index (either 2 or 6, left or right)
    public boolean legalCastling(Position[][] gameBoard, int y, int x)
    {
        if (!this.getMoved())
        {
            if (x == 2)
            {
                if (gameBoard[y][3].isFree() && gameBoard[y][2].isFree() && gameBoard[y][1].isFree() && (!gameBoard[y][0].isFree() && gameBoard[y][0].getPiece().getSide() == this.getSide() && !gameBoard[y][0].getPiece().getMoved()))
                    return true;
                else
                    return false;
            }
            else //x == 6
            {
                if (gameBoard[y][5].isFree() && gameBoard[y][6].isFree() && (!gameBoard[y][7].isFree() && gameBoard[y][7].getPiece().getSide() == this.getSide() && !gameBoard[y][7].getPiece().getMoved()))
                    return true;
                else
                    return false;
            }
        }
        return false;
    }

    /***
     * this method gets all legal position linear from start position
     * @param gameBoard - board to check
     * @param start - starting position to get legal moves from
     * @return all legal positions north, south, east, and west from start
     */
    public List<Position> getLegalLinearPositions(Position[][] gameBoard, Position start) {
        List<Position> linearPositions = new ArrayList<Position>();
        int[][] lines = {{-1, 0}, {1, 0}, {0, 1}, {0, -1}}; //left, right, up, down
        for (int[] shift : lines)
        {
            //similar to can be taken logic in board, start with shifted values to avoid checking start position
            int x = start.getPosX() + shift[0];
            int y = start.getPosY() + shift[1];
            while (x > -1 && x < Tag.SIZE_MAX && y > -1 && y < Tag.SIZE_MAX) //check all bounds since x or y could be increasing or decreasing
            {
                if (gameBoard[y][x].isFree()) //empty square
                    linearPositions.add(gameBoard[y][x]);
                else //taken
                {
                    if (gameBoard[y][x].getPiece().getSide() != this.getSide())
                        linearPositions.add(gameBoard[y][x]);
                    break; //can not move past piece, can either take enemy piece or not take friendly piece, break either way
                }
                x += shift[0];
                y += shift[1];
            }
        }
        return linearPositions;
    }

   /***
     * Method that gets all legal diagonal positions from start position
     * @param gameBoard - board to check
     * @param start - starting position to get legal moves from
     * @return all legal positions north east, north west, south east, and south west from start
     */
    public List<Position> getLegalDiagonalPositions(Position[][] gameBoard, Position start) {
        List<Position> diagonalPositions = new ArrayList<Position>();
        int[][] diagonals = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}}; //top left, bottom left, bottom right, top right
        for (int[] shift : diagonals)
        {
            //similar to can be taken logic in board, start with shifted values to avoid checking start position
            int x = start.getPosX() + shift[0];
            int y = start.getPosY() + shift[1];
            while (x > -1 && x < Tag.SIZE_MAX && y > -1 && y < Tag.SIZE_MAX)
            {
                if (gameBoard[y][x].isFree())
                    diagonalPositions.add(gameBoard[y][x]);
                else
                {
                    if (gameBoard[y][x].getPiece().getSide() != this.getSide())
                        diagonalPositions.add(gameBoard[y][x]);
                    break;
                }
                x += shift[0];
                y += shift[1];
            }
        }
        return diagonalPositions;
    }

    /**
     * abstract methods to return all legal moves from current position of piece
     * @param gameBoard - board to checl
     * @return - all legal moves from current postion on baord
     */
    public abstract List<Position> getLegalMoves(Position[][] gameBoard);
    
    public String name() { 
        return "(_)";
    }

    //will be overridden in king, rook, and pawn
    public boolean getMoved() {
        return true;
    }

    public void setMoved() {

    }
}