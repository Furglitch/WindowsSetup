package com.furglitch.windows_setup;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.List;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import com.sun.jna.platform.win32.Advapi32Util;
import static com.sun.jna.platform.win32.WinReg.HKEY_LOCAL_MACHINE;

import org.json.JSONObject;

import com.furglitch.windows_setup.Config.gridData;

public class SteamGrid {

    boolean boopExists = false;
    boolean protocolHandler = false;
    String sgdbKey = null;
    String friendCode = null;

    public void protocolHandler() {
        URL.setURLStreamHandlerFactory(protocol -> {
            if ("sgdb".equals(protocol)) {
                return new URLStreamHandler() {
                    protected URLConnection openConnection(URL u) throws IOException {
                        App.out("Opening connection to " + u.toString());
                        throw new UnsupportedOperationException("SGDBoop protocol handler not implemented");
                    }
                };
            }
            return null;
        });
        protocolHandler = true;
    }

    public void getBoop() {
        if (!boopExists) {
            if (!Advapi32Util.registryKeyExists(HKEY_LOCAL_MACHINE, "SOFTWARE\\Wow6432Node\\Microsoft\\VisualStudio\\14.0\\VC\\Runtimes\\x64")) installVCR();
            else App.out("Visual C++ Redist exists!");
            App.download("https://github.com/SteamGridDB/SGDBoop/releases/latest/download/sgdboop-win64.zip", ".\\downloads\\sgdboop-win64.zip", "SGDBoop");
            App.unzip("./downloads/sgdboop-win64.zip", "C:/Program Files (x86)/Steam/sgdboop");
            boopExists = App.execute("\"C:/Program Files (x86)/Steam/sgdboop/sgdboop.exe\"");
        }
    }

    public void installVCR() {
        if (App.hasChoco) {
            if (App.hasChocoInit) App.execute("choco install vcredist140 -y");
            else App.execute("C:\\ProgramData\\chocolatey\\bin\\choco.exe install vcredist140 -y");
        }
        else {
            App.download("https://aka.ms/vs/17/release/vc_redist.x64.exe", ".\\downloads\\vc_redist.x64.exe", "latest Visual C++ Redistributable (x64)");
            App.execute(".\\downloads\\vc_redist.x64.exe /S");
        }
    }

    public class gridURLData {
        private String name;
        private String appID;
        private URL gridURL;
        private URL heroURL;
        private URL logoURL;
        private URL iconURL;

        public gridURLData (String name, String appID, String gridURL, String heroURL, String logoURL, String iconURL) {
            this.name = name.trim();
            this.appID = appID.trim();
            try {
                if (gridURL != null) this.gridURL = new URL(gridURL.trim());
                if (heroURL != null) this.heroURL = new URL(heroURL.trim());
                if (logoURL != null) this.logoURL = new URL(logoURL.trim());
                if (iconURL != null) this.iconURL = new URL(iconURL.trim());
            } catch (MalformedURLException e) { App.out(e.getMessage(), "URL Conversion Error", "Exception"); }
        }

        public String getName() { return name; }
        public String getID() { return appID; }
        public URL getGridURL() { return gridURL; }
        public URL getHeroURL() { return heroURL; }
        public URL getLogoURL() { return logoURL; }
        public URL getIconURL() { return iconURL; }
    }

    public String getSGDBKey() {
        JEditorPane apiInstruct = new JEditorPane("text/html",
            "<html><body>" +
            "Please enter your SteamGridDB API Key" + "<br>" + "<br>" +
            "Obtainable at <a href=\"https://www.steamgriddb.com/profile/preferences/api\">https://www.steamgriddb.com/profile/preferences/api</a>" +
            "</body></html>"
        );

        apiInstruct.setEditable(false);
        apiInstruct.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent h) {
                if (h.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        URI link = new URI(h.getURL().toString());
                        Desktop.getDesktop().browse(link);
                    } catch (Exception e) { App.out(e.getMessage(), "SGDB API Key Error", "Exception"); }
                }
            }
        });
        
        while (sgdbKey == null || sgdbKey.trim().isEmpty()) {
            sgdbKey = JOptionPane.showInputDialog(null, apiInstruct, "SteamGridDB API", JOptionPane.PLAIN_MESSAGE);
            
            if (sgdbKey == null || sgdbKey.trim().isEmpty()) { App.out("API Key must be entered", "SGDB API Key Error", "Error"); }
        }

        return sgdbKey;
    }

    public String getSteamID() {

        JEditorPane apiInstruct = new JEditorPane("text/html",
            "<html><body>" +
            "Please enter your Steam friend code" + "<br>" + "<br>" +
            "How to get: <a href=\"https://www.youtube.com/watch?v=X2P0geVSMWU&t=26s\">https://www.youtube.com/watch?v=X2P0geVSMWU</a>" +
            "</body></html>"
        );
        apiInstruct.setEditable(false);
        apiInstruct.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent h) {
                if (h.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    try {
                        URI link = new URI(h.getURL().toString());
                        Desktop.getDesktop().browse(link);
                    } catch (Exception e) { App.out(e.getMessage(), "Steam ID Error", "Exception"); }
                }
            }
        });
        
        while (friendCode == null || friendCode.trim().isEmpty() || !friendCode.matches("\\d+")) {
            friendCode = JOptionPane.showInputDialog(null, apiInstruct, "Steam Friend Code", JOptionPane.PLAIN_MESSAGE);
            if (friendCode == null || friendCode.trim().isEmpty() || !friendCode.matches("\\d+")) {
                App.out("Steam ID must be entered", "Steam ID Error", "Error");
            }
        }

        return friendCode;

    }

    public void getGrids() {
        getBoop();
        if (!protocolHandler) protocolHandler();
        List<gridData> data = App.ini.parseSG(App.hasSteamIni);

        File gridPath = new File("C:\\Program Files (x86)\\Steam\\userdata\\" + App.sgdb.getSteamID() + "\\config\\grid");
        File iconPath = new File("C:\\Program Files (x86)\\Steam\\appcache\\librarycache" + "");

        if (gridPath.exists() || iconPath.exists()) {
            if (JOptionPane.showConfirmDialog(null, "Clear existing Steam Grids?") == 0) {
                App.ini.delete(gridPath);
                App.ini.delete(iconPath);
            }
        }
        gridPath.mkdirs();
        iconPath.mkdirs();

        for (gridData links : data) {
            String name = links.getName();
            String id = links.getID();
            String grid = links.getGrid(), hero = links.getHero(), logo = links.getLogo(), icon = links.getIcon();

            if (id.equals("nonsteam")) {
                App.out("Running SGDBoop Command for " + name + " images");
                try {
                    if (grid != null) App.execute("powershell Start-Process " + grid);
                    if (hero != null) App.execute("powershell Start-Process " + hero);
                    if (logo != null) App.execute("powershell Start-Process " + logo);
                    if (icon != null) App.execute("powershell Start-Process " + icon);
                } catch (Exception e) { App.out(e.getMessage(), "Exception Running SGDB Command", "Exception"); }
            } else {
                App.out("Downloading images for " + name);
                if (grid != null) { 
                    String ext = grid.substring(grid.lastIndexOf('.') + 1);
                    App.download(grid, gridPath + "\\" + id + "p." + ext, "Grid image for " + name);
                }
                if (hero != null) { 
                    String ext = hero.substring(hero.lastIndexOf('.') + 1);
                    App.download(hero, gridPath + "\\" + id + "_hero." + ext, "Hero image for " + name);
                }
                if (logo != null) { 
                    String ext = logo.substring(logo.lastIndexOf('.') + 1);
                    App.download(logo, gridPath + "\\" + id + "_logo." + ext, "Logo image for " + name);
                }
                if (icon != null) { 
                    String ext = icon.substring(icon.lastIndexOf('.') + 1);
                    App.download(icon, iconPath + "\\" + id + "p." + ext, "Icon image for " + name);
                }
            }

        }
    }

    public void downloadImage(String type, String appID, URL url, File path) {
        String ext = url.getPath().substring(url.getPath().lastIndexOf('.') + 1);

        if (!appID.equals("nonsteam")) {
            if (type.equals("grid")) { App.download(url.toString(), path.toString() + "\\" + appID + "p." + ext, appID); }
            if (type.equals("hero")) { App.download(url.toString(), path.toString() + "\\" + appID + "_hero." + ext, appID); }
            if (type.equals("logo")) { App.download(url.toString(), path.toString() + "\\" + appID + "p." + ext, appID); }
            if (type.equals("icon")) { App.download(url.toString(), path.toString() + "\\" + appID + "_icon.jpg", appID); }
        }
    }

    public String parseApi(String link) {
        try {
            URL path = new URL(link);
            HttpURLConnection http = (HttpURLConnection) path.openConnection();
            http.setRequestMethod("GET");
            http.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            http.addRequestProperty("Authorization", "Bearer " + sgdbKey);
            BufferedReader in = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) { out.append(line); }
            in.close();
            http.disconnect();

            JSONObject json = new JSONObject(out.toString());
            json = json.getJSONObject("data");
            return json.getString("url");

        } catch (Exception e) {
            App.out(e.getMessage(), "SteamGridDB URL Error", "Exception");
            return null;
        }
    }
    
}
