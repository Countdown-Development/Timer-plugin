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

**💡 Pro Tip:** All commands support **tab completion**! Just start typing and press `Tab` to see available options and auto-complete commands.

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
| `/timerplugin setmaxadd <days>` | ➕ Set max days that can be added | `timerplugin.admin` | `/timerplugin setmaxadd 3` |
| `/timerplugin setmaxremove <days>` | ➖ Set max days that can be removed | `timerplugin.admin` | `/timerplugin setmaxremove 2` |
| `/timerplugin givespecial <player> <item>` | 🎁 Give special items | `timerplugin.admin` | `/timerplugin givespecial Steve mace` |

### 🔨 Mace Configuration Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/timerplugin setmacedashes <amount>` | 🏃 Set max mace dashes | `timerplugin.admin` | `/timerplugin setmacedashes 5` |
| `/timerplugin setmacecooldown <seconds>` | ⏰ Set mace cooldown | `timerplugin.admin` | `/timerplugin setmacecooldown 180` |
| `/timerplugin setmacestrength <strength>` | 💪 Set dash strength | `timerplugin.admin` | `/timerplugin setmacestrength 3.0` |
| `/timerplugin toggleexplosion` | 💥 Toggle time explosions | `timerplugin.admin` | `/timerplugin toggleexplosion` |
| `/timerplugin setexplosionradius <blocks>` | 📏 Set explosion radius | `timerplugin.admin` | `/timerplugin setexplosionradius 12` |

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

**Setting max modification limits:**
```
/timerplugin setmaxadd 3
```
*Players can now add up to 3 days at once*

```
/timerplugin setmaxremove 1
```
*Players can only remove 1 day at a time*

**Configuring Time Mace settings:**
```
/timerplugin setmacedashes 5
```
*Time Mace now has 5 charges before cooldown*

```
/timerplugin setmacecooldown 180
```
*Time Mace cooldown set to 3 minutes*

```
/timerplugin setmacestrength 3.5
```
*Dashes are now more powerful*

```
/timerplugin toggleexplosion
```
*Enable/disable time explosion feature*

### ⌨️ Tab Completion Features

The plugin includes smart tab completion to make commands easier to use:

- **🔤 Command Suggestions**: Type `/timerplugin` and press `Tab` to see all available subcommands
- **👤 Player Names**: When using `/timerplugin givespecial`, press `Tab` to cycle through online players
- **🎯 Item Types**: After selecting a player, press `Tab` to see available items (`mace`, `heartofthesea`)
- **🔢 Timer Values**: For `/timerplugin settime`, get suggestions for common timer values (1, 5, 10, 15, 20, 25, 30)
- **🔐 Permission-Based**: Only shows commands you have permission to use

**Example Tab Completion Flow:**
1. Type: `/timerplugin give` + `Tab` → Completes to `/timerplugin givespecial`
2. Add space and `Tab` → Shows list of online players
3. Type player name + space + `Tab` → Shows `mace` and `heartofthesea` options

## 🧪 Developer Testing (Realisticrave Only)

Special testing commands are available exclusively for the plugin developer:

### 🔬 Test Commands
| Command | Description |
|---------|-------------|
| `/timerplugin test` | 🖥️ Open developer test GUI |
| `/timerplugin test timergui` | 🧪 Open timer modification GUI without killing |
| `/timerplugin test items` | 🎁 Get all test items instantly |
| `/timerplugin test mace` | 🔄 Reset Time Mace cooldowns and charges |
| `/timerplugin test effects` | ✨ Test particle effects |
| `/timerplugin test explosion` | 💥 Test time explosion effect |

### 🖥️ Developer Test GUI Features
- **🧪 Quick Testing**: Instant access to all plugin features
- **⏰ Timer Controls**: Set timer to 1 or 30 days instantly
- **👁️ Visibility Toggle**: Quick timer show/hide testing
- **📊 Debug Info**: Real-time plugin statistics
- **🎯 All Items**: Get test items without killing players
- **🔄 Reset Functions**: Clear cooldowns for repeated testing

### 🖥️ Configuration GUI Navigation

When you use `/timerplugin configgui`, you'll see:

- 🔨 **Time Mace Config** - Configure dash count, cooldowns, explosion settings
- ⏰ **Timer Config** - Set visibility, max days, modification limits  
- 🔄 **Reload Config** - Apply changes from config.yml

**🖱️ How to Use Config GUIs:**
All configuration options in the GUIs are **click-to-cycle** - simply click on any setting to cycle through available values! No need to type commands or remember exact values. Changes are **automatically saved** to your config file.

**Timer Config Options:**
- 👁️ **Timer Visibility** - Toggle between visible/hidden (click to toggle)
- 📚 **Max Days** - Set maximum timer value (click to cycle: 10→15→20→25→30→50→100)
- ⏰ **Current Time** - View/modify current timer value (click to cycle: 1→5→10→15→20→25→30)
- ➕ **Max Add Days** - Limit how many days can be added at once (click to cycle: 1→2→3→4→5)
- ➖ **Max Remove Days** - Limit how many days can be removed at once (click to cycle: 1→2→3→4→5)

**Mace Config Options:**
- 🏃 **Max Dashes** - Set dash charges before cooldown (click to cycle: 1→10)
- ⏰ **Cooldown** - Set recharge time (click to cycle: 30→60→90→120→180→240 seconds)
- 💪 **Dash Strength** - Set movement force (click to cycle: 1.0→1.5→2.0→2.5→3.0→3.5→4.0)
- 💥 **Explosions** - Enable/disable time explosions (click to toggle)
- 📏 **Explosion Radius** - Set blast range (click to cycle: 3→5→8→10→12→15→20 blocks)

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

## 📋 Recent Updates & Changes

### ❌ Removed Features
- 💀 **Lives System**: No more player lives tracking or management
- 🧊 **Time Controller**: Time freezing item and all related mechanics removed
- 🔄 **Revive System**: Player revival mechanics and revive totems removed
- 📊 **Lives Commands**: `/lives` command and lives checking removed
- 🤝 **Trust System**: Player trust mechanics and related commands removed
- ⚔️ **Life Rewards**: No more gaining lives from adding time to timer

### ✅ New Features Added
- 🔨 **Complete Mace Configuration System**: Added 5 new commands to configure all Time Mace settings
- 🖱️ **Functional Config GUIs**: Click-to-cycle interface for easy configuration without typing commands
- ⌨️ **Smart Tab Completion**: Full tab completion system for all commands with context-aware suggestions
- 🛡️ **Right-Click Only Items**: Both Time Modifier and Time Mace now only activate on right-click
- 🧪 **Developer Testing Tools**: Comprehensive testing commands available exclusively to Realisticrave
- ➕➖ **Dynamic Timer Limits**: Commands to adjust max add/remove days on-the-fly
- 👁️ **Timer Visibility Toggle**: Admins can hide the timer from players completely
- 🎛️ **Real-Time Configuration**: All changes take effect immediately without server restart

### 🔄 Updated Features
- 📝 **Streamlined Commands**: Focused command set with organized help menu
- 🔨 **Enhanced Time Mace**: Improved effects, better configuration, and more reliable mechanics
- 🛠️ **Better Admin Tools**: More intuitive configuration management with working GUIs
- 🎯 **Simplified Focus**: Plugin now centers purely on timer modification and Time Mace mechanics
- 🖥️ **Professional Interface**: Modern GUI design with click-to-cycle functionality
- 📖 **Comprehensive Documentation**: Detailed examples, usage instructions, and organized help
- 🏢 **Professional Licensing**: Now licensed by RavenMC and Crave Inc.

### 🔧 Enhanced Configuration
- **Timer Settings**: `/timerplugin setmaxadd` and `/timerplugin setmaxremove` commands
- **Mace Settings**: Complete control over dashes, cooldown, strength, explosions, and radius
- **Auto-Save**: All GUI changes automatically save to config.yml
- **Real-Time Updates**: Changes take effect immediately without server restart

### 🖥️ Improved Admin Experience
- **Click-to-Cycle GUIs**: No more typing - just click to change values
- **Organized Help Menu**: Commands grouped by category for easy reference
- **Permission-Based Suggestions**: Only shows commands you can actually use
- **Instant Feedback**: Confirmation messages for all configuration changes

### 🎯 Better User Experience
- **Accidental Activation Prevention**: Items only work on right-click
- **Tab Completion**: Type faster with smart auto-completion
- **Cleaner Gameplay**: Removed complex systems for streamlined experience
- **Professional Polish**: Improved error handling and user feedback

### 🧪 Developer Tools (Realisticrave Only)
- **Test GUI**: Complete testing interface with all debugging tools
- **Quick Item Access**: Instant items and cooldown resets for development
- **Effect Testing**: Test particle effects and explosions safely
- **Timer Manipulation**: Quick timer adjustments for various test scenarios

## 📄 License

This plugin is licensed by **RavenMC** and **Crave Inc.**. You are free to use and modify it for your own server.

## 👨‍💻 Developer

**Made with ❤️ by Realisticrave**

### 🔗 Connect with Realisticrave:
- 🎥 **YouTube**: [https://www.youtube.com/@Realisticrave](https://www.youtube.com/@Realisticrave)
- 💬 **Discord**: [https://discord.gg/AgFQkNRaSe](https://discord.gg/AgFQkNRaSe)
- 🐙 **GitHub**: [https://github.com/Realisticrave1](https://github.com/Realisticrave1)

### 🙏 Support the Developer
If you enjoy this plugin, consider:
- ⭐ Starring the repository on GitHub
- 📺 Subscribing to the YouTube channel
- 💬 Joining the Discord community for support and updates
- 🐛 Reporting bugs and suggesting features
