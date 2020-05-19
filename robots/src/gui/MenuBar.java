package gui;

import log.Logger;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class MenuBar extends JMenuBar {
    private final JFrame parentObj;
    private final Config config;

    public MenuBar(JFrame par, Config configuration)
    {
        parentObj = par;
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
        JMenu lookAndFeelMenu = createMenuCategory(config.getLocalization("displayMode"), KeyEvent.VK_V, config.getLocalization("displayModeDesc"));

        JMenuItem systemLook = createMenuItem(config.getLocalization("systemScheme"), KeyEvent.VK_S, (event) -> {
            setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(systemLook);

        JMenuItem universalLook = createMenuItem(config.getLocalization("universalScheme"), KeyEvent.VK_S,(event) -> {
            setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            this.invalidate();
        });
        lookAndFeelMenu.add(universalLook);

        return lookAndFeelMenu;
    }

    private JMenu createMenuCategory_Test()
    {
        JMenu testMenu = createMenuCategory(config.getLocalization("tests"), KeyEvent.VK_T, config.getLocalization("testsDesc"));

        JMenuItem logMessage = createMenuItem(config.getLocalization("logMessage"), KeyEvent.VK_S, (event) -> {
            Logger.debug(config.getLocalization("newString"));
        });
        testMenu.add(logMessage);

        return testMenu;
    }

    private JMenu createMenuCategory_Tools()
    {
        JMenu toolMenu = createMenuCategory(config.getLocalization("menuBarTools"), KeyEvent.VK_I, config.getLocalization("menuBarTools"));

        // Close window with confirm window
        JMenuItem exitItem = createMenuItem(config.getLocalization("menuItemExit"), KeyEvent.VK_E, (event) -> {
            Object[] options = { config.getLocalization("yes"), config.getLocalization("no") };
            if (JOptionPane.showOptionDialog(this.getParent(),
                    config.getLocalization("closeWindowQuestion"), config.getLocalization("closeWindowTitle"),
                    JOptionPane.YES_NO_OPTION,
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
        JMenu langMenu = createMenuCategory(config.getLocalization("menuBarLang"), KeyEvent.VK_L, config.getLocalization("menuBarLang"));

        // Close window with confirm window
        JMenuItem rusLangItem = createMenuItem(config.getLocalization("menuItemLangRus"), KeyEvent.VK_R, (event) -> {
            config.setLocale("ru", "RU");
            JOptionPane.showMessageDialog(this.getParent(), config.getLocalization("changeLangInfo"),"",JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem engLangItem = createMenuItem(config.getLocalization("menuItemLangEng"), KeyEvent.VK_E, (event) -> {
            config.setLocale("en", "US");
            JOptionPane.showMessageDialog(this.getParent(), config.getLocalization("changeLangInfo"),"",JOptionPane.INFORMATION_MESSAGE);
        });

        langMenu.add(rusLangItem);
        langMenu.add(engLangItem);

        return langMenu;
    }
}
