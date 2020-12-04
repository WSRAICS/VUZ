package ru.ncfu.selentar.app

import javafx.scene.image.Image
import ru.ncfu.selentar.DatabaseConfiguration
import ru.ncfu.selentar.view.MainView
import tornadofx.App


class MyApp: App(icon = Image("/img/WcarRight.png"), primaryView = MainView::class) {

    override fun stop() {
        super.stop()
        DatabaseConfiguration.SESSION_FACTORY.close()
    }
}