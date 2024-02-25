package com.furglitch.windows_setup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.ini4j.Ini;

public class Config {

    Ini chocoIni, webdlIni, steamIni;

    List<chocoData> chocoPackages = new ArrayList<>();
    List<urlData> urlLinks = new ArrayList<>();
    List<gridData> steamGridIDs = new ArrayList<>();

    public boolean checkChoco() {
        String path = "choco-install.ini";

        try { chocoIni = new Ini(new File(".\\config\\" + path)); return true; }
        catch (IOException e) {
            if (e.getMessage().contains("parse") && e.getMessage().contains(": [")) {
                int ln = getErrorLine(e.getMessage());
                App.out("Bad Section on line " + ln + " in " + path + "", "Config Check Error", "Error");
            }
            else if (e.getMessage().contains("parse")) App.out("Unable to parse " + path , "Config Check Error" , "Error");
            else {
                App.out(path + " not detected!", "Config Error" , "Error");
                return getIni(1);
            }
            return false;
        }
    }

    public boolean checkWebdl() {
        String path = "web-install.ini";

        try { webdlIni = new Ini(new File(".\\config\\" + path)); return true; }
        catch (IOException e) {
            if (e.getMessage().contains("parse") && e.getMessage().contains(": [")) {
                int ln = getErrorLine(e.getMessage());
                App.out("Bad Section on line " + ln + " in " + path + "", "Config Check Error", "Error");
            }
            else if (e.getMessage().contains("parse")) App.out("Unable to parse " + path , "Config Check Error" , "Error");
            else {
                App.out(path + " not detected!", "Config Error" , "Error");
                return getIni(2);
            }
            return false;
        }
    }

    public boolean checkSgdb() {
        String path = "steam-grids.ini";

        try { steamIni = new Ini(new File(".\\config\\" + path)); return true; }
        catch (IOException e) {
            if (e.getMessage().contains("parse") && e.getMessage().contains(": [")) {
                int ln = getErrorLine(e.getMessage());
                App.out("Bad Section on line " + ln + " in " + path + "", "Config Check Error", "Error");
            }
            else if (e.getMessage().contains("parse")) App.out("Unable to parse " + path , "Config Check Error" , "Error");
            else {
                App.out(path + " not detected!", "Config Error" , "Error");
                return getIni(3);
            }
            return false;
        }
    }

    public List<chocoData> parseChoco(boolean check) {
        if (check) {
            chocoPackages.clear();

            for (String sectionName : App.ini.chocoIni.keySet()) {
                Ini.Section section = App.ini.chocoIni.get(sectionName);
                String name = sectionName;
                String pkg = section.get("pkg").trim();
                    if (pkg != null && pkg.isEmpty()) pkg = null;
                String ver = section.get("ver");
                    if (ver != null && ver.isEmpty()) ver = null;
                String arg = section.get("arg");
                    if (arg != null && arg.isEmpty()) arg = null;

                if (pkg != null) { chocoPackages.add(new chocoData(name, pkg, ver, arg)); }
                else { App.out("Entry for " + name + "does not contain a package!", "Parse Error", "Error"); }
            }
            return chocoPackages;
        }
        else return null;
    }

    public List<urlData> parseURL(boolean check) {
        if (check) {
            urlLinks.clear();

            for (String sectionName : App.ini.webdlIni.keySet()) {
                Ini.Section section = App.ini.webdlIni.get(sectionName);
                String name = sectionName;
                String url = section.get("url").trim();
                    if (url != null && url.isEmpty()) url = null;
                String ver = section.get("ver");
                    if (ver != null && ver.isEmpty()) ver = null;
                String arg = section.get("arg");
                    if (arg != null && arg.isEmpty()) arg = null;

                if (url != null) urlLinks.add(new urlData(name, url, ver, arg));
                else App.out("Entry for " + name + "does not contain a URL!", "Parse Error", "Error");
            }
            return urlLinks;
        }
        else return null;
    }

    public List<gridData> parseSG(boolean check) {
        if (check) {
            steamGridIDs.clear();

            App.sgdb.getSGDBKey();

            for (String sectionName : App.ini.steamIni.keySet()) {
                Ini.Section section = App.ini.steamIni.get(sectionName);
                String name = sectionName;
                String id = section.get("id");
                    if (id != null && id.isEmpty()) id = null;
                    else if (id != null) id = id.trim();
                String grid = section.get("grid");
                    if (grid != null && grid.isEmpty()) grid = null;
                    else if (grid != null) grid = grid.trim();
                String hero = section.get("hero");
                    if (hero != null && hero.isEmpty()) hero = null;
                    else if (hero != null) hero = hero.trim();
                String logo = section.get("logo");
                    if (logo != null && logo.isEmpty()) logo = null;
                    else if (logo != null) logo = logo.trim();
                String icon = section.get("icon");
                    if (icon != null && icon.isEmpty()) icon = null;
                    else if (icon != null) icon = icon.trim();

                if (id == null) App.out("Entry for " + name + " in steam-grids.ini does not contain a Steam App ID, or is not 'nonsteam'!\n\nEntry will not be added to data list", "Parse Error", "Error");
                else if (hero == null && grid == null && logo == null && icon == null) App.out("Entry for " + name + " in steam-grids.ini does not contain any SGDB image IDs!\n\nEntry will not be added to data list", "Parse Error", "Error");
                else if (id.equals("nonsteam")) {
                    App.out("Creating SGDB Protocol commands for " + name);
                    if (grid != null && grid.matches("\\d+")) grid = "sgdb://boop/grid/" + grid;
                        else grid = null;
                    if (hero != null && hero.matches("\\d+")) hero = "sgdb://boop/hero/" + hero;
                        else hero = null;
                    if (logo != null && logo.matches("\\d+")) logo = "sgdb://boop/logo/" + logo;
                        else logo = null;
                    if (icon != null && icon.matches("\\d+")) icon = "sgdb://boop/icon/" + icon;
                        else icon = null;
                    steamGridIDs.add(new gridData(name, id, grid, hero, logo, icon));
                } else {
                    App.out("Creating API URLs for " + name);
                    if (grid != null && grid.matches("\\d+")) {
                        grid = "https://www.steamgriddb.com/api/v2/grids/" + grid;
                        grid = App.sgdb.parseApi(grid);
                    } else grid = null;
                    if (hero != null && hero.matches("\\d+")) {
                        hero = "https://www.steamgriddb.com/api/v2/heroes/" + hero;
                        hero = App.sgdb.parseApi(hero);
                    } else hero = null;
                    if (logo != null && logo.matches("\\d+")) {
                        logo = "https://www.steamgriddb.com/api/v2/logos/" + logo;
                        logo = App.sgdb.parseApi(logo);
                    } else logo = null;
                    if (icon != null && icon.matches("\\d+")) {
                        icon = "https://www.steamgriddb.com/api/v2/icons/" + icon;
                        icon = App.sgdb.parseApi(icon);
                    } else icon = null;
                    steamGridIDs.add(new gridData(name, id, grid, hero, logo, icon));
                }
            }

            return steamGridIDs;
        }
        else return null;
    }

    int getErrorLine(String errorMessage) {
        int line = -1;
        try {
            int startIndex = errorMessage.indexOf("line: ") + 6;
            int endIndex = errorMessage.indexOf(")", startIndex);
            String lineNumberStr = errorMessage.substring(startIndex, endIndex);
            line = Integer.parseInt(lineNumberStr);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return line;
    }

    public boolean getIni(int type) {
        int response = -1;
        if (type == 1) { //chocolatey
            response = JOptionPane.showOptionDialog(null, "Get Chocolatey Download Config File?", "INI Grabber", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (response == 0 ) {
                App.download("https://raw.githubusercontent.com/Furglitch/WindowsSetup/main/config/choco-install.ini", ".\\config\\choco-install.ini", "Chocolatey Package .ini Config");
                return checkChoco();
            } else App.out("Chocolatey config download rejected!");
        } else if (type == 2) { //url
            response = JOptionPane.showOptionDialog(null, "Get Software Download (Web) Config File?", "INI Grabber", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (response == 0 ) {
                App.download("https://raw.githubusercontent.com/Furglitch/WindowsSetup/main/config/web-install.ini", ".\\config\\web-install.ini", "Web Software .ini Config");
                return checkWebdl();
            } else App.out("WebDL config download rejected!");
        } else if (type == 3) { //steam
            response = JOptionPane.showOptionDialog(null, "Get Steam Grid Config File?", "INI Grabber", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (response == 0 ) {
                App.download("https://raw.githubusercontent.com/Furglitch/WindowsSetup/main/config/steam-grids.ini", ".\\config\\steam-grids.ini", "Steam Grid .ini Config");
                return checkSgdb();
            } else App.out("Steam Grid config download rejected!");
        } else App.out("Invalid input type! Given: " + type, "INI Grabber Error", "Error");
        return false;

    }

    public class chocoData {
        private String name;
        private String pkg;
        private String ver;
        private String arg;

        public chocoData(String name, String pkg, String ver, String arg) {
            this.name = name;
            this.pkg = pkg;
            this.ver = ver;
            this.arg = arg;
        }

        public String getName() { return name; }
        public String getPackage() { return pkg; }
        public String getVersion() { return ver; }
        public String getArgs() { return arg; }
    }

    public class urlData {

        private String name;
        private String url;
        private String ver;
        private String arg;

        public urlData(String name, String url, String ver, String arg) {
            this.name = name;
            this.url = url;
            this.ver = ver;
            this.arg = arg;
        }

        public String getName() { return name; }
        public String getURL() { return url; }
        public String getVersion() { return ver; }
        public String getArgs() { return arg; }
    }

    public class gridData {
        private String name;
        private String id;
        private String grid;
        private String hero;
        private String logo;
        private String icon;

        public gridData(String name, String id, String grid, String hero, String logo, String icon) {
            this.name = name;
            this.id = id;
            this.grid = grid;
            this.hero = hero;
            this.logo = logo;
            this.icon = icon;
        }

        public String getName() { return name; }
        public String getID() { return id; }
        public String getGrid() { return grid; }
        public String getHero() { return hero; }
        public String getLogo() { return logo; }
        public String getIcon() { return icon; }
    }

    public void delete(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) { delete(file); }
                else { file.delete(); }
            }
        } else {
            App.out(dir.toString() + " does not exist.");
        }
        dir.delete();
    }
    
}
