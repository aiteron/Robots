package NatSelection;

import NatSelection.NSWindow;
import NatSelection.Pair;
import gui.Config;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;


public class RobotCoordsWindow extends JInternalFrame implements Observer
{
    private final Config config;
    private final NSWindow gameWindow;
    JLabel robotCoordsValuesLabel;

    public RobotCoordsWindow(Config config, NSWindow gameWindow)
    {
        super(config.getLocalization("NSRobotCoordsWindow"), true, true, true, true);
        this.config = config;
        this.gameWindow = gameWindow;

        JLabel robotCoordsTitleLabel = new JLabel(config.getLocalization("NSRobotCoordsTitleLabel"));
        robotCoordsValuesLabel  = new JLabel("x, y");

// Define the panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.add(robotCoordsTitleLabel);
        panel.add(robotCoordsValuesLabel);
        add(panel);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // Confirm close window
        addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                Object[] options = { config.getLocalization("yes"), config.getLocalization("no") };
                if (JOptionPane.showOptionDialog(e.getInternalFrame(),
                        config.getLocalization("closeWindowQuestion"), config.getLocalization("closeWindowTitle"),
                        0,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, null) == 0)
                {
                    e.getInternalFrame().getDesktopPane().getDesktopManager().closeFrame(e.getInternalFrame());
                }
            }
        });

        pack();
    }

    @Override
    public void update(Observable o, Object arg) {
        var event = (Event)arg;
        if(event.type == EventType.Coords)
        {
            var coords = (Pair<Integer, Integer>) event.data;
            robotCoordsValuesLabel.setText("x = " + coords.getFirst() + ", y = " + coords.getSecond());
        }
    }
}