package com.furglitch.windows_setup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Chocolatey {

    String[] command = {"powershell.exe", "-ExecutionPolicy", "Bypass", "", ""};

    public boolean check() {

        App.out("Checking for Chocolatey on system...");

        command[3] = "-Command";
        command[4] = "choco --version";
        String line;

        try {
            App.out("Starting Powershell with command '" + command[4] + "'...");

            Process ps = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (!line.trim().matches("\\d+.\\d+.\\d+")) App.out(line.trim());
                else {
                    App.out("Chocolatey Version: " + line);
                    App.out("Stopping Powershell...");
                    ps.destroy();
                    return true;
                }
            }
            ps.waitFor();

            if (ps.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));

                if (errorOutput.contains("CommandNotFoundException")) {
                    App.out("Chocolatey not detected.");
                    App.out("Stopping Powershell...");
                    ps.destroy();
                } else {
                    App.out(errorOutput, "Chocolatey Check Error", "Error");
                    App.out("Stopping Powershell...");
                    ps.destroy();
                }
                return false;
            }

        } catch (Exception e) {
            App.out(e.getMessage(), "Chocolatey Check Error", "Exception");
        }
        return false;
    }

    public boolean checkGui() {

        App.out("Checking for Chocolatey on system...");

        command[3] = "-Command";
        String line;

        try {
            
            if (App.hasChocoInit) command[4] = "choco list";
            else { command[4] = "C:\\ProgramData\\chocolatey\\bin\\choco.exe list"; }

            App.out("Starting Powershell with command '" + command[4] + "'...");

            Process ps = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            while ((line = reader.readLine()) != null) {
                if (!line.contains("chocolateygui")) App.out(line.trim());
                else {
                    App.out(line.trim());
                    App.out("ChocolateyGUI detected.");
                    App.out("Stopping Powershell...");
                    ps.destroy();
                    return true;
                }
            }
            ps.waitFor();

            if (ps.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));

                if (errorOutput.contains("CommandNotFoundException")) {
                    App.out("Chocolatey not detected.");
                    App.out("Stopping Powershell...");
                    ps.destroy();
                } else {
                    App.out(errorOutput, "ChocolateyGUI Check Error", "Error");
                    App.out("Stopping Powershell...");
                    ps.destroy();
                }
                return false;
            }

        } catch (Exception e) {
            App.out(e.getMessage(), "ChocolateyGUI Check Error", "Exception");
        }
        return false;
    }

    public void install() {

        App.out("Installing Chocolatey...");

        String path = ".\\downloads\\chocoInstall.ps1";
        command[3] = "-File";
        command[4] = path;

        try {
            App.download("https://community.chocolatey.org/install.ps1", path, "Chocolatey Install Script");

            App.out("Starting Powershell, executing '" + path + "'");
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            App.out("");
            while ((line = reader.readLine()) != null) {
                App.out(line.trim());
            }
            App.out("");

            process.waitFor();
            if (process.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));

                App.out(errorOutput, "Chocolatey Install Error", "Error");
            }
            App.out("Stopping Powershell.");
            process.destroy();

        } catch (Exception e) { App.out(e.getMessage(), "Chocolatey Install Error", "Exception"); }

    }

    public void installGui() {

        App.out("Installing ChocolateyGUI...");

        command[3] = "-Command";

        try {

            if (App.hasChocoInit) command[4] = "choco install chocolateygui -y";
            else { command[4] = "C:\\ProgramData\\chocolatey\\bin\\choco.exe install chocolateygui -y"; }

            App.out("Starting Powershell process with command '" + command[4] + "'");
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            int prev = 0;
            App.out("");
            while ((line = reader.readLine()) != null) {
                if (line.hashCode() != prev) App.out(line.trim());
                prev = line.hashCode();
            }
            App.out("");

            process.waitFor();
            if (process.exitValue() != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String errorOutput = errorReader.lines().collect(Collectors.joining(System.lineSeparator()));

                App.out(errorOutput, "ChocolateyGUI Install Error", "Error");
            }
            App.out("Stopping Powershell.");
            process.destroy();

        } catch (Exception e) { App.out(e.getMessage(), "ChocolateyGUI Install Error", "Exception"); }

    }
    
}
