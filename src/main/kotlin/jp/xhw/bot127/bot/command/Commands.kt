package jp.xhw.bot127.bot.command

import jp.xhw.bot127.bot.BotServices
import jp.xhw.trakt.bot.command.CommandRegistryBuilder
import jp.xhw.trakt.bot.command.commands
import jp.xhw.trakt.bot.infrastructure.runtime.TraktClientBuilder

private const val COMMAND_PREFIX = "!"

internal fun TraktClientBuilder.installCommands(services: BotServices) {
    commands(prefix = COMMAND_PREFIX) {
        registerForwardCommands(services)
        registerIconCommands(services)
    }
}

private fun CommandRegistryBuilder.registerForwardCommands(services: BotServices) {
    command("forward") {
        literal("add") {
            listOf("any", "任意", "*").forEach { anyChannelLiteral ->
                literal(anyChannelLiteral) {
                    greedyString("pattern") {
                        executes { command -> command.handleForwardAdd(services, sourceChannelId = null) }
                    }
                }
            }
            channel("channel") {
                greedyString("pattern") {
                    executes { command ->
                        command.handleForwardAdd(services, sourceChannelId = command.args.channel("channel").id)
                    }
                }
            }
        }

        literal("list") {
            executes { command -> command.handleForwardList(services) }
        }

        literal("remove") {
            string("ruleId") {
                executes { command -> command.handleForwardRemove(services) }
            }
        }

        literal("help") {
            executes { command -> command.handleForwardHelp(services) }
        }
    }
}

private fun CommandRegistryBuilder.registerIconCommands(services: BotServices) {
    command("icon") {
        literal("random") {
            executes { command -> command.handleIconRandom(services) }
        }

        user("user") {
            executes { command -> command.handleIconUser() }
        }
    }
}
