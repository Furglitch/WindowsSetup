package com.furglitch.windows_setup;

import static java.lang.System.setErr;
import static java.util.prefs.Preferences.systemRoot;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

import javax.swing.JOptionPane;

public class App { 

    static Config ini = new Config();
    static Interface ui = new Interface();
    static Chocolatey choco = new Chocolatey();
    static Software install = new Software();
    static SteamGrid sgdb = new SteamGrid();
    static Playbook ame = new Playbook();

    static boolean isAdmin = false;
    static boolean init = true;

    static boolean hasChoco = false;
    static boolean hasChocoInit = false;
    static boolean hasChocoGui = false;

    static boolean hasChocoIni = false;
    static boolean hasWebdlIni = false;
    static boolean hasSteamIni = false;

    public static void main(String[] args) {

        isAdmin = adminCheck();
        ui.create();
        if (isAdmin) {
            createDirectories();
            ui.update();
            init = false;
        } else {
            out("Program is not elevated! Please run with Admin.", "Launch Error", "Error");
        }
    }

    public static boolean adminCheck() {
        Preferences sys = systemRoot();

        synchronized (System.err) {
            try {
                sys.put("foo", "bar");
                sys.remove("foo");
                sys.flush();
                return true;
            } catch (Exception e) {
                return false;
            } finally { setErr(System.err); }
        }
    }

    public static void createDirectories() {
        File newFolder;
        String[] path = {"config", "downloads", "logs"};

        for (int p = 0; p < path.length; p++) {
            newFolder = new File(".\\" + path[p]);
            if (!newFolder.exists()) { 
                if (newFolder.mkdirs()) {
                    out("'" + path[p] + "' directory created successfully.");
                } else {
                    out("Unable to create '" + path[p] +"' directory.");
                }
            } else {
                out("'" + path[p] + "' directory already exists.");
            }
        }
    }

    static final String logdt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss"));
    public static void out(String msg) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
        String dt = LocalDateTime.now().format(format);
        System.out.println("[" + dt + "]  " + msg);

        try (PrintStream log = new PrintStream(new FileOutputStream("logs\\" + logdt +".txt", true));) {
            log.println("[" + dt + "]  " + msg);
        } catch (Exception  e) { System.out.println("ERROR: Could not write to log.\n\n" + e.getMessage()); }

    }

    public static void out(String msg, String title, String type) {
        if (type.equals("Exception")) {
            JOptionPane.showMessageDialog(null, "Exception Thrown!\n\nOutput:\n" + msg, title, JOptionPane.ERROR_MESSAGE);
        } else if (type.equals("Error")) {
            JOptionPane.showMessageDialog(null, "Error!\n\n" + msg, title, JOptionPane.ERROR_MESSAGE);
        } else if (type.equals("Info")) {
            JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            out("CODING ERROR: Invalid Out Type");
            out(msg);
        }
    }

    public static boolean download(String url, String path, String name) {
        try {
            out("Downloading " + name + "...");
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();
            http.setRequestMethod("GET");
            http.addRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
            BufferedInputStream in = new BufferedInputStream(http.getInputStream());
            Files.copy(in, Paths.get(path), StandardCopyOption.REPLACE_EXISTING);
            out("Downloaded " + name + " to " + path);
            in.close();
            return true;
        } catch (Exception e) {
            App.out(e.getMessage(), "Download Error", "Exception");
            return false;
        }
    }

    public static boolean unzip(String path, String output) {
        try {
            byte[] buffer = new byte[1024];
            ZipInputStream zip = new ZipInputStream(new FileInputStream(path));
            java.util.zip.ZipEntry zipEntry = zip.getNextEntry();
            new File(output).mkdirs();
            out("Unzipping " + path + " to " + output);
            while (zipEntry != null) {
                String fileName = zipEntry.getName();
                FileOutputStream out = new FileOutputStream(new File(output + "/" + fileName));
                int a;
                out("Extracting '" + fileName);
                while ((a = zip.read(buffer)) > 0) {
                    out.write(buffer, 0, a);
                }
                out.close();
                zip.closeEntry();
                zipEntry = zip.getNextEntry();
            }
            zip.closeEntry();
            zip.close();
            return true;
        } catch (Exception e) { 
            out(e.getMessage(), "Unable to Unzip", "Exception");
            return false;
        }
    }

    public static boolean execute(String command) {
        try {
            out("Running " + command);
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int prev = 0;
            while ((line = reader.readLine()) != null) {
                if (line.hashCode() != prev) App.out(line.trim());
                prev = line.hashCode();
            }

            if (process.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));
                App.out(errorOutput, "Execution Error", "Error");
            }
            return true;
        } catch (Exception e) {
            out(e.getMessage(), "Execution Error", "Exception");
            return false;
        }
    }

}