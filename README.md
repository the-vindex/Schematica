## Fork notes (the-vindex/Schematica)

This is a fork of [Lunatrius/Schematica](https://github.com/Lunatrius/Schematica) (MIT, see `LICENSE`;
`logo.png` stays under its own CC BY-ND licence, see `LICENSE-EXCLUSIONS`). It exists for the
[minecraft-overseer](https://github.com/the-vindex/minecraft-overseer) project, which drives Schematica's
preview and printer from an AI agent. Planned changes: a printer and material list that can use blocks
stored inside a Dank/Null, and a printer that pauses when the player is hurt or hostiles are close.

It stays a drop-in replacement: mod id `schematica`, version `1.8.0.169`, same network messages, so a
client with this jar still joins servers running the original Schematica.

### Changes

- **Dank/Null support (optional).** When no loose stack of a block exists, the printer brings a Dank/Null
  holding it into an allowed swap slot, selects the stack with Dank/Null's own SELECTED packet and places
  with a normal right-click. That click is unsneaked (sneak+use opens the Dank/Null GUI), so it never
  targets a block with a tile entity. The material list counts Dank/Null contents (minus what the
  placement mode keeps). Only `client/printer/source/DankNullSource` touches Dank/Null, instantiated
  reflectively when the `danknull` mod is loaded; Dank/Null is a compile-only dependency.
- **Safety pause.** While printing, the printer pauses when the player takes damage or a hostile mob is
  within `hostileRadius` blocks, says so in chat (`[Schematica] printer paused: <reason>`), and resumes after
  `resumeAfterTicks` quiet ticks. Toggling the printer clears a pause. Config (`printer` category):
  `pauseOnDamage=true`, `hostileRadius=6` (0 = off), `resumeAfterTicks=60`.

### Building (Docker, original toolchain)

Upstream's toolchain (Gradle 2.9 wrapper, ForgeGradle 2.3, Java 8, MCP `snapshot_20171010`) is kept
unchanged and run in a container:

    scripts/docker-build.sh      # -> build/libs/Schematica-1.12.2-1.8.0.169-universal.jar

- `docker/Dockerfile`: `eclipse-temurin:8-jdk`. The Gradle cache lives in the named volume
  `schematica-gradle` (cold build ~1.5 min, rebuild ~10 s); the build runs as the calling user and sets
  `BUILD_NUMBER=169` so the version is `1.8.0.169`.
- `scripts/fetch-libs.sh` installs LunatriusCore 1.2.0.42 from a local e36 instance
  (`/mnt/minecraft-e36/mods`, override with `E36_MODS`) into the git-ignored `local-maven/`. It must not
  live in `libs/`: ForgeGradle 2 puts every file under `libs/` on the compile classpath, and the
  release (SRG-named) jar there shadows the deobfuscated one (`x has private access in Vec3i`).

Build changes versus upstream (`build.gradle` only):
- Forge maven URL `http://files.minecraftforge.net/maven` → `https://maven.minecraftforge.net/`.
- Removed `jcenter()` (shut down) and the CurseGradle plugin plus the CurseForge upload block.
- The `mc.lunatri.us` ivy repo (gone) is replaced by the local `local-maven/` repository.

The built jar has the same 145 classes and resources as the official 1.8.0.169 release; it is not signed
with Lunatrius' key (the official jar is, but Schematica declares no certificate fingerprint, so Forge
does not check it).

## Welcome to Schematica!
### Compiling
[Setup Java](#setup-java)

[Setup Git](#setup-git)

[Setup Schematica](#setup-schematica)

[Compile Schematica](#compile-schematica)

[Updating Your Repository](#updating-your-repository)

#### Setup Java
The Java JDK is used to compile Schematica.

1. Download and install the Java JDK.
    * [Windows/Mac download link](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html). Scroll down, accept the `Oracle Binary Code License Agreement for Java SE`, and download it (if you have a 64-bit OS, please download the 64-bit version).
    * Linux: Installation methods for certain popular flavors of Linux are listed below. If your distribution is not listed, follow the instructions specific to your package manager or install it manually [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html).
        * Gentoo: `emerge dev-java/oracle-jdk-bin`
        * Archlinux: `pacman -S jdk7-openjdk`
        * Ubuntu/Debian: `apt-get install openjdk-7-jdk`
        * Fedora: `yum install java-1.7.0-openjdk`
2. Set up the environment.
    * Windows: Set environment variables for the JDK.
        1. Go to `Control Panel\System and Security\System`, and click on `Advanced System Settings` on the left-hand side.
        2. Click on `Environment Variables`.
        3. Under `System Variables`, click `New`.
        4. For `Variable Name`, input `JAVA_HOME`.
        5. For `Variable Value`, input something similar to `C:\Program Files\Java\jdk1.7.0_45` exactly as shown (or wherever your Java JDK installation is), and click `Ok`.
        6. Scroll down to a variable named `Path`, and double-click on it.
        7. Append `;%JAVA_HOME%\bin` EXACTLY AS SHOWN and click `Ok`. Make sure the location is correct; double-check just to make sure.
3. Open up your command line and run `javac`. If it spews out a bunch of possible options and the usage, then you're good to go. If not try the steps again.

#### Setup Git
Git is used to clone Schematica and update your local copy.

1. Download and install Git [here](http://git-scm.com/download/).
2. *Optional* Download and install a Git GUI client, such as Github for Windows/Mac, SmartGitHg, TortoiseGit, etc. A nice list is available [here](http://git-scm.com/downloads/guis).

#### Setup Schematica
This section assumes that you're using the command-line version of Git.

1. Open up your command line.
2. Navigate to a place where you want to download Schematica's source (eg `C:\Development\Github\Minecraft\`) by executing `cd [folder location]`. This location is known as `mcdev` from now on.
3. Execute `git clone git@github.com:Lunatrius/Schematica.git`. This will download Schematica's source into `mcdev`.
4. Right now, you should have a directory that looks something like:

***
    mcdev
    \-Schematica
        \-Schematica's files (should have build.gradle)
***

#### Compile Schematica
1. Execute `gradlew setupDevWorkspace`. This sets up Forge and downloads the necessary libraries to build Schematica. This might take some time, be patient.
    * You will generally only have to do this once until the Forge version in `gradle.properties` changes.
2. Execute `gradlew build`. If you did everything right, `BUILD SUCCESSFUL` will be displayed after it finishes. This should be relatively quick.
    * If you see `BUILD FAILED`, check the error output (it should be right around `BUILD FAILED`), fix everything (if possible), and try again.
3. Go to `mcdev\Schematica\build\libs`.
    * You should see a `.jar` file named `Schematica-#.#.#-#.#.#.#-universal.jar`.
4. Copy the jar into your Minecraft mods folder, and you are done!

#### Updating Your Repository
In order to get the most up-to-date builds, you'll have to periodically update your local repository.

1. Open up your command line.
2. Navigate to `mcdev` in the console.
3. Make sure you have not made any changes to the local repository, or else there might be issues with Git.
    * If you have, try reverting them to the status that they were when you last updated your repository.
4. Execute `git pull master`. This pulls all commits from the official repository that do not yet exist on your local repository and updates it.

Shamelessly based this README off [pahimar's version](https://github.com/pahimar/Equivalent-Exchange-3).

## Origin

Fork of Lunatrius/Schematica (MIT) for the Overseer project: Cleanroom build, Dank/Null-aware printer, safety pause
