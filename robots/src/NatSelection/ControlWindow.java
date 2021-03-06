package NatSelection;

import gui.Config;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControlWindow extends JInternalFrame
{
    private final Config config;
    public ControlWindow(Config config, NSWindow gameWindow)
    {
        super(config.getLocalization("NSControlWindow"), true, true, true, true);
        this.config = config;

        JTextField foodCountTF = new JTextField();
        JTextField mobsCountTF = new JTextField();
        JTextField iterationsCountTF = new JTextField();

        ((PlainDocument) foodCountTF.getDocument()).setDocumentFilter(new MyIntFilter());
        ((PlainDocument) mobsCountTF.getDocument()).setDocumentFilter(new MyIntFilter());
        ((PlainDocument) iterationsCountTF.getDocument()).setDocumentFilter(new MyIntFilter());

        JLabel foodCountLabel = new JLabel(config.getLocalization("foodCountLabel"));
        JLabel mobsCountLabel  = new JLabel(config.getLocalization("mobsCountLabel"));
        JLabel iterationsCountLabel  = new JLabel(config.getLocalization("iterationsCountLabel"));

        JButton startButton = new JButton(config.getLocalization("StartButtonLabel"));
        startButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(foodCountTF.getText().equals("") || mobsCountTF.getText().equals("") || iterationsCountTF.getText().equals(""))
                {
                    JOptionPane.showMessageDialog((Component) e.getSource(),
                            config.getLocalization("ErrorMessage_EmptyTextField"));
                }
                else
                {
                    gameWindow.setCountFood( Integer.parseInt(foodCountTF.getText()));
                    gameWindow.setCountMobs(Integer.parseInt(mobsCountTF.getText()));
                    gameWindow.setCountIterations(Integer.parseInt(iterationsCountTF.getText()));
                    gameWindow.startSimulation();
                }
            }
        });


        JButton updateButton = new JButton(config.getLocalization("UpdateButtonLabel"));
        updateButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(foodCountTF.getText().equals("") || mobsCountTF.getText().equals("") || iterationsCountTF.getText().equals(""))
                {
                    JOptionPane.showMessageDialog((Component) e.getSource(),
                            config.getLocalization("ErrorMessage_EmptyTextField"));
                }
                else
                {
                    gameWindow.setCountFood( Integer.parseInt(foodCountTF.getText()));
                }
            }
        });


// Define the panel to hold the buttons
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        panel.add(foodCountTF);
        panel.add(foodCountLabel);
        panel.add(mobsCountTF);
        panel.add(mobsCountLabel);
        panel.add(iterationsCountTF);
        panel.add(iterationsCountLabel);
        panel.add(startButton);
        panel.add(updateButton);
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
}


class MyIntFilter extends DocumentFilter {
    @Override
    public void insertString(FilterBypass fb, int offset, String string,
                             AttributeSet attr) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.insert(offset, string);

        if (test(sb.toString())) {
            super.insertString(fb, offset, string, attr);
        } else {
            // warn the user and don't allow the insert
        }
    }

    private boolean test(String text) {
        if (text.isEmpty())
            return true;
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text,
                        AttributeSet attrs) throws BadLocationException {

        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.replace(offset, offset + length, text);

        if (test(sb.toString())) {
            super.replace(fb, offset, length, text, attrs);
        } else {
            // warn the user and don't allow the insert
        }

    }

    @Override
    public void remove(FilterBypass fb, int offset, int length)
            throws BadLocationException {
        Document doc = fb.getDocument();
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getText(0, doc.getLength()));
        sb.delete(offset, offset + length);

        if (test(sb.toString())) {
            super.remove(fb, offset, length);
        } else {
            // warn the user and don't allow the insert
        }

    }
}