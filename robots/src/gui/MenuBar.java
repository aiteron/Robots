package gui;

import log.LogChangeListener;
import log.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

public class MenuBar extends JMenuBar {
    private JFrame parentObj;
    private final ResourceBundle localization;
    private final Properties config;

    public MenuBar(JFrame par, ResourceBundle localization, Properties configuration)
    {
        parentObj = par;
        this.localization = localization;
        config = configuration;

        add(createMenuCategory_LookAndFeel());
        add(createMenuCategory_Test());
        add(createMenuCategory_Tools());
        add(createMenuCategory_Language());
    }

    private JMenu createMenuCategory(String name, int key, String descriprion)
    {
        JMenu menu = new JMenu(name);
        menu.setMnemonic(key);
        menu.getAccessibleContext().setAccessibleDescription(descriprion);
        return menu;
    }

    private JMenuItem createMenuItem(String name, int key, ActionListener listener)
    {
        JMenuItem menuItem = new JMenuItem(name, key);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    private void setLookAndFeel(String className)
    {
        try
        {
            UIManager.setLookAndFeel(className);
            SwingUtilities.updateComponentTreeUI(parentObj);
        }
        catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e)
        {
            // just ignore
        }
    }

    private JMenu createMenuCategory_LookAndFeel()
    {
        JMenu lookAndFeelMenu = createMenuCategory(localization.getString("displayMode"), KeyEvent.VK_V, localization.getString("displayModeDesc"));

        JMenuItem systemLook = createMenuItem(localization.getString("systemScheme"), KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLook);

        JMenuItem universalLook = createMenuItem(localization.getString("universalScheme"), KeyEvent.VK_S,(event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(universalLook);

        return lookAndFeelMenu;
    }

    private JMenu createMenuCategory_Test()
    {
        JMenu testMenu = createMenuCategory(localization.getString("tests"), KeyEvent.VK_T, localization.getString("testsDesc"));

        JMenuItem logMessage = createMenuItem(localization.getString("logMessage"), KeyEvent.VK_S, (event) -> {
            Logger.debug(localization.getString("newString"));
        });
        testMenu.add(logMessage);

        return testMenu;
    }

    private JMenu createMenuCategory_Tools()
    {
        JMenu toolMenu = createMenuCategory(localization.getString("menuBarTools"), KeyEvent.VK_I, localization.getString("menuBarTools"));

        // Close window with confirm window
        JMenuItem exitItem = createMenuItem(localization.getString("menuItemExit"), KeyEvent.VK_E, (event) -> {
            Object[] options = { localization.getString("closeWindowYes"), localization.getString("closeWindowNo") };
            if (JOptionPane.showOptionDialog(this.getParent(),
                    localization.getString("closeWindowQuestion"), localization.getString("closeWindowTitle"),
                    0,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, null) == 0)
            {
                System.exit(0);
            }
        });

        toolMenu.add(exitItem);

        return toolMenu;
    }

    private JMenu createMenuCategory_Language()
    {
        JMenu langMenu = createMenuCategory(localization.getString("menuBarLang"), KeyEvent.VK_L, localization.getString("menuBarLang"));

        JMenuItem rusLangItem = createMenuItem(localization.getString("menuItemLangRus"), KeyEvent.VK_R, (event) -> {
            try {
                config.setProperty("lang", "ru");
                config.setProperty("country", "RU");
                config.store(new FileOutputStream("src/resources/config.properties"), null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this.getParent(), localization.getString("ChangeLangInfo"),"",JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem engLangItem = createMenuItem(localization.getString("menuItemLangEng"), KeyEvent.VK_E, (event) -> {
            try {
                config.setProperty("lang", "en");
                config.setProperty("country", "US");
                config.store(new FileOutputStream("src/resources/config.properties"), null);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(this.getParent(), localization.getString("ChangeLangInfo"),"",JOptionPane.INFORMATION_MESSAGE);
        });

        langMenu.add(rusLangItem);
        langMenu.add(engLangItem);

        return langMenu;
    }
}
