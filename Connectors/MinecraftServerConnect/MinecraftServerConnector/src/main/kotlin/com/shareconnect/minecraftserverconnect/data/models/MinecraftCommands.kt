/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.minecraftserverconnect.data.models

/**
 * Predefined Minecraft commands organized by category
 */
object MinecraftCommands {

    /**
     * Player management commands
     */
    val playerManagement = listOf(
        MinecraftCommand(
            name = "List Players",
            command = "list",
            description = "List all online players",
            category = CommandCategory.PLAYER_MANAGEMENT,
            requiresOp = false
        ),
        MinecraftCommand(
            name = "Kick Player",
            command = "kick {player} {reason}",
            description = "Kick a player from the server",
            category = CommandCategory.PLAYER_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player name to kick", ParameterType.PLAYER_NAME, required = true),
                CommandParameter("reason", "Reason for kicking", ParameterType.STRING, required = false, defaultValue = "Kicked by admin")
            )
        ),
        MinecraftCommand(
            name = "Teleport Player",
            command = "tp {player} {coordinates}",
            description = "Teleport a player to coordinates",
            category = CommandCategory.TELEPORT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to teleport", ParameterType.PLAYER_NAME, required = true),
                CommandParameter("coordinates", "Target coordinates (x y z)", ParameterType.COORDINATES, required = true)
            )
        ),
        MinecraftCommand(
            name = "Set Game Mode",
            command = "gamemode {mode} {player}",
            description = "Change player's game mode",
            category = CommandCategory.PLAYER_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("mode", "Game mode (survival/creative/adventure/spectator)", ParameterType.GAMEMODE, required = true),
                CommandParameter("player", "Target player", ParameterType.PLAYER_NAME, required = true)
            )
        ),
        MinecraftCommand(
            name = "Give XP",
            command = "xp add {player} {amount}",
            description = "Give experience points to a player",
            category = CommandCategory.EXPERIENCE,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to give XP to", ParameterType.PLAYER_NAME, required = true),
                CommandParameter("amount", "Amount of XP", ParameterType.INTEGER, required = true)
            )
        ),
        MinecraftCommand(
            name = "Clear Inventory",
            command = "clear {player}",
            description = "Clear player's inventory",
            category = CommandCategory.PLAYER_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player whose inventory to clear", ParameterType.PLAYER_NAME, required = true)
            )
        )
    )

    /**
     * Server control commands
     */
    val serverControl = listOf(
        MinecraftCommand(
            name = "Stop Server",
            command = "stop",
            description = "Stop the server",
            category = CommandCategory.SERVER_CONTROL,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Save World",
            command = "save-all",
            description = "Save the world to disk",
            category = CommandCategory.SERVER_CONTROL,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Save Off",
            command = "save-off",
            description = "Disable automatic saving",
            category = CommandCategory.SERVER_CONTROL,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Save On",
            command = "save-on",
            description = "Enable automatic saving",
            category = CommandCategory.SERVER_CONTROL,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Broadcast Message",
            command = "say {message}",
            description = "Broadcast a message to all players",
            category = CommandCategory.SERVER_CONTROL,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("message", "Message to broadcast", ParameterType.STRING, required = true)
            )
        )
    )

    /**
     * World management commands
     */
    val worldManagement = listOf(
        MinecraftCommand(
            name = "Set Time Day",
            command = "time set day",
            description = "Set time to day",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Set Time Night",
            command = "time set night",
            description = "Set time to night",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Set Time",
            command = "time set {time}",
            description = "Set world time to specific value",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("time", "Time value (0-24000)", ParameterType.INTEGER, required = true)
            )
        ),
        MinecraftCommand(
            name = "Clear Weather",
            command = "weather clear {duration}",
            description = "Set weather to clear",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("duration", "Duration in seconds", ParameterType.INTEGER, required = false, defaultValue = "300")
            )
        ),
        MinecraftCommand(
            name = "Rain Weather",
            command = "weather rain {duration}",
            description = "Set weather to rain",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("duration", "Duration in seconds", ParameterType.INTEGER, required = false, defaultValue = "300")
            )
        ),
        MinecraftCommand(
            name = "Thunder Weather",
            command = "weather thunder {duration}",
            description = "Set weather to thunder",
            category = CommandCategory.TIME_WEATHER,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("duration", "Duration in seconds", ParameterType.INTEGER, required = false, defaultValue = "300")
            )
        ),
        MinecraftCommand(
            name = "Set Spawn Point",
            command = "setworldspawn {coordinates}",
            description = "Set world spawn point",
            category = CommandCategory.WORLD_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("coordinates", "Spawn coordinates (x y z)", ParameterType.COORDINATES, required = true)
            )
        ),
        MinecraftCommand(
            name = "Set Difficulty",
            command = "difficulty {difficulty}",
            description = "Change server difficulty",
            category = CommandCategory.DIFFICULTY,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("difficulty", "Difficulty (peaceful/easy/normal/hard)", ParameterType.STRING, required = true)
            )
        )
    )

    /**
     * Whitelist commands
     */
    val whitelist = listOf(
        MinecraftCommand(
            name = "Whitelist Add",
            command = "whitelist add {player}",
            description = "Add player to whitelist",
            category = CommandCategory.WHITELIST,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player name to add", ParameterType.PLAYER_NAME, required = true)
            )
        ),
        MinecraftCommand(
            name = "Whitelist Remove",
            command = "whitelist remove {player}",
            description = "Remove player from whitelist",
            category = CommandCategory.WHITELIST,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player name to remove", ParameterType.PLAYER_NAME, required = true)
            )
        ),
        MinecraftCommand(
            name = "Whitelist On",
            command = "whitelist on",
            description = "Enable whitelist",
            category = CommandCategory.WHITELIST,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Whitelist Off",
            command = "whitelist off",
            description = "Disable whitelist",
            category = CommandCategory.WHITELIST,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Whitelist List",
            command = "whitelist list",
            description = "List whitelisted players",
            category = CommandCategory.WHITELIST,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Whitelist Reload",
            command = "whitelist reload",
            description = "Reload whitelist from disk",
            category = CommandCategory.WHITELIST,
            requiresOp = true
        )
    )

    /**
     * Operator commands
     */
    val operator = listOf(
        MinecraftCommand(
            name = "Op Player",
            command = "op {player}",
            description = "Grant operator status to player",
            category = CommandCategory.OPERATOR,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to op", ParameterType.PLAYER_NAME, required = true)
            )
        ),
        MinecraftCommand(
            name = "Deop Player",
            command = "deop {player}",
            description = "Revoke operator status from player",
            category = CommandCategory.OPERATOR,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to deop", ParameterType.PLAYER_NAME, required = true)
            )
        )
    )

    /**
     * Ban management commands
     */
    val banManagement = listOf(
        MinecraftCommand(
            name = "Ban Player",
            command = "ban {player} {reason}",
            description = "Ban a player from the server",
            category = CommandCategory.BAN_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to ban", ParameterType.PLAYER_NAME, required = true),
                CommandParameter("reason", "Reason for ban", ParameterType.STRING, required = false, defaultValue = "Banned by admin")
            )
        ),
        MinecraftCommand(
            name = "Pardon Player",
            command = "pardon {player}",
            description = "Unban a player",
            category = CommandCategory.BAN_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to unban", ParameterType.PLAYER_NAME, required = true)
            )
        ),
        MinecraftCommand(
            name = "Ban IP",
            command = "ban-ip {ip} {reason}",
            description = "Ban an IP address",
            category = CommandCategory.BAN_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("ip", "IP address to ban", ParameterType.STRING, required = true),
                CommandParameter("reason", "Reason for ban", ParameterType.STRING, required = false, defaultValue = "Banned by admin")
            )
        ),
        MinecraftCommand(
            name = "Pardon IP",
            command = "pardon-ip {ip}",
            description = "Unban an IP address",
            category = CommandCategory.BAN_MANAGEMENT,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("ip", "IP address to unban", ParameterType.STRING, required = true)
            )
        ),
        MinecraftCommand(
            name = "Ban List",
            command = "banlist",
            description = "List all banned players",
            category = CommandCategory.BAN_MANAGEMENT,
            requiresOp = true
        )
    )

    /**
     * Give item commands
     */
    val giveItems = listOf(
        MinecraftCommand(
            name = "Give Item",
            command = "give {player} {item} {amount}",
            description = "Give items to a player",
            category = CommandCategory.GIVE_ITEMS,
            requiresOp = true,
            parameters = listOf(
                CommandParameter("player", "Player to give items to", ParameterType.PLAYER_NAME, required = true),
                CommandParameter("item", "Item ID (e.g., minecraft:diamond)", ParameterType.ITEM_ID, required = true),
                CommandParameter("amount", "Number of items", ParameterType.INTEGER, required = false, defaultValue = "1")
            )
        )
    )

    /**
     * Game rule commands
     */
    val gameRules = listOf(
        MinecraftCommand(
            name = "Keep Inventory On",
            command = "gamerule keepInventory true",
            description = "Enable keep inventory on death",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Keep Inventory Off",
            command = "gamerule keepInventory false",
            description = "Disable keep inventory on death",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Mob Griefing On",
            command = "gamerule mobGriefing true",
            description = "Enable mob griefing",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Mob Griefing Off",
            command = "gamerule mobGriefing false",
            description = "Disable mob griefing",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Day Cycle On",
            command = "gamerule doDaylightCycle true",
            description = "Enable day/night cycle",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Day Cycle Off",
            command = "gamerule doDaylightCycle false",
            description = "Disable day/night cycle",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Fire Spread On",
            command = "gamerule doFireTick true",
            description = "Enable fire spread",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        ),
        MinecraftCommand(
            name = "Fire Spread Off",
            command = "gamerule doFireTick false",
            description = "Disable fire spread",
            category = CommandCategory.GAME_RULES,
            requiresOp = true
        )
    )

    /**
     * Get all commands
     */
    val allCommands = playerManagement + serverControl + worldManagement +
                      whitelist + operator + banManagement + giveItems + gameRules

    /**
     * Get commands by category
     */
    fun getCommandsByCategory(category: CommandCategory): List<MinecraftCommand> {
        return allCommands.filter { it.category == category }
    }

    /**
     * Find command by name
     */
    fun findCommandByName(name: String): MinecraftCommand? {
        return allCommands.find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Search commands by keyword
     */
    fun searchCommands(keyword: String): List<MinecraftCommand> {
        return allCommands.filter {
            it.name.contains(keyword, ignoreCase = true) ||
            it.description.contains(keyword, ignoreCase = true) ||
            it.command.contains(keyword, ignoreCase = true)
        }
    }

    /**
     * Get commonly used commands
     */
    val commonCommands = listOf(
        findCommandByName("List Players"),
        findCommandByName("Stop Server"),
        findCommandByName("Save World"),
        findCommandByName("Set Time Day"),
        findCommandByName("Clear Weather"),
        findCommandByName("Kick Player"),
        findCommandByName("Teleport Player"),
        findCommandByName("Give Item")
    ).filterNotNull()
}
