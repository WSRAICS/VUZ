package ru.ncfu.selentar.domain

import javax.persistence.*

@Entity
@Table(name = "Learning_Records")
class LearningRecord(
    iteration: Int,
    time: Int,
    turnsCount: Int,
    finished: Boolean
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    var iteration = iteration

    var time = time

    var turnsCount = turnsCount

    var finished = finished
}