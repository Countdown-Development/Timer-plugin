# ⏰ Timer Plugin

A Minecraft Spigot plugin that adds a global timer with Time Mace mechanics. Players can modify the timer after getting kills and use powerful Time Mace abilities.

## ✨ Features

- ⏰ **Global Timer Display**: Shows time remaining as a boss bar for all players (can be hidden)
- ⚔️ **Kill Rewards**: Players who kill others receive special items to modify the timer
- 🔨 **Time Mace**: Dash forward and create time explosions
- 🎆 **Epic Announcements**: Server-wide title displays when legendary items are crafted
- ⚔️ **Mace Scarcity System**: Limited crafting for strategic gameplay
- 🖥️ **Dynamic GUI**: Centered modification interface regardless of configuration
- ⚙️ **Admin Configuration**: In-game GUI for easy configuration
- 🎛️ **Fully Configurable**: Customize all aspects of the plugin through the config

## 🆕 What's New in v6.5 - Mace Limit Fix

### 🔧 Bug Fix Update: Enhanced Mace System

### ✅ Fixed Issues:
- **Regular mace limit increased from 2 to 3 total per server**
- **Fixed crafting count bug that caused incorrect increments**
- **Improved event handling to prevent double-counting**
- **Updated all displays to show correct "X/3" format**

### ⚔️ Mace System Now:
- **3 regular maces + 1 unique Time Mace per server**
- **Accurate crafting counters and status displays**
- **Better server weapon economy balance**

### 🔧 Quick hotfix for servers experiencing mace counting issues.

### 🎬 **Epic Title Displays**
- **Server-wide announcements** when the Time Mace is crafted
- All players see dramatic titles: "⚡ LEGENDARY CRAFT! ⚡"
- Special crafter recognition: "🎉 LEGENDARY WIELDER! 🎉"
- Creates unforgettable legendary moments for your server

### ⚔️ **Regular Mace Scarcity System**
- **Only 3 regular maces** can be crafted per server
- **Only 1 Time Mace** can exist (legendary unique weapon)
- Server announcements track remaining crafts
- Title displays when limits are reached

### 🛠️ **Enhanced Admin Tools**
- New `/timerplugin resetregularmaces` command
- New `/timerplugin resetallmaces` command  
- Enhanced `/timerplugin macestatus` with full statistics
- **Mace Crafting Management GUI** with click-to-reset options

## 🔨 Crafting Recipes

### ⚡ Time Mace (Legendary Unique)
```
[   ] [ H ] [   ]
[ H ] [ M ] [ H ]
[   ] [ H ] [   ]
```
- **H** = Heart of the Sea (obtained from killing players)
- **M** = Regular Mace
- **Result**: 1x Time Mace (only ONE can exist per server!)

**Requirements:**
- Must use Heart of the Sea obtained from killing players
- Regular mace must be crafted first
- Once crafted, no more Time Maces can be made until reset by admin

### ⚔️ Regular Mace (Limited to 3 per server)
```
[   ] [ I ] [   ]
[   ] [ S ] [   ]
[   ] [ S ] [   ]
```
- **I** = Heavy Core
- **S** = Stick
- **Result**: 1x Regular Mace

**Requirements:**
- Standard Minecraft mace recipe
- Only 3 can be crafted per server
- Used as ingredient for Time Mace

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
  unique:
    crafted: false     # Auto-managed: whether Time Mace exists
```

### ⚔️ Regular Mace Settings

```yaml
regular-maces:
  crafted: 0           # Auto-managed: count of regular maces crafted
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
*Shows current timer status, visibility, configuration limits, and mace crafting status.*

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

### 🆕 Mace Management Commands (New in v6.5)

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/timerplugin macestatus` | 🔍 Check all mace crafting status | `timerplugin.admin` | `/timerplugin macestatus` |
| `/timerplugin resetmace` | 🔄 Reset Time Mace status | `timerplugin.admin` | `/timerplugin resetmace` |
| `/timerplugin resetregularmaces` | ⚔️ Reset regular mace count | `timerplugin.admin` | `/timerplugin resetregularmaces` |
| `/timerplugin resetallmaces` | 🔄 Reset all mace limits | `timerplugin.admin` | `/timerplugin resetallmaces` |

### 📝 Command Examples

**Checking mace status:**
```
/timerplugin macestatus
```
*Shows Time Mace status (CRAFTED/AVAILABLE) and regular mace count (X/3)*

**Resetting mace limits:**
```
/timerplugin resetallmaces
```
*Resets both Time Mace and regular mace limits, allowing fresh crafting*

**Setting the timer to 25 days:**
```
/timerplugin settime 25
```

**Giving a Time Mace to a player:**
```
/timerplugin givespecial Notch mace
```

**Opening the configuration GUI:**
```
/timerplugin configgui
```
*Opens an interactive menu to configure timer and mace settings*

### ⌨️ Tab Completion Features

The plugin includes smart tab completion to make commands easier to use:

- **🔤 Command Suggestions**: Type `/timerplugin` and press `Tab` to see all available subcommands
- **👤 Player Names**: When using `/timerplugin givespecial`, press `Tab` to cycle through online players
- **🎯 Item Types**: After selecting a player, press `Tab` to see available items (`mace`, `heartofthesea`)
- **🔢 Timer Values**: For `/timerplugin settime`, get suggestions for common timer values (1, 5, 10, 15, 20, 25, 30)
- **🔐 Permission-Based**: Only shows commands you have permission to use

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

### 🔨 Time Mace ⚡ UNIQUE LEGENDARY WEAPON
- 🏃 Allows you to dash forward (3 charges by default)
- 💥 Shift + right-click creates a time explosion that affects nearby players
- 💎 Uses custom Heart of the Sea in crafting
- ⏳ Has a cooldown period when charges are depleted
- 👑 **ONLY ONE can exist per server** - first to craft it wins!
- 🎉 **Server-wide announcement** when crafted
- ⚡ **Special effects** for the legendary crafter
- 🎆 **Epic title displays** for all players to witness

### ⚔️ Regular Mace (Limited Edition)
- 🛠️ Standard Minecraft mace with limited availability
- 📊 **Only 3 can be crafted per server**
- 🔧 Required ingredient for Time Mace crafting
- 🎯 Creates strategic value through scarcity

## 🖥️ Configuration GUI

Admins can use `/timerplugin configgui` to access in-game configuration:
- ⏰ **Timer Config**: Modify timer settings, visibility, and limits
- 🔨 **Mace Config**: Adjust Time Mace behavior and cooldowns
- 🆕 **Mace Management**: Reset crafting limits with click-to-reset interface
- 🔄 **Reload Config**: Apply changes from the config file

## 👑 Admin Features

### 👁️ Timer Visibility
- 🔄 Use `/timerplugin toggletimer` or the config GUI to hide the timer from players
- 🕶️ When hidden, players won't see the boss bar but the timer still counts down
- 🎭 Useful for creating suspense or surprise events

### 🔨 Mace Management
- 📊 Track all mace crafting with `/timerplugin macestatus`
- 🔄 Reset individual limits or all limits at once
- 🖥️ Use the Mace Management GUI for easy click-to-reset functionality
- 🎆 Control the legendary weapon economy of your server

## 🛠️ Troubleshooting

If you encounter any issues:

1. 📋 Check the server console for error messages
2. ✅ Make sure you're using the correct Minecraft version (1.21.1+)
3. 📝 Verify that the config.yml file is properly formatted
4. 🔄 Try using `/timerplugin reload` to reload the configuration

## 📋 Recent Updates & Changes

### ✅ New Features Added in v6.5
- 🎆 **Epic Title Displays**: Server-wide announcements when Time Mace is crafted
- ⚔️ **Regular Mace Limiting**: Only 3 regular maces can be crafted per server
- 🛠️ **Enhanced Admin Tools**: New reset commands and management GUI
- 📊 **Improved Status Tracking**: Better mace count display throughout plugin
- 🎬 **Cinematic Moments**: Legendary crafting creates memorable server events

### ✅ Features from Previous Updates
- 🔨 **Complete Mace Configuration System**: Commands to configure all Time Mace settings
- 🖱️ **Functional Config GUIs**: Click-to-cycle interface for easy configuration
- ⌨️ **Smart Tab Completion**: Full tab completion system for all commands
- 🛡️ **Right-Click Only Items**: Both Time Modifier and Time Mace only activate on right-click
- 👑 **Unique Time Mace**: Only one Time Mace can exist per server - legendary exclusivity!

### 🔄 Updated Features
- 📝 **Streamlined Commands**: Focused command set with organized help menu
- 🔨 **Enhanced Time Mace**: Improved effects, better configuration, and more reliable mechanics
- 🛠️ **Better Admin Tools**: More intuitive configuration management with working GUIs
- 🎯 **Simplified Focus**: Plugin centers on timer modification and mace mechanics
- 🖥️ **Professional Interface**: Modern GUI design with click-to-cycle functionality

### ❌ Removed Features
- 💀 **Lives System**: No more player lives tracking or management
- 🧊 **Time Controller**: Time freezing item and all related mechanics removed
- 🔄 **Revive System**: Player revival mechanics and revive totems removed

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
