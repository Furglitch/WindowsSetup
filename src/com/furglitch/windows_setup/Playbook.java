package com.furglitch.windows_setup;

import java.io.File;

import javax.swing.JOptionPane;

public class Playbook {
    
    public void downloadAME() {
        App.download("https://download.ameliorated.io/AME%20Wizard%20Beta.zip", ".\\downloads\\AME Wizard Beta.zip", "AME Wizard");
        App.unzip(".\\downloads\\AME Wizard Beta.zip", ".\\downloads");
    }

    public void runPlaybook() {
        if (!new File(".\\downloads\\AME Wizard Beta.exe").exists()) downloadAME();
        
        String[] options = {"AtlasOS", "ReviOS", "Cancel"};
        int pb = JOptionPane.showOptionDialog(null, "Which Playbook to Install?", "Choose AME Playbook", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (pb == 0) { //AtlasOS
            App.download("https://github.com/Atlas-OS/Atlas/releases/download/0.3.2/AtlasPlaybook_v0.3.2.apbx", ".\\downloads\\AtlasPlaybook_v0.3.2.apbx", "AtlasOS Playbook v0.3.2");
        }
        else if (pb == 1) { // ReviOS
            App.download("https://github.com/meetrevision/playbook/releases/download/23.12/Revi-PB-23.12.apbx", ".\\downloads\\Revi-PB-23.12.apbx", "ReviOS Playbook v23.12");
        }
        else return;
        App.out("Playbook is latest as of release.\nDoes not install latest github release, still troubleshooting the code.");
        
        JOptionPane.showMessageDialog(null, "Playbook downloaded!.\n\nAutomated install currently not supported as there's no documentation on the CLI version of AME.\n\nRunning the AME Wizard.");
        App.execute(".\\downloads\\AME Wizard Beta.exe");
    }
}
