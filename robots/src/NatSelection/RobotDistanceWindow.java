package NatSelection;

import gui.Config;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

public class RobotDistanceWindow  extends JInternalFrame implements Observer
{
    private final Config config;
    private final NSWindow gameWindow;
    private String distanceTitle;
    JLabel robotCoordsValuesLabel;

    public RobotDistanceWindow(Config config, NSWindow gameWindow)
    {
        super(config.getLocalization("NSDistanceWindow"), true, true, true, true);
        this.config = config;
        this.gameWindow = gameWindow;
        distanceTitle = config.getLocalization("NSDistance");

        JLabel robotCoordsTitleLabel = new JLabel(config.getLocalization("NSdistanceToFood"));
        robotCoordsValuesLabel  = new JLabel("");

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
        if(event.type == EventType.Distance)
        {
            var dist = (double) event.data;
            robotCoordsValuesLabel.setText(distanceTitle + " " + (int)dist);
        }
    }
}