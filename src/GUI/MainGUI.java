package GUI;

import java.awt.Font;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.BorderFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Information.Tag;
import BoardComponents.Position;
import SpeechRecognizer.SpeechRecognizerMain;

public class MainGUI implements Runnable {
    private SpeechRecognizerMain speech = new SpeechRecognizerMain();
    private static final int VERTICAL_SPACE = 50;
    private static final int COLUMN_SPACE = 10;
    private int colorSet;

    private JFrame mainGUI;
    private JPanel gameTitlePanel;
    private JPanel playerPanel;
    private JPanel blackPlayerPanel;
    private JPanel whitePlayerPanel;
    private JPanel buttons;
    private JTextField blackPlayerTextField;
    private JTextField whitePlayerTextField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MainGUI());
    }
    
    public void run() {
        assignColorSet();
        initializeMainMenu();
        mainGUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainGUI.setVisible(true);
    }

    private void assignColorSet() {
        try {
            File saveFile = new File("./savedgames/Settings.txt");
            Scanner myReader = new Scanner(saveFile);
            String savedColorSet = myReader.nextLine();
            colorSet = Integer.valueOf(savedColorSet);
            myReader.close();
        
      } catch (FileNotFoundException error) {
        System.out.println("No settings found");
        error.printStackTrace();
      }
    }

    private void initializeMainMenu() {
        createFrame();
        addGameTitle();
        addPlayerFields();
        addPlayerTextField();
        addButtons();
    }

    private void createFrame() {
        mainGUI = new JFrame(Tag.TITLE);
        mainGUI.setIconImage(new ImageIcon(Tag.LAZY_ICON).getImage());
        mainGUI.setSize(Tag.IMAGE_WIDTH * 8, Tag.IMAGE_HEIGHT * 8);
        mainGUI.setResizable(false);
        mainGUI.setBackground(Tag.ColorChoice[1][6]);
        mainGUI.setLocationRelativeTo(null);
    }

    private void addGameTitle() {
        gameTitlePanel = new JPanel();
        JLabel title = new JLabel(Tag.TITLE);
        title.setFont(new Font("Monospaced", Font.BOLD, 35));
        title.setForeground(Tag.ColorChoice[1][9]);
        title.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.setBackground(Tag.ColorChoice[1][6]);
        gameTitlePanel.setPreferredSize(new Dimension(600, 200));
        gameTitlePanel.add(title);
        mainGUI.add(gameTitlePanel, BorderLayout.NORTH);
    }

    private void addPlayerFields() {
        final JLabel whiteIcon = new JLabel(new ImageIcon((Tag.WHITE_KING)));
        final JLabel blackIcon = new JLabel(new ImageIcon((Tag.BLACK_KING)));
        // create new panel for player one
        whitePlayerPanel = new JPanel();
        whitePlayerPanel.add(whiteIcon);
        whitePlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        // create new panel for player two
        blackPlayerPanel = new JPanel();
        blackPlayerPanel.add(blackIcon);
        blackPlayerPanel.setBackground(Tag.ColorChoice[1][6]);
        //create panel that holds both player panels
        playerPanel = new JPanel();
        playerPanel.setBackground(Tag.ColorChoice[1][6]);
        //third, empty panel to leave more space between player panel and buttons
        JPanel buttonSpacer = new JPanel();
        buttonSpacer.setBackground(Tag.ColorChoice[1][6]);
        playerPanel.setLayout(new GridLayout(3, 1, 0, 0));
        playerPanel.add(whitePlayerPanel);
        playerPanel.add(blackPlayerPanel);
        playerPanel.add(buttonSpacer);
        //add panel holding both to frame
        mainGUI.add(playerPanel, BorderLayout.CENTER);
    }

    private void addPlayerTextField() {
        blackPlayerTextField = new JTextField();
        whitePlayerTextField = new JTextField();
        blackPlayerPanel.add(blackPlayerTextField);
        whitePlayerPanel.add(whitePlayerTextField);
        blackPlayerTextField.setToolTipText("Enter Player 2 Name Here");
        whitePlayerTextField.setToolTipText("Enter Player 1 Name Here");
        blackPlayerTextField.setColumns(COLUMN_SPACE);
        whitePlayerTextField.setColumns(COLUMN_SPACE);
    }

    private void addButtons() {
        buttons = new JPanel();
        buttons.setBackground(Tag.ColorChoice[1][6]);
        buttons.setLayout(new GridLayout(1, 5, 25, 10));
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setBackground(Tag.ColorChoice[1][6]);
        buttonWrapper.setPreferredSize(new Dimension(600, 120));
        final JButton play = new JButton("Play");
        final JButton load = new JButton("Load");
        final JButton settings = new JButton("Settings");
        final JButton help = new JButton("Help");
        final JButton quit = new JButton("Quit");
        play.setBackground(Tag.ColorChoice[1][7]);
        load.setBackground(Tag.ColorChoice[1][7]);
        settings.setBackground(Tag.ColorChoice[1][7]);
        help.setBackground(Tag.ColorChoice[1][7]);
        quit.setBackground(Tag.ColorChoice[1][7]);
        play.addActionListener(e -> playItemActionPerformed(e));
        load.addActionListener(e -> loadItemActionPerformed(e));
        settings.addActionListener(e -> settingsItemActionPerformed(e));
        help.addActionListener(e -> helpItemActionPerformed(e));
        quit.addActionListener(e -> quitItemActionPerformed(e));
        buttons.add(play);
        buttons.add(load);
        buttons.add(settings);
        buttons.add(help);
        buttons.add(quit);
        buttonWrapper.add(buttons);
        mainGUI.add(buttonWrapper, BorderLayout.SOUTH);
    }

    public void mainMenu()
    {
        mainGUI.setVisible(true);
    }

    private void playItemActionPerformed(ActionEvent e) {
        new GameGUI(this, speech, whitePlayerTextField.getText(), blackPlayerTextField.getText(), colorSet);
        mainGUI.setVisible(false);
    }

    private void loadItemActionPerformed(ActionEvent e) {
        try {
                File saveFile = new File("./savedgames/Chess.txt");
                Scanner myReader = new Scanner(saveFile);
                String savedGame = myReader.nextLine();
                String[] pieces = savedGame.split(" "); //list of words separated by spaces
                myReader.close();
                new GameGUI(this, pieces, speech, pieces[0], pieces[1], Integer.valueOf(pieces[2]));
                mainGUI.setVisible(false);
            
          } catch (FileNotFoundException error) {
            System.out.println("No save found");
            error.printStackTrace();
          }
    }

    private void helpItemActionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(mainGUI,
        "Enter names for players or they will default to white and black\n" +
        "Left click or press the speak button and say the name of the square to select\n" +
        "Because the speech recognizer can mishear you, please say one square at a time\n" +
        "For example, say 'alpha two' to select and then 'alpha four' to move that piece\n" +
        "Right click or press the speak button and say 'clear' to unselect a piece\n" +
        "Press play to start a new game or load to load a previously saved game",
        "Help Menu", JOptionPane.INFORMATION_MESSAGE);
    }

    private void quitItemActionPerformed(java.awt.event.ActionEvent e) {
        int quit = JOptionPane.showConfirmDialog(mainGUI, "Are you sure you want to quit?", "Quit", JOptionPane.OK_CANCEL_OPTION);
        if(quit == JOptionPane.OK_OPTION) 
        {
            mainGUI.dispose();
            exit();
        }
    }
    public void exit() {
        speech.stopSpeechRecognizerThread();
        mainGUI.dispatchEvent(new WindowEvent(mainGUI, WindowEvent.WINDOW_CLOSING));
    }

    private void settingsItemActionPerformed(ActionEvent e) {
        JFrame settings = new JFrame("Settings");
        settings.setSize(300, 450);
        settings.setLocationRelativeTo(mainGUI);
        JPanel instructions = new JPanel();
        JTextArea text = new JTextArea("Please select your preferred\n   color and press apply");
        text.setBackground(Tag.ColorChoice[1][6]);
        text.setForeground(Tag.ColorChoice[1][9]);
        text.setFont(new Font("Monospaced", Font.BOLD, 14));
        instructions.add(text);
        instructions.setBackground(Tag.ColorChoice[1][6]);
        instructions.setPreferredSize(new Dimension(300, 50));
        settings.add(instructions, BorderLayout.NORTH);
        //main panel holds buttons and demos of board colors
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Tag.ColorChoice[1][6]);
        mainPanel.setPreferredSize(new Dimension(300, 300));
        mainPanel.setLayout(null);
        JRadioButton colorSetOne = new JRadioButton();
        colorSetOne.setBackground(Tag.ColorChoice[1][6]);
        JRadioButton colorSetTwo = new JRadioButton();
        colorSetTwo.setBackground(Tag.ColorChoice[1][6]);
        JRadioButton colorSetThree = new JRadioButton();
        colorSetThree.setBackground(Tag.ColorChoice[1][6]);
        ButtonGroup bg = new ButtonGroup();
        bg.add(colorSetOne);
        bg.add(colorSetTwo);
        bg.add(colorSetThree);
        mainPanel.add(colorSetOne);
        mainPanel.add(colorSetTwo);
        mainPanel.add(colorSetThree);
        if (colorSet == 0)
            colorSetOne.setSelected(true);
        else if (colorSet == 1)
            colorSetTwo.setSelected(true);
        else //colorSetThree
            colorSetThree.setSelected(true);
        colorSetOne.setBounds(100, 50, 50, 20);
        colorSetTwo.setBounds(100, 125, 50, 20);
        colorSetThree.setBounds(100, 200, 50, 20);
        for (int i = 0; i < Tag.ColorChoice.length; i++)
        {
            JPanel displayBoard = new JPanel();
            displayBoard.setLayout(new GridLayout(2, 2, 0, 0));
            displayBoard.add(new Position(0, 6, false, 10, i));
            displayBoard.add(new Position(1, 6, true, 10, i));
            displayBoard.add(new Position(0, 7, true, 10, i));
            displayBoard.add(new Position(1, 7, false, 10, i));
            displayBoard.setPreferredSize(new Dimension(50, 50));
            displayBoard.setBorder(BorderFactory.createLineBorder(Tag.ColorChoice[i][7]));
            mainPanel.add(displayBoard);
            displayBoard.setBounds(30, 35 + (i * 75), 50,50); //shifts each down to corresponding buttons
        }
        JButton apply = new JButton("Apply");
        apply.setBackground(Tag.ColorChoice[1][7]);
        JPanel buttonWrapper = new JPanel();
        buttonWrapper.setPreferredSize(new Dimension(300, 50));
        buttonWrapper.setBackground(Tag.ColorChoice[1][6]);
        buttonWrapper.add(apply);
        settings.add(mainPanel);
        settings.add(buttonWrapper, BorderLayout.SOUTH);
        settings.setResizable(false);
        settings.setVisible(true);
        apply.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (colorSetOne.isSelected())
                    colorSet = 0;
                else if (colorSetTwo.isSelected())
                    colorSet = 1;
                else //colorSetThree
                    colorSet = 2;
                try {
                    FileWriter writer = new FileWriter("./savedgames/Settings.txt", false);
                    writer.write(String.valueOf(colorSet));
                    writer.close();
                }
                catch (Exception error) {
                    error.getStackTrace();
                }
                settings.dispose();
            }
        });
        settings.addWindowFocusListener( new WindowFocusListener() {
            //implemented because settings window would otherwise stay open when you do anything else with the main menu, including starting a game
            @Override
            public void windowLostFocus(WindowEvent event) {
                settings.dispose();
            }
            //has to be overwritten for windowFocusListener, not used so left empty
            @Override
            public void windowGainedFocus(WindowEvent event) { }
        });
    }
}