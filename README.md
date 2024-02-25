# Furglitch Windows Setup
![Windows](https://img.shields.io/badge/Windows-0078d4?style=for-the-badge&logo=windows-11&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![VSCode](https://img.shields.io/badge/VSCode-0078D4?style=for-the-badge&logo=visual%20studio%20code&logoColor=white)

A (fairly) simple way to quickly set up a fresh Windows, utelizing INI configs. (Does not install Windows, just tools)

## Current Features
* Automated installation of [Chocolatey](https://github.com/chocolatey/choco)
  * Automated installation of software via Chocolatey
  * Automated installation of [ChocolateyGUI](https://github.com/chocolatey/ChocolateyGUI)
* Execution of software from URLs
* [Ameliorated Wizard](https://ameliorated.io/) installation
* Automated installation of Steam assets from [SteamGridDB](https://www.steamgriddb.com/)
  * Installation of [SteamGridDB/SGDBoop](https://github.com/SteamGridDB/SGDBoop) for non-steam games
* .INI Config Files
  * .INI detection, download if not present

## Usage
No Java / On a fresh install of Windows...
* Download 'run.bat' and place it in the same folder as 'WindowsSetup.jar', then run 'run.bat' as an administrator

If you already have Java installed...
* Open terminal as an administrator and run `java -jar path/to/WindowsSetup.jar`
* Or run 'run.bat' as an administrator. It should detect you already have Java and run

## Dependencies
* [JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf) - Dark Theme
* [ini4j](https://ini4j.sourceforge.net/) - Parsing of .INI config files
* [JSON-java](https://github.com/stleary/JSON-java) - Parsing of JSON data from SteamGridDB API
* [JNA](https://github.com/java-native-access/jna) - Checking Windows Registry for protocols
Distributed under [Apache License v2.0](https://github.com/Furglitch/WindowsSetup?tab=Apache-2.0-1-ov-file).

## Planned Features
* [ ] Automated execution of AME Playbooks
  * Waiting for documentation of [Ameliorated CLI](https://git.ameliorated.info/Styris/trusted-uninstaller-cli)
* [ ] Automated execution of software from compressed files.
* [ ] Decompression of .zip and .rar files
* [ ] Installation of fonts from [Google Fonts](https://github.com/google/fonts) and [Nerd Fonts](https://github.com/ryanoasis/nerd-fonts)

## Known issues
* 
