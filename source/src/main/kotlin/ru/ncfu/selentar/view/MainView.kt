package ru.ncfu.selentar.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.concurrent.Task
import javafx.geometry.Pos
import ru.ncfu.selentar.DatabaseConfiguration
import ru.ncfu.selentar.Learner
import ru.ncfu.selentar.domain.LearningRecord
import ru.ncfu.selentar.domain.Record
import ru.ncfu.selentar.environment.Action
import ru.ncfu.selentar.environment.Environment
import tornadofx.*
import java.lang.IllegalStateException

class MainView : View("Worldskills_2020") {

    private val environmentUi = find<EnvironmentUi>()
    private val env = Environment(environmentUi)
    private val learner = Learner()

    private val visualizeSpeedProperty = SimpleLongProperty(1)
    private val pauseProperty = SimpleBooleanProperty(false)
    private val cancelledProperty = SimpleBooleanProperty(false)
    private val isLearningProperty = SimpleBooleanProperty(false)

    private val runDisableProperty = SimpleBooleanProperty(false)
    private val startLearnDisableProperty = SimpleBooleanProperty(false)
    private val stopLearnDisableProperty = SimpleBooleanProperty(true)
    private val testDisableProperty = SimpleBooleanProperty(false)
    private val leftDisableProperty = SimpleBooleanProperty(false)
    private val rightDisableProperty = SimpleBooleanProperty(false)
    private val forwardDisableProperty = SimpleBooleanProperty(false)
    private val turnDisableProperty = SimpleBooleanProperty(false)

    private val iterationProperty = SimpleIntegerProperty(1)
    private val learnerIterationProperty = SimpleIntegerProperty(0)
    private val timeProperty = SimpleIntegerProperty(0)
    private val bestTimeProperty = SimpleIntegerProperty(0)

    private var startTask: Task<*> = FXTask() {}
    private var testTask: Task<*> = FXTask() {}
    private var learnTask: Task<*> = FXTask() {}

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
                            while (!env.canceled && !startTask.isCancelled) {
                                doMove(learner.getAction(env.getObservation(), false))
                                Thread.sleep(visualizeSpeedProperty.value)
                            }

                            saveInDb(
                                Record(
                                    iteration = env.atomicIteration.get(),
                                    time = env.atomicTime.get(),
                                    turnsCount = env.turnsCount.get(),
                                    finished = env.finished
                                )
                            )
                        }
                    }
                }

                button("Стоп") {
                    prefWidth = 150.0
                    action {
                        startTask.cancel()
                        testTask.cancel()
                        learnTask.cancel()

                        reset()

                        runAsyncWithProgress {
                            Thread.sleep(1000)
                            pauseProperty.value = false
                            runDisableProperty.value = false
                            startLearnDisableProperty.value = false
                            stopLearnDisableProperty.value = true
                            testDisableProperty.value = false
                            leftDisableProperty.value = false
                            rightDisableProperty.value = false
                            forwardDisableProperty.value = false
                            turnDisableProperty.value = false
                        }
                    }
                }

                button("Пауза") {
                    prefWidth = 150.0
                    action {
                        if (pauseProperty.value == false) {
                            //Ставим на паузу
                            runDisableProperty.value = true
                            startLearnDisableProperty.value = true
                            testDisableProperty.value = true
                            leftDisableProperty.value = true
                            rightDisableProperty.value = true
                            forwardDisableProperty.value = true
                            turnDisableProperty.value = true
                        } else {
                            //Снимаем с паузы
                            runDisableProperty.value = false
                            startLearnDisableProperty.value = false
                            testDisableProperty.value = false
                            leftDisableProperty.value = false
                            rightDisableProperty.value = false
                            forwardDisableProperty.value = false
                            turnDisableProperty.value = false
                        }
                        pauseProperty.value = !pauseProperty.value
                    }
                }

                button("Запуск обучения") {
                    prefWidth = 150.0
                    disableProperty().bind(startLearnDisableProperty)

                    action {
                        learnTask = runAsyncWithProgress {
                            while (!learnTask.isCancelled) {
                                val iter = learner.atomicIteration.addAndGet(1)
                                runLater {
                                    learnerIterationProperty.value = iter
                                }

                                var observation = env.getObservation()
                                while (!observation.canceled) {

                                    runLater {
                                        isLearningProperty.value = true

                                        startLearnDisableProperty.value = true
                                        runDisableProperty.value = true
                                        testDisableProperty.value = true
                                        leftDisableProperty.value = true
                                        rightDisableProperty.value = true
                                        forwardDisableProperty.value = true
                                        turnDisableProperty.value = true
                                    }

                                    val action = learner.getAction(observation)
                                    doMove(action)

                                    val newObservation = env.getObservation()
                                    learner.learn(observation, action, newObservation)

                                    Thread.sleep(visualizeSpeedProperty.value)
                                    observation = newObservation
                                }

                                saveInDb(
                                    LearningRecord(
                                        iteration = env.atomicIteration.get(),
                                        time = env.atomicTime.get(),
                                        turnsCount = env.turnsCount.get(),
                                        finished = env.finished
                                    )
                                )


                                startTask.cancel()
                                testTask.cancel()
                                reset()
                            }
                        }
                    }
                }

                button("Остановка обучения") {
                    prefWidth = 150.0
                    disableProperty().bind(stopLearnDisableProperty)

                    action {
                        learnTask.cancel()

                        runAsyncWithProgress {
                            Thread.sleep(1000)

                            isLearningProperty.value = false

                            pauseProperty.value = false
                            runDisableProperty.value = false
                            startLearnDisableProperty.value = false
                            stopLearnDisableProperty.value = true
                            testDisableProperty.value = false
                            leftDisableProperty.value = false
                            rightDisableProperty.value = false
                            forwardDisableProperty.value = false
                            turnDisableProperty.value = false
                        }
                    }
                }

                button("Тест управления") {
                    prefWidth = 150.0
                    disableProperty().bind(testDisableProperty)
                    action {
                        testTask = runAsyncWithProgress {
                            doMove(Action.MOVE_FORWARD, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN_LEFT, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN_RIGHT, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN_RIGHT, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN_LEFT, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)

                            doMove(Action.MOVE_FORWARD, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)
                            doMove(Action.TURN, test = true)
                            Thread.sleep(visualizeSpeedProperty.value)

                            saveInDb(
                                Record(
                                    iteration = env.atomicIteration.get(),
                                    time = env.atomicTime.get(),
                                    turnsCount = env.turnsCount.get(),
                                    finished = env.finished
                                )
                            )
                        }
                    }
                }

                form {
                    fieldset {
                        field {
                            label("Итерация: ")
                            label(iterationProperty)
                        }

                        field {
                            label("Итерация обучения: ")
                            label(learnerIterationProperty)
                        }

                        field {
                            label("Время: ")
                            label(timeProperty)
                        }

                        field {
                            label("Лучшее время: ")
                            label(bestTimeProperty)
                        }

                        field {
                            vbox {
                                label("Скорость визуализации: ")
                                hbox {
                                    textfield(visualizeSpeedProperty)
                                    label(" мс.")
                                }
                            }
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
        DatabaseConfiguration.SESSION_FACTORY

        isLearningProperty.onChange {
            stopLearnDisableProperty.value = !it
        }
    }

    private fun reset() {
        env.reset()
        env.render()

        runLater {
            bestTimeProperty.value = env.atomicBestTime.get()
            iterationProperty.value = env.atomicIteration.get()
        }
    }

    private fun doMove(action: Action, test: Boolean = false) {
        val task = task {
            while (pauseProperty.value) {
                if (cancelledProperty.value) return@task
                if (isCancelled) return@task
                Thread.sleep(visualizeSpeedProperty.value)
            }

            try {
                env.step(action, test)
            } catch (e: IllegalStateException) {
                runLater {
                    val result = if (env.getObservation().finished) "Вы выйграли!" else "Вы проиграли!"
                    error("Эксперимент уже закончился! $result", env.getObservation().toString())
                }
            }

            env.render()

            runLater {
                timeProperty.value = env.atomicTime.get()
            }
        }

        task.get()
        //println("DEBUG: " + env.getObservation())
    }

    private fun saveInDb(record: Record) {
        DatabaseConfiguration.SESSION_FACTORY.openSession().use {
            it.save(record)
        }
    }

    private fun saveInDb(record: LearningRecord) {
        DatabaseConfiguration.SESSION_FACTORY.openSession().use {
            it.save(record)
        }
    }
}