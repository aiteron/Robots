package NatSelection;

import java.awt.BorderLayout;
import java.util.ResourceBundle;


import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class NSWindow extends JInternalFrame
{
    private final NSMap m_visualizer;
    private final ResourceBundle localization;
    public NSWindow(ResourceBundle localization)
    {
        super(localization.getString("NSWindow"), true, true, true, true);
        this.localization = localization;




        m_visualizer = new NSMap();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(m_visualizer, BorderLayout.CENTER);
        getContentPane().add(panel);


        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //TODO добавить удаление map при закрытие окна.

        // Confirm close window
        addInternalFrameListener(new InternalFrameAdapter(){
            public void internalFrameClosing(InternalFrameEvent e) {
                Object[] options = { localization.getString("closeWindowYes"), localization.getString("closeWindowNo") };
                if (JOptionPane.showOptionDialog(e.getInternalFrame(),
                        localization.getString("closeWindowQuestion"), localization.getString("closeWindowTitle"),
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
        m_visualizer.setCountFood(i);
    }

    public void setCountMobs(int i) {
        m_visualizer.setCountMobs(i);
    }

    public void setCountIterations(int i) {
        m_visualizer.setCountIterations(i);
    }

    public void startSimulation() {
        m_visualizer.startSimulation();
    }
}

