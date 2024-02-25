package com.furglitch.windows_setup;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import com.sun.jna.platform.win32.Advapi32Util;
import static com.sun.jna.platform.win32.WinReg.HKEY_CLASSES_ROOT;

import com.formdev.flatlaf.FlatDarculaLaf;

public class Interface implements ActionListener {

    JMenuItem licenseMenu, checkMenu, exitMenu;
    JButton runButton, checkButton;
    JCheckBox[] optionBox;
    JTextArea consoleField;

    public void create() {
        try { UIManager.setLookAndFeel( new FlatDarculaLaf() ); }
        catch ( Exception ex ) { showMessageDialog(null, "Unable to initialize LaF. Library may be missing."); }

        JFrame frame = new JFrame("Furglitch Windows Setup");
        JPanel mainPanel = new JPanel();

        JPanel noAdminPanel = new JPanel();
        JLabel warningLabel = new JLabel("<html>This program requires admin privileges!<br><br>Either use the run.bat found on github,<br>or open CMD as an administrator and run 'java -jar WindowsSetup.jar'</html>");
        noAdminPanel.add(warningLabel);

        JMenuBar menuBar = new JMenuBar();
        JMenu optionsMenu = new JMenu("File");
        checkMenu = new JMenuItem("Run Check"); checkMenu.addActionListener(this);
        licenseMenu = new JMenuItem("License"); licenseMenu.addActionListener(this);
        exitMenu = new JMenuItem("Exit"); exitMenu.addActionListener(this);
        optionsMenu.add(checkMenu);
        optionsMenu.add(licenseMenu);
        optionsMenu.add(exitMenu);
        menuBar.add(optionsMenu);

        JPanel optionsPanel = new JPanel();
        optionBox = new JCheckBox[6];
        for (int b = 0; b < optionBox.length; b++) {
            optionBox[b] = new JCheckBox(""+b); 
            optionBox[b].setEnabled(false); 
            optionBox[b].addActionListener(this); 
            optionsPanel.add(optionBox[b]);
        }
        optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        optionsPanel.setLayout(new GridLayout(3,2));
        
        runButton = new JButton("Run Tasks");
        runButton.addActionListener(this);

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        mainPanel.add(runButton, BorderLayout.SOUTH);

        if (App.isAdmin) { 
            frame.add(mainPanel); 
            frame.setJMenuBar(menuBar);
        } else { frame.add(noAdminPanel); }
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

    }

    public void check() {
        App.out("Checking for presence of programs and configs.");
        App.hasChoco = App.choco.check();
        if (App.init) App.hasChocoInit = App.hasChoco;
        if (App.hasChoco || App.hasChocoInit) App.hasChocoGui = App.choco.checkGui();
        if (App.ini.checkChoco()) App.hasChocoIni = true;
        if (App.ini.checkWebdl()) App.hasWebdlIni = true;
        if (App.ini.checkSgdb()) App.hasSteamIni = true;
        App.sgdb.boopExists = Advapi32Util.registryKeyExists(HKEY_CLASSES_ROOT, "sgdb\\Shell\\Open\\Command");
        if (App.sgdb.boopExists && !App.sgdb.protocolHandler) App.sgdb.protocolHandler();
    }

    public void update() {
        check();
        boolean noIni = true;
        if (App.hasChocoIni || App.hasWebdlIni) noIni = false;
        App.out("Updating UI based on results of check.");
        updateBox(0, App.hasChoco, "Install Chocolatey", "Chocolatey Installed!");
        updateBox(1, App.hasChocoGui, "Install ChocolateyGUI", "ChocolateyGUI Installed!");
        updateBox(2, noIni, "Download Software", "Software .INIs Not Detected!");
        updateBox(3, !App.hasSteamIni, "Download Steam Grids", "'steam-grid.ini' Not Detected!");
        updateBox(4, false, "Install Playbook", "Playbook installation not available");
        updateBox(5, true, "Placeholder", "Placeholder");
    }

    public void updateBox(int boxNum, boolean state, String onMsg, String offMsg) {
        if (!state) optionBox[boxNum].setText(onMsg);
        else optionBox[boxNum].setText(offMsg);
        optionBox[boxNum].setEnabled(!state);
        optionBox[boxNum].setSelected(!state);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == checkMenu) update();
        if (e.getSource() == exitMenu)  System.exit(0);

        if (e.getSource() == licenseMenu) {
            JEditorPane licenseInfo = new JEditorPane("text/html",
                "<html><body>" +
                "FurgltchOS Setup - v1.0.0" + "<br>" + "<br>" +
                "Included libraries: <ul>" + 
                "<li>JFormDesigner/FlatLaf v3.3</li>" + 
                "<li>ini4j v0.5.5</li>" + 
                "<li>stleary/JSON-java v20240205</li>" + 
                "<li>java-native-access/jna v5.14.0</li>" + 
                "</ul><br>" + "<br>" +
                "Utilized and distributed under <a href=\"https://www.apache.org/licenses/LICENSE-2.0\">Apache License 2.0</a>" +
                 "</body></html>"
            );
            licenseInfo.setEditable(false);
            licenseInfo.addHyperlinkListener(new HyperlinkListener() {
                public void hyperlinkUpdate(HyperlinkEvent h) {
                    if (h.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                        try {
                            URI link = new URI(h.getURL().toString());
                            java.awt.Desktop.getDesktop().browse(link);
                        } catch (Exception e) {
                            App.out(e.getMessage(), "License Menu Exception", "Exception");
                        }
                    }
                }
            });
            JOptionPane.showOptionDialog(null, licenseInfo, "License Information", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        }

        if (e.getSource() == runButton) {
            runButton.setEnabled(false);
            if (optionBox[0].isSelected()) App.choco.install();
            if (optionBox[1].isSelected()) App.choco.installGui();
            if (optionBox[2].isSelected()) {
                if (App.hasChocoIni) App.install.choco();
                if (App.hasWebdlIni) App.install.url();
            }
            if (optionBox[3].isSelected()) App.sgdb.getGrids();
            if (optionBox[4].isSelected()) App.ame.runPlaybook();
            //if (optionBox[5].isSelected()) 
            runButton.setEnabled(true);
            update();
        }

        if (e.getSource() == optionBox[0] && optionBox[0].getText().matches("Install Chocolatey") && !optionBox[0].isSelected() ) {
            optionBox[1].setEnabled(false);
            optionBox[1].setSelected(false);
        } else if (e.getSource() == optionBox[0] && optionBox[0].getText().matches("Install Chocolatey") && optionBox[0].isSelected()) {
            optionBox[1].setEnabled(true);
        }
    }

}
