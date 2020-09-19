@file:Suppress("UNCHECKED_CAST")

package io.nekohasekai.bots

import cn.hutool.core.io.file.FileNameUtil
import io.nekohasekai.ktlib.td.cli.TdCli
import io.nekohasekai.pm.TdPmBot
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

object Launcher : TdCli("Launcher") {

    override var configFile = File("bots.yml")

    val botInstances = HashMap<String, TdCli>()
    lateinit var runTarget: String
    lateinit var runArgs: Array<String>

    @JvmStatic
    fun main(args: Array<String>) {

        launch(args)

        loadConfig()

        val bots = config["BOTS"] as? List<Map<String, Any>>

        if (bots == null) {

            clientLog.error("BOTS undefined.")

            exitProcess(100)

        } else if (bots.isEmpty()) {

            clientLog.error("BOTS empty.")

            exitProcess(100)

        }

        bots.forEachIndexed { index, it ->

            val botId = it["ID"] as? String

            if (botId.isNullOrBlank()) {

                clientLog.error("ID undefined ( bot #$index )")

                exitProcess(100)

            }

            if (botInstances.containsKey(botId)) {

                clientLog.error("ID $botId already used ( bot #$index )")

                exitProcess(100)

            }

            val botType = it["TYPE"] as? String

            if (botType.isNullOrBlank()) {

                clientLog.error("TYPE undefined ( bot $botId #$index )")

                exitProcess(100)

            }

            botInstances[botId] = when (botType.toLowerCase()) {

                "pm" -> TdPmBot(botId, "TdPmBot #$botId")

                else -> {

                    clientLog.error("Unknown bot type $botType ( bot $botId #$index )")

                    exitProcess(100)

                }

            }.apply {

                arguments = arrayOf()
                dataDir = File(this@Launcher.dataDir, FileNameUtil.cleanInvalid(tag)
                        .toLowerCase()
                        .replace("  ", " ")
                        .replace(" ", "_"))

                cacheDir = this@Launcher.cacheDir

                config.putAll(this@Launcher.config.filterKeys { it !in arrayOf("BOTS", "DATA_DIR", "CACHE_DIR") })
                config.putAll(it.filterKeys { it !in arrayOf("ID", "TYPE") })

                config.remove("LOG_LEVEL")

                onLoadConfig()

            }

        }

        if (::runTarget.isInitialized) {

            val targetBots = LinkedList<TdCli>()

            when {

                runTarget.toLowerCase() == "all" -> {

                    targetBots.addAll(botInstances.values)

                }

                runTarget.startsWith("type:") -> {

                    val runType = runTarget.substringAfter(":")

                    when (runType.toLowerCase()) {

                        "pm" -> {

                            targetBots.addAll(botInstances.values.filterIsInstance<TdPmBot>())

                        }

                        else -> {

                            clientLog.error("Unknown bot type $runType in exec run target")

                            exitProcess(100)

                        }

                    }

                }

                else -> {

                    if (botInstances.containsKey(runTarget)) {

                        targetBots.add(botInstances[runTarget]!!)

                    } else {

                        clientLog.error("Unknown botId $runTarget in exec run target")

                        exitProcess(100)

                    }


                }

            }

            if (targetBots.isEmpty()) {

                clientLog.error("Empty exec targets")

                exitProcess(0)
            }

            targetBots.forEach {

                clientLog.info("Exec in ${it.tag}:\n")

                it.launch(runArgs)

            }

            exitProcess(0)

        }

        runBlocking {

            botInstances.values.forEach {

                it.clientLog.info("Launching")

                if (!it.waitForAuth()) {

                    exitProcess(100)

                }

            }

        }

    }

    override fun onArgument(argument: String, value: String?) {

        when (argument) {

            "config" -> {

                super.onArgument(argument, value)

                return

            }

            else -> {

                runTarget = argument

                if (value.isNullOrBlank()) {

                    clientLog.error("Empty exec args")

                    exitProcess(100)

                }

                runArgs = value.split(" ").toTypedArray()

            }

        }

    }

}