package BoardComponents;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;

import Information.Tag;
import Information.Tag.Side;
import Pieces.Bishop;
import Pieces.Knight;
import Pieces.Queen;
import Pieces.Rook;

//this class is similar to Board except it has a 1x4 board instead of 8x8, used to display and select the piece to upgrade a pawn to
public class Promotion extends JPanel implements MouseListener {
    private Board currentBoard;
    private JFrame frame;
    private static final Dimension FRA_DIMENSION = new Dimension((Tag.IMAGE_WIDTH + 10) * 10, (Tag.IMAGE_HEIGHT + 10) * 10);
    Position[][] pieces;
    public Promotion(Side side, Board board, String playerName, int colorSet)
    {
        currentBoard = board;
        setLayout(new GridLayout(1, 4, 0, 0));
        this.setPanelDimensions(FRA_DIMENSION);
        pieces = new Position[1][4];
        for (int i = 0; i < 4; i++)
        {
            pieces[0][i] = new Position(i, 0, false, 0, colorSet);
            this.add(pieces[0][i]);
        }
        if (side == Side.WHITE)
            initializeWhite();
        else
            initializeBlack();
        this.addMouseListener(this);
        frame = new JFrame("Promotion");
        JPanel panel = new JPanel();
        panel.setBackground(Tag.ColorChoice[colorSet][0]);
        JLabel instructions = new JLabel(playerName + ", please select a piece your pawn to promote to");
        instructions.setForeground(Tag.ColorChoice[colorSet][9]);
        panel.add(instructions);
        frame.add(panel, BorderLayout.NORTH);
        frame.add(this, BorderLayout.CENTER);
        frame.setSize(400, 150);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        //have to handle promotion jframe getting closed, if it is closed without selecting piece the game is permanently paused
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                currentBoard.promote("(Q)"); //default to queen if window is closed
                frame.dispose();
            }
        });
        frame.setVisible(true);

    }

    private void initializeWhite()
    {
        pieces[0][0].setPiece(new Queen(Side.WHITE, pieces[0][0], Tag.WHITE_QUEEN));
        pieces[0][1].setPiece(new Rook(Side.WHITE, pieces[0][1], Tag.WHITE_ROOK));
        pieces[0][2].setPiece(new Knight(Side.WHITE, pieces[0][2], Tag.WHITE_KNIGHT));
        pieces[0][3].setPiece(new Bishop(Side.WHITE, pieces[0][3], Tag.WHITE_BISHOP));
    }

    private void initializeBlack()
    {
        pieces[0][0].setPiece(new Queen(Side.BLACK, pieces[0][0], Tag.BLACK_QUEEN));
        pieces[0][1].setPiece(new Rook(Side.BLACK, pieces[0][1], Tag.BLACK_ROOK));
        pieces[0][2].setPiece(new Knight(Side.BLACK, pieces[0][2], Tag.BLACK_KNIGHT));
        pieces[0][3].setPiece(new Bishop(Side.BLACK, pieces[0][3], Tag.BLACK_BISHOP));
    }

    private void setPanelDimensions(Dimension size){
        System.out.println("Setting dimensions: " + size.getWidth() + ", " + size.getHeight());
        this.setPreferredSize(size);
        this.setMaximumSize(size);
        this.setMinimumSize(size);
        this.setSize(size);
        System.out.println("Set: " + this.getSize());
    }

    @Override
    public void mouseClicked(MouseEvent e) {        
        Position clickedPosition = (Position) this.getComponentAt(new Point(e.getX(), e.getY()));
        if (clickedPosition.getPiece() != null)
        {
            currentBoard.promote(clickedPosition.getPiece().name());
            closePromotion();
        }
    }
    
    //public so that it can be called and frame is closed in case user tries to save before selecting promotion piece
    public void closePromotion()
    {
        frame.dispose();
    }

    /**
     * since the promotion implements MouseListner, 
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