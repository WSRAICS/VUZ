package ru.ncfu.selentar.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleLongProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import ru.ncfu.selentar.environment.Action
import ru.ncfu.selentar.environment.Environment
import ru.ncfu.selentar.environment.MapSurface
import tornadofx.*
import java.lang.IllegalStateException
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class MainView : View("Hello TornadoFX") {

    val environmentUi = find<EnvironmentUi>()
    val env = Environment(environmentUi)

    val executor = Executors.newCachedThreadPool()

    val sleepProperty = SimpleLongProperty(1000)
    val pauseProperty = SimpleBooleanProperty(false)
    val cancelledProperty = SimpleBooleanProperty(false)

    val runDisableProperty = SimpleBooleanProperty(false)
    val startLearnDisableProperty = SimpleBooleanProperty(false)
    val stopLearnDisableProperty = SimpleBooleanProperty(false)
    val testDisableProperty = SimpleBooleanProperty(false)
    val leftDisableProperty = SimpleBooleanProperty(false)
    val rightDisableProperty = SimpleBooleanProperty(false)
    val forwardDisableProperty = SimpleBooleanProperty(false)
    val turnDisableProperty = SimpleBooleanProperty(false)

    var startTask: Task<*> = FXTask() {}
    var testTask: Task<*> = FXTask() {}

    override val root = borderpane {
        left {
            vbox {
                spacing = 10.0
                paddingLeft = 10.0
                paddingTop = 10.0

                button("Запуск") {
                    prefWidth = 150.0
                    disableProperty().bind(runDisableProperty)
                    action {
                        startTask = runAsyncWithProgress {
                            while (env.getObservation().forward == MapSurface.ROAD && !startTask.isCancelled) {
                                doMove(Action.MOVE_FORWARD)
                                Thread.sleep(sleepProperty.value)
                            }
                        }
                    }
                }

                button("Стоп") {
                    prefWidth = 150.0
                    action {
                        startTask.cancel()
                        testTask.cancel()

                        executor.shutdown()
                        pauseProperty.value = false

                        runDisableProperty.value = false
                        startLearnDisableProperty.value = false
                        stopLearnDisableProperty.value = false
                        testDisableProperty.value = false
                        leftDisableProperty.value = false
                        rightDisableProperty.value = false
                        forwardDisableProperty.value = false
                        turnDisableProperty.value = false

                        env.reset()
                        env.render()
                    }
                }

                button("Пауза") {
                    prefWidth = 150.0
                    action {
                        pauseProperty.value = !pauseProperty.value

                        runDisableProperty.value = !runDisableProperty.value
                        startLearnDisableProperty.value = !startLearnDisableProperty.value
                        stopLearnDisableProperty.value = !stopLearnDisableProperty.value
                        testDisableProperty.value = !testDisableProperty.value
                        leftDisableProperty.value = !leftDisableProperty.value
                        rightDisableProperty.value = !rightDisableProperty.value
                        forwardDisableProperty.value = !forwardDisableProperty.value
                        turnDisableProperty.value = !turnDisableProperty.value
                    }
                }

                button("Запуск обучения") {
                    prefWidth = 150.0
                    disableProperty().bind(startLearnDisableProperty)
                }

                button("Остановка обучения") {
                    prefWidth = 150.0
                    disableProperty().bind(stopLearnDisableProperty)
                }

                button("Тест управления") {
                    prefWidth = 150.0
                    disableProperty().bind(testDisableProperty)
                    action {
                        testTask = runAsyncWithProgress {
                            doMove(Action.MOVE_FORWARD, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN_LEFT, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN_RIGHT, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN_RIGHT, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN_LEFT, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN, test = true)
                            Thread.sleep(sleepProperty.value)

                            doMove(Action.MOVE_FORWARD, test = true)
                            Thread.sleep(sleepProperty.value)
                            doMove(Action.TURN, test = true)
                            Thread.sleep(sleepProperty.value)
                        }
                    }
                }
            }
        }

        center {
            add(environmentUi)
        }

        right {
            vbox {
                spacing = 5.0
                paddingRight = 10.0
                paddingTop = 10.0

                hbox {
                    alignment = Pos.CENTER
                    button("F") {
                        disableProperty().bind(forwardDisableProperty)
                        action {
                            doMove(Action.MOVE_FORWARD)
                        }
                    }
                }

                hbox {
                    spacing = 10.0
                    alignment = Pos.CENTER
                    button("L") {
                        disableProperty().bind(leftDisableProperty)
                        action {
                            doMove(Action.TURN_LEFT)
                        }
                    }
                    button("R") {
                        disableProperty().bind(rightDisableProperty)
                        action {
                            doMove(Action.TURN_RIGHT)
                        }
                    }
                }

                hbox {
                    alignment = Pos.CENTER
                    button("T") {
                        disableProperty().bind(turnDisableProperty)
                        action {
                            doMove(Action.TURN)
                        }
                    }
                }
            }
        }
    }

    init {
        env.render()
    }

    private fun doMove(action: Action, test: Boolean = false) {
        val task = task {
            while (pauseProperty.value) {
                if (cancelledProperty.value) return@task
                if (isCancelled) return@task
                Thread.sleep(sleepProperty.value)
            }

            try {
                env.step(action, test)
            } catch (e: IllegalStateException) {
                runLater {
                    val result = if (env.getObservation().finished) "Вы выйграли!" else "Вы проиграли!"
                    tornadofx.error("Эксперимент уже закончился! ${result}", env.getObservation().toString())
                }
            }

            env.render()
        }

        task.get()
        println(env.getObservation())
    }
}