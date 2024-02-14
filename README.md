# FurglitchOS Setup
![image](https://img.shields.io/badge/Windows-0078d4?style=for-the-badge&logo=windows-11&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![image](https://img.shields.io/badge/VSCode-0078D4?style=for-the-badge&logo=visual%20studio%20code&logoColor=white) 
![image](https://img.shields.io/badge/Chocolatey-628BAD?style=for-the-badge&logo=chocolatey&logoColor=fff)

A (fairly) simple way to quickly set up a fresh Windows, utelizing INI configs. (Does not install Windows, just tools)

## Current Features
In development...

## Usage
No Java / On a fresh install of Windows...
* Download 'run.bat' and place it in the same folder as 'WindowsSetup.jar', then run 'run.bat' as an administrator

If you already have Java installed...
* Open terminal as an administrator and run `java -jar path/to/WindowsSetup.jar`

## Dependencies
* [JFormDesigner/FlatLaf](https://github.com/JFormDesigner/FlatLaf) - Dark Theme
* [ini4j](https://ini4j.sourceforge.net/) - Parsing of .INI config files

## Planned Features
* [X] Installation of Chocolatey
* [X] Installation of ChocolateyGUI
* [ ] Installation of software via Chocolatey
* [ ] Installation of software via URLs
* [ ] Installation of fonts from [Google Fonts](https://github.com/google/fonts) and [Nerd Fonts](https://github.com/ryanoasis/nerd-fonts)
* [ ] Installation of Steam assets from [SteamGridDB](https://www.steamgriddb.com/)
  * [ ] Installation via URLS
  * [ ] Installation via [SteamGridDB/SGDBoop](https://github.com/SteamGridDB/SGDBoop) for non-steam games.
* [ ] Installation of AtlasOS via [Playbook](https://github.com/meetrevision/playbook)
* [X] .INI config files
  * [X] Parsing Steam Grid config INI
  * [X] Parsing Chocolatey config INI
  * [X] Parsing URL Download config INI
