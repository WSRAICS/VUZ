package ru.ncfu.selentar.environment

import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import ru.ncfu.selentar.domain.LearningRecord
import ru.ncfu.selentar.domain.Record
import java.util.*

enum class Action {
    MOVE_FORWARD,
    TURN_LEFT,
    TURN_RIGHT,
    TURN
}
