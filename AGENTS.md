## Approach

- Read existing files before writing. Don't re-read unless changed.
- Thorough in reasoning, concise in output.
- ONLY analyze code files
- Skip binary files over 100KB unless required.
- No sycophantic openers or closing fluff.
- No emojis or em-dashes.
- Do not guess APIs, versions, flags, commit SHAs, or package names. Verify by reading code or docs before asserting.
- ALWAYS ask me if want to add, update or remove dependencies or change directory structure.

## Compiling and validating

Because project does not contain gradle wrapper, if an agent need to validate changes, call a programmer to compile the
project via IDE and tell the agent the result before running further changes or fixes.

## Project Structure

Only key files and directories are shown in the directory tree.

```
arkhud/
├─ src/main/
│  ├─ resources/
│  │  ├─ assets/
│  │  ├─ META-INF/
│  │  │  ├─ mods.toml
│  │  ├─ arkhud.mixins.json
│  ├─ java/cc/arkko/mc/arkhud/forge/
│  │  ├─ configs/
│  │  │  ├─ ConfigManager.java           // configuration managements and definitions
│  │  ├─ mixins/
│  │  ├─ overlays/                       // custom hud IGuiOverlay implementations
│  │  │  ├─ components                   // custom hud components
│  │  ├─ utils/
│  │  │  ├─ U.java                       // mod utilities
│  │  ├─ C.java                          // mod constants
│  │  ├─ CommandManager.java             // command managements and definitions
│  │  ├─ ModForgeArkHud.java             // mod entrypoint
├─ gradle/
│  ├─ libs.versions.toml                 // definitions of versions of mod dependency and runtime mod
├─ build.gradle
├─ gradle.properties                     // basic information of mod
```

## Project

minecraft_version=1.20.1

forge_version=47.4.23

gradle path: `D:\dev\dotgradle`

shell priority: use powershell in windows, bash in linux or zsh in macos 
