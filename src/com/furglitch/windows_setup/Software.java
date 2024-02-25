package com.furglitch.windows_setup;

import java.util.List;

import javax.swing.JOptionPane;

import com.furglitch.windows_setup.Config.chocoData;
import com.furglitch.windows_setup.Config.urlData;

public class Software {

    public void choco() {
        List<chocoData> data = App.ini.parseChoco(App.hasChocoIni);

        String exe;
        if (App.hasChocoInit) exe = "choco";
        else exe = "C:\\ProgramData\\chocolatey\\bin\\choco.exe";

        for (chocoData packages : data) {
            String name = packages.getName();
            String pkg = packages.getPackage(), ver = packages.getVersion(), arg = packages.getArgs();

            App.out("Running choco command for " + name);
            if (ver != null && arg != null) App.execute(exe + " install " + pkg + " -y --version " + ver + " " + arg);
            else if (ver != null) App.execute(exe + " install " + pkg + " -y --version " + ver);
            else if (arg != null) App.execute(exe + " install " + pkg + " -y " + arg);
            else App.execute(exe + " install " + pkg + " -y ");
        }
    }

    public void url() {
        List<urlData> data = App.ini.parseURL(App.hasWebdlIni);

        for (urlData packages : data) {
            String name = packages.getName();
            String url = packages.getURL(), ver = packages.getVersion(), arg = packages.getArgs();

            String ext = url.substring(url.lastIndexOf('.') + 1);
            String path = ".\\downloads\\" + name + "." + ext;

            App.download(url, path, name + " v" + ver);
            if (ext.matches("zip")) {
                App.unzip(path, ".\\downloads\\" + name);
                JOptionPane.showMessageDialog(null, "Automatic execution from compressed files currently not implemented!\n\nThe archive for " + name + " has been extracted to " + "'.\\downloads\\" + name + "' and needs to be run manually");
            }
            else if (arg == null) App.execute(path);
            else App.execute(path + " " + arg);
        }
    }
    
}
