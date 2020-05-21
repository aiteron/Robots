package NatSelection;

import gui.Config;
import gui.MainApplicationFrame;

import java.awt.BorderLayout;
import java.util.Observer;
import java.util.ResourceBundle;


import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class NSWindow extends JInternalFrame
{
    private final NSMap visualizer;
    private final Config config;
    public NSWindow(Config config)
    {
        super(config.getLocalization("NSWindow"), true, true, true, true);
        this.config = config;

        visualizer = new NSMap(this);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //TODO добавить удаление map при закрытие окна.

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

    public void setCountFood(int i) {
        visualizer.setFoodGenerateCoeff(i);
    }

    public void setCountMobs(int i) {
        visualizer.setCountMobs(i);
    }

    public void setCountIterations(int i) {
        visualizer.setCountIterations(i);
    }

    public void startSimulation() {
        visualizer.startSimulation();
    }

    public void setMonsterCoordsListener(Observer listener) { visualizer.setMonsterCoordsListener(listener); }

    public void setMonsterDistanceListener(Observer listener)
    {
        visualizer.setMonsterDistanceListener(listener);
    }
}

