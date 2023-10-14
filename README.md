# Dragon Island

Dragon Island is a side scrolling platform game based on Super Mario World by Nintendo. The objective of this project is to create an exciting action game which is enjoyable to play. The players aim is to try and set a high score by getting to the end of each level before the time runs out while avoiding or destroying enemies and collecting power-ups.

The history of this project is that it started as a final year project for my Computer Science degree at Kingston University. I first developed a prototype using sprites from the New Super Mario Bros. game for the Nintendo DS. I then went on to build a fully functional game using Java SE which runs as an embedded webpage applet, standalone JAR and then mobile versions for Android, GP2X and the Sony PSP.

The source code is all written in the Java and C++ languages. 

If anyone is interested in continuing work on this open source game then I will be happy to merge the changes and republish to Google Play as an update. It is available on the app store [here](https://play.google.com/store/apps/details?id=com.citex.android.free.dragonisland&hl=en&pli=1).

Some of the new features I would like to add are:

* Improved touch screen controls
* Testing on different devices
* Improved physics
* Improved graphics engine and sprite handling
* More levels and a second world select screen
* Adding more royalty free sprites and backgrounds
* Different power up items and in-app purchases
* Mini subgames and secret areas
* Fixing keyboard controls on Apple Mac computers

The keyboard controls for the Java SE version of the game in the release are as follows:

Arrow keys = Move player  
Ctrl / Q = Run or Fireball  
Space / W = Jump  
Enter = Select Menu Item or Pause

There is also a debug menu which can be accessed from the Title Screen -> Settings -> Debug. This can be used to enable cheat settings to control various aspects of the game and display collision rectangles and debug information.

Below are the instructions on how to setup and run the various projects.

Importing an Eclipse Project
============================

The following Java projects were built using Eclipse. 

* Prototype-Demo
* DragonIsland-Java
* DragonIsland-Applet

Download the required software here:

[Java SE Development Kit 7](https://drive.google.com/file/d/1L7PlaI6XfkSZJcNKb5k9lY2wc_Ir4g5J/view?usp=sharing)

[Eclipse Juno ADT Bundle](https://drive.google.com/file/d/1UYRXzRgVR7R7XbJyDK7sxaEAi6Wh_Hmn/view?usp=sharing)

Here are the instructions to install the Java SDK and setup Eclipse:

1. Run the jdk-7u80-windows-x64.exe file to install the SDK
2. Extract the adt-bundle-windows-x86_64-20140702.zip to C:\Program Files\Eclipse

To import the projects in Eclipse follow these instructions:

1. Open Eclipse
2. Select File -> Import... 
3. Choose the import source -> General -> Existing Projects into Workspace
4. Select the directory of the project you want to open in the source code folder
5. Select Finish

You should now be able to choose the project in the Package Explorer and select Run to start the application.

The DragonIsland-Java project includes an editor to create new levels.

1. From the main game menu select Editor
2. Select Load Level -> Main Game and Level 1.1.1 to open an example level map
3. The editor includes instructions on how to edit or create a level
4. Press the enter key and select options to edit level settings such as background, tileset and dimensions

Compiling the Android Mobile Version
====================================

The Android version of the project is more difficult to setup. You will need to download the Android SDK. Follow the instructions below:

1. Run exclipse.exe as an Administrator
1. Open Window -> Android SDK Manager
2. Make sure to uncheck any existing API libraries
3. Check the option next to the Android 4.2.2 (API 17) SDK and expand the node to see packages
4. Deselect all System Images except for ARM EABI v7a System Image
5. Click Install packages...

Import the project DragonIsland-Android using the same instructions above.

I would suggest that you change the source code package presentation to hierarchical. Select the project in the Package Explorer open the "View Menu" with Ctrl + F10 then Package Presentation -> Hierarchical

The next thing to do is create an AVD (Android Virtual Device).

1. Select Window -> Android Virtual Device Manager
2. Click Create...
3. Give the device a name
4. Choose a device. I recommend Nexus S
5. Select the Target Android Version Android 4.2.2 - API Level 17
6. Select CPU/ABI as ARM (armebi-v7a)

Interestingly this build of the game can also be run as a Java SE application.

1. Select Run -> Run As... -> Java Application
2. Project -> Properties -> Run/Debug Settings
3. In the Run/Debug Settings select the Main launch configuration and click Edit...
4. Open the "Classpath" tab and remove Android Lib from "Bootstrap Entries"
5. Apply everything and Run the class again

Compiling the GP2X build
========================

Download the required software here:

[Dev-C++ for GP2X](https://drive.google.com/file/d/10xc5CiEEbbdEI99LKkKCmdnsaT-5S0Cd/view?usp=sharing)

1. Extract gp2xsdk_windows.zip to C:\Program Files\GP2XSDK
2. Open devcpp.exe to run Dev-C++
3. Select Tools -> Compiler Options and add -lSDL_gfx to the linker command line to include SDL
4. Select File -> Open Project or File... and the DragonIsland-GP2X source code folder and then DragonIsland.dev
5. Select Execute -> Run or Ctrl+F10

Compiling the PSP build
=======================

Download the required software here:

[Precompiled PSP Toolchain for Cygwin](https://drive.google.com/file/d/1L73EUwKjS2MkKD3QDyKspNbI3NxM7tWi/view?usp=sharing) or alternatively [Mirror](http://www.sakya.it/downloads/psp/cygwin_20081114.rar)

[OldSchool Library PSP](https://drive.google.com/file/d/1O7q2yKUBOIUSOzohAjVtT6ScS1hBxsJz/view?usp=sharing) or alternatively [Mirror](http://www.mobile-dev.ch/dl/psp/OSLib_210.rar)

You may have to right click on the mirror links and select Save link as... to download the files.

1. Extract cygwin_20081114_precompiled.rar to C:\cygwin\
2. Run C:\cygwin\launcher.exe as an Administrator
3. Extract OSLib_210.rar to C:\cygwin\home\user\
4. Run the commands:
```
cd "C:/cygwin/home/user/OSLib 2.10/Install"
./Install_cygwin.bat
```
5. Copy the DragonIsland-PSP folder included in the repository to C:\cygwin\home\user\
6. Enter the following command to build the game:
```
cd C:/cygwin/home/user/DragonIsland-PSP
make kxploit 
```

Acknowledgements
================

I have to mention a special thanks to my university lecturers who oversaw the project and helped teach me Java and C++. For the PSP build I have to mention the [PSPDEV](https://forums.ps2dev.org/) team who built the unofficial toolchain and Brunni for the [OSLib graphics library](http://www.mobile-dev.ch/old.php?page=pspsoft_oslib). An extra special thanks goes out to the artist [No-Body-The-Dragon](https://www.deviantart.com/no-body-the-dragon) who provided many of the graphics for the game.











