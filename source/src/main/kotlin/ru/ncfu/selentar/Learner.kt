package ru.ncfu.selentar

import ru.ncfu.selentar.environment.Action
import ru.ncfu.selentar.environment.Observation
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.DoubleBinaryOperator

class Learner {

    val learnRate = 0.1
    val discount = 0.9
    var epsilon = 1.0
    val epsilonDecay = 0.9999
    val randomizer = Random()

    val atomicIteration = AtomicInteger(0)

    /**
     * Q таблица для обучения
     * [y - координата][x - координата][направление машины][q-цена действия]
     */
    val qTable = Array(12) { Array(13) { Array(4) { Array(4) { 20.0 } } } }

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

    fun learn(oldObservation: Observation, madeAction: Action, newObservation: Observation) {
        val currentQ = getQ(oldObservation, madeAction)
        val reward = calcReward(oldObservation, madeAction, newObservation)
        val maxNewQ = getMaxQ(newObservation)

        val newQ = (1 - learnRate) * currentQ + learnRate * (reward + discount * maxNewQ)
        setQ(oldObservation, madeAction, newQ)
    }

    private fun calcReward(oldObservation: Observation, madeAction: Action, newObservation: Observation) : Double {
        return if (newObservation.finished) 100.0
        else if (newObservation.lost) -100.0
        else -1.0
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