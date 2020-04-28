package com.example.demo.app

import com.example.demo.view.MainView
import javafx.stage.Stage
import tornadofx.App

class MyApp: App(MainView::class, Styles::class){

    fun main(){
        tornadofx.launch<MyApp>()
    }

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isResizable = false
    }
}