# ⏰ Timer Plugin

A Minecraft Spigot plugin that adds a global timer with Time Mace mechanics. Players can modify the timer after getting kills and use powerful Time Mace abilities.

## ✨ Features

- ⏰ **Global Timer Display**: Shows time remaining as a boss bar for all players (can be hidden)
- ⚔️ **Kill Rewards**: Players who kill others receive special items to modify the timer
- 🔨 **Time Mace**: Dash forward and create time explosions
- 🖥️ **Dynamic GUI**: Centered modification interface regardless of configuration
- ⚙️ **Admin Configuration**: In-game GUI for easy configuration
- 🎛️ **Fully Configurable**: Customize all aspects of the plugin through the config

## 📦 Installation

1. 📥 Download the compiled plugin JAR file
2. 📁 Place the JAR file in your server's `plugins` folder
3. 🔄 Restart your server
4. ⚙️ The plugin will generate default configuration files

## ⚙️ Configuration

The main configuration file is `config.yml`, located in the `plugins/TImerPlugin` folder after first run.

### ⏰ Timer Settings

```yaml
timer:
  initial-days: 30.0    # Starting time in days
  max-days: 30          # Maximum possible time
  visible: true         # Whether timer is visible to players
  modification:
    max-add: 2          # Maximum days a player can add at once
    max-remove: 2       # Maximum days a player can remove at once
    min-days-to-remove: 2  # Minimum timer value required to allow removal
```

### 🔨 Time Mace Settings

```yaml
time-mace:
  max-dashes: 3
  cooldown: 120        # Seconds
  dash-strength: 2.5
  explosion:
    radius: 8          # Blocks
    duration: 5        # Seconds
    slowness-level: 3  # Effect amplifier
    enabled: true
```

### 🎭 Effects Settings

```yaml
effects:
  add-time:
    sound: BLOCK_BEACON_AMBIENT
    sound-volume: 1.0
    sound-pitch: 1.0
    duration: 5         # Seconds
```

## 💻 Commands

### 👤 Player Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/timerplugin info` | 📊 Show plugin information | `timerplugin.use` | `/timerplugin info` |

**Example:**
```
/timerplugin info
```
*Shows current timer status, visibility, and configuration limits.*

### 👑 Admin Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/timerplugin settime <days>` | ⏰ Set the timer value | `timerplugin.admin` | `/timerplugin settime 15.5` |
| `/timerplugin reload` | 🔄 Reload the configuration | `timerplugin.admin` | `/timerplugin reload` |
| `/timerplugin configgui` | 🖥️ Open configuration GUI | `timerplugin.admin` | `/timerplugin configgui` |
| `/timerplugin toggletimer` | 👁️ Toggle timer visibility | `timerplugin.admin` | `/timerplugin toggletimer` |
| `/timerplugin givespecial <player> <item>` | 🎁 Give special items | `timerplugin.admin` | `/timerplugin givespecial Steve mace` |

### 📝 Command Examples

**Setting the timer to 25 days:**
```
/timerplugin settime 25
```

**Setting the timer to 12.5 days (12 days, 12 hours):**
```
/timerplugin settime 12.5
```

**Hiding the timer from all players:**
```
/timerplugin toggletimer
```
*Players won't see the boss bar, but the timer continues counting down*

**Giving a Time Mace to a player:**
```
/timerplugin givespecial Notch mace
```

**Giving a Time Modifier (Heart of the Sea) to a player:**
```
/timerplugin givespecial Steve heartofthesea
```

**Opening the configuration GUI:**
```
/timerplugin configgui
```
*Opens an interactive menu to configure timer and mace settings*

**Reloading the configuration:**
```
/timerplugin reload
```
*Applies changes made to the config.yml file*

### 🖥️ Configuration GUI Navigation

When you use `/timerplugin configgui`, you'll see:

- 🔨 **Time Mace Config** - Configure dash count, cooldowns, explosion settings
- ⏰ **Timer Config** - Set visibility, max days, modification limits
- 🔄 **Reload Config** - Apply changes from config.yml

**Timer Config Options:**
- 👁️ **Timer Visibility** - Toggle between visible/hidden
- 📚 **Max Days** - Set maximum timer value
- ⏰ **Current Time** - View/modify current timer value
- ➕ **Max Add Days** - Limit how many days can be added at once
- ➖ **Max Remove Days** - Limit how many days can be removed at once

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `timerplugin.use` | 👤 Allow use of basic plugin commands | All players |
| `timerplugin.admin` | 👑 Allow use of admin commands | Operators |

## 🎯 Custom Items

### 💙 Time Modifier
- 🎯 Obtained by killing another player
- 🖱️ Right-click to open a GUI to add or remove days from the timer
- 💀 Single-use item consumed after modification

### 🔨 Time Mace
- 🏃 Allows you to dash forward (3 charges by default)
- 💥 Shift + right-click creates a time explosion that affects nearby players
- 💎 Uses custom Heart of the Sea in crafting
- ⏳ Has a cooldown period when charges are depleted

## 🖥️ Configuration GUI

Admins can use `/timerplugin configgui` to access in-game configuration:
- ⏰ **Timer Config**: Modify timer settings, visibility, and limits
- 🔨 **Mace Config**: Adjust Time Mace behavior and cooldowns
- 🔄 **Reload Config**: Apply changes from the config file

## 👑 Admin Features

### 👁️ Timer Visibility
- 🔄 Use `/timerplugin toggletimer` or the config GUI to hide the timer from players
- 🕶️ When hidden, players won't see the boss bar but the timer still counts down
- 🎭 Useful for creating suspense or surprise events

## 🛠️ Troubleshooting

If you encounter any issues:

1. 📋 Check the server console for error messages
2. ✅ Make sure you're using the correct Minecraft version (1.21.1+)
3. 📝 Verify that the config.yml file is properly formatted
4. 🔄 Try using `/timerplugin reload` to reload the configuration

## 📋 Changes from Previous Version

### ❌ Removed Features
- 💀 **Lives System**: No more player lives tracking
- 🧊 **Time Controller**: Time freezing item removed
- 🔄 **Revive System**: Player revival mechanics removed
- 📊 **Lives Commands**: `/lives` command removed
- 🤝 **Trust System**: Player trust mechanics removed

### ✅ Added Features
- 👁️ **Timer Visibility Toggle**: Admins can hide the timer from players
- 🖥️ **Configuration GUI**: In-game configuration interface
- 🎯 **Simplified Focus**: Plugin now focuses solely on timer modification and Time Mace mechanics

### 🔄 Updated Features
- 📝 **Streamlined Commands**: Reduced command set for easier management
- 🔨 **Enhanced Time Mace**: Improved effects and configuration options
- 🛠️ **Better Admin Tools**: More intuitive configuration management

## 📄 License

This plugin is provided as-is with no warranty. You are free to use and modify it for your own server.