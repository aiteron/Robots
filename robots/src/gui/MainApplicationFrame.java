package gui;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.*;

import NatSelection.NSWindow;
import log.Logger;


public class MainApplicationFrame extends JFrame
{
    private final JDesktopPane desktopPane = new JDesktopPane();
    private final ResourceBundle localization;
    private final Properties config;
    
    public MainApplicationFrame() {
        config = getConfiguration();
        localization = getLocalization();

        int inset = 50;        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(inset, inset,
            screenSize.width  - inset*2,
            screenSize.height - inset*2);

        setContentPane(desktopPane);
        

        LogWindow logWindow = createLogWindow();
        addWindow(logWindow);

//        GameWindow gameWindow = new GameWindow(localization);
//        gameWindow.setSize(400,  400);
//        addWindow(gameWindow);

        NSWindow gameWindow = new NSWindow(localization);
        gameWindow.setSize(400,  400);
        addWindow(gameWindow);



        setJMenuBar(new MenuBar(this, localization, config));

        // Confirm close window
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                Object[] options = { localization.getString("closeWindowYes"), localization.getString("closeWindowNo") };
                if (JOptionPane.showOptionDialog((Component) e.getSource(),
                        localization.getString("closeWindowQuestion"), localization.getString("closeWindowTitle"),
                        0,
                        JOptionPane.QUESTION_MESSAGE,
                        null, options, null) == 0)
                {
                    System.exit(0);
                }
            }
        });

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    private Properties getConfiguration() {

        FileInputStream fis;
        Properties property = new Properties();

        try {
            fis = new FileInputStream("src/resources/config.properties");
            property.load(fis);
        } catch (IOException e) {
            System.err.println("Error! Config file not found");
            System.exit(5);
        }

        return property;
    }

    protected ResourceBundle getLocalization()
    {
        ResourceBundle resources;
        resources = ResourceBundle.getBundle("resources/LocalizationResources",
                new Locale(config.getProperty("lang"), config.getProperty("country")));

        return resources;
    }


    protected LogWindow createLogWindow()
    {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource(), localization);
        logWindow.setLocation(10,10);
        logWindow.setSize(300, 800);
        setMinimumSize(logWindow.getSize());
        logWindow.pack();
        Logger.debug(localization.getString("protocolWorking"));
        return logWindow;
    }
    
    protected void addWindow(JInternalFrame frame)
    {
        desktopPane.add(frame);
        frame.setVisible(true);
    }


}
