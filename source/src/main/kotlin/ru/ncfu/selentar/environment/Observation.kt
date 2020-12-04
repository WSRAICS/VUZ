package ru.ncfu.selentar.environment

/**
 * Данные, возвращаемые средой симуляции
 */
data class Observation(
    /**
     * Тип местности перед автомобилем
     */
    val forward: MapSurface,

    /**
     * Текущая координата автомобиля
     */
    val position: Position,

    /**
     * Текущее направление автомобиля
     */
    val direction: Direction,

    /**
     * true - если экспереминет законче
     */
    val canceled: Boolean,

    /**
     * true - если автомобиль достиг финиша
     */
    val finished: Boolean,

    /**
     * true - если автомобиль заехал под стоп знак, либо вышел за пределы дороги
     */
    val lost: Boolean,

    /**
     * Текущее время поездки
     */
    val time: Int,

    /**
     * Номер текущего эксперимент
     */
    val iteration: Int,

    /**
     * Количество поворотов, совершенных автомобилем в этом эксперименте
     */
    val turnsCount: Int

    )