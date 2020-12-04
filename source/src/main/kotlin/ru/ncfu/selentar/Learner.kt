package ru.ncfu.selentar

import ru.ncfu.selentar.environment.Action
import ru.ncfu.selentar.environment.Observation
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

/**
 * Клас реазилует Q-обучение для автомобиля.
 * Цель автомобиля - добраться до виниша, не съезжая с дороги и не переская знаков стоп
 */
class Learner {

    private val learnRate = 0.1
    private val discount = 0.9
    private var epsilon = 1.0
    private val epsilonDecay = 0.9999
    private val randomizer = Random()

    /**
     * Текущая итерация обучения
     */
    val atomicIteration = AtomicInteger(0)

    /**
     * Q таблица для обучения
     * [y - координата][x - координата][направление машины][q-цена действия]
     * Инициализирована значением 20.0 для того, что бы сделать автомобиль более "любопытным"
     */
    private val qTable = Array(12) { Array(13) { Array(4) { Array(4) { 20.0 } } } }

    /**
     * Возвращает действие автомобиля для текущего состояния внешней среды
     * @param observation - состяоние внешенй среды
     * @param isLearning - false - отключает шанс случайной геренации действия
     */
    fun getAction(observation: Observation, isLearning: Boolean = true) : Action {
        if (!isLearning) return getActionFromQTable(observation)

        val action = if (randomizer.nextDouble() > epsilon) {
            getActionFromQTable(observation)
        } else {
            val actionIndex = randomizer.nextInt(4)
            Action.values()[actionIndex]
        }

        epsilon *= epsilonDecay
        return action
    }

    /**
     * Изменение q-значение таблицы. Реализация обучения машины.
     * @param oldObservation - наблюдения автомобиля на предыдущем ходе
     * @param madeAction - действие совершенное автомобилем
     * @param newObservation - наблюдения автомобиля после совершенного дейстия
     */
    fun learn(oldObservation: Observation, madeAction: Action, newObservation: Observation) {
        val currentQ = getQ(oldObservation, madeAction)
        val reward = calcReward(newObservation)
        val maxNewQ = getMaxQ(newObservation)

        val newQ = (1 - learnRate) * currentQ + learnRate * (reward + discount * maxNewQ)
        setQ(oldObservation, madeAction, newQ)
    }

    /**
     * Подсчет награды
     */
    private fun calcReward(newObservation: Observation) : Double {
        return when {
            newObservation.finished -> 100.0
            newObservation.lost -> -100.0
            else -> -1.0
        }
    }

    private fun getActionFromQTable(observation: Observation) : Action {
        val maxQ = getMaxQ(observation)
        for (action in Action.values()) {
            if (getQ(observation, action) == maxQ) {
                return action
            }
        }

        throw IllegalStateException("Что-то пошло сильно не так!")
    }

    private fun getMaxQ(observation: Observation) : Double {
        val qList = qTable[observation.position.y][observation.position.x][observation.direction.ordinal].toList()
        return Collections.max(qList)
    }

    private fun setQ(observation: Observation, action: Action, value: Double) {
        qTable[observation.position.y][observation.position.x][observation.direction.ordinal][action.ordinal] = value
    }

    private fun getQ(observation: Observation, action: Action) : Double {
        return qTable[observation.position.y][observation.position.x][observation.direction.ordinal][action.ordinal]
    }
}