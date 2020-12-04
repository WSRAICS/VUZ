package ru.ncfu.selentar.view

import javafx.scene.layout.Pane
import ru.ncfu.selentar.environment.Direction
import ru.ncfu.selentar.environment.Position
import tornadofx.*

/**
 * Графический элемент для отображения карты симулятора и автомобиля на ней
 */
class EnvironmentView : View("EnvironmentUi") {
    override val root: Pane by fxml()

    private val columnSizes = listOf(40.0, 40.0, 100.0, 40.0, 40.0, 100.0, 40.0, 40.0, 100.0, 40.0, 40.0, 100.0, 40.0)
    private val rowSizes = listOf(40.0, 40.0, 100.0, 40.0, 100.0, 40.0, 40.0, 100.0, 40.0, 100.0, 40.0, 40.0)
    private val carHeight = 30.0
    private val carWidth = 16.0
    private val carImg = imageview("/img/Wcar.png") {
        fitHeight = carHeight
        preserveRatioProperty().value = true
    }

    /**
     * Отображает автомобиль в соответствии с переданной позицией
     * @param position координаты автомобиля
     * @param direction направление автомобиля
     */
    fun setCarAtPosition(position: Position, direction: Direction) {
        var biasX = 0.0
        var biasY = 0.0

        for (i in 0 until position.x) {
            biasX += columnSizes[i]
        }
        for (i in 0 until position.y) {
            biasY += rowSizes[i]
        }

        val xCellSize = columnSizes[position.x]
        val yCellSize = rowSizes[position.y]

        val rotation = when(direction) {
            Direction.NORTH -> 0.0
            Direction.EAST -> 90.0
            Direction.SOUTH -> 180.0
            Direction.WEST -> 270.0
        }

        carImg.x = biasX + xCellSize/2 - carWidth/2
        carImg.y = biasY + yCellSize/2 - carHeight/2
        carImg.rotate = rotation
    }

    init {
        root.add(carImg)
    }
}
