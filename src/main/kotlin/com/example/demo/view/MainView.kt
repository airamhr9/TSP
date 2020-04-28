package com.example.demo.view

import com.jfoenix.controls.JFXButton
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import kfoenix.jfxbutton
import org.intellij.lang.annotations.JdkConstants
import tornadofx.*
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Thread.sleep
import java.util.*
import javax.imageio.ImageIO

class MainView : View("TSP") {
    val imageConstructor: ImageConstructor = ImageConstructor(null, null)
    lateinit var errorMessage: javafx.scene.control.Label
    lateinit var sourceButton : JFXButton
    lateinit var targetButton : JFXButton

    val isPNG = Optional.of("png")
    val isJPG = Optional.of("jpg")
    val isJPEG = Optional.of("jpeg")

    override val root = vbox(20) {
        paddingTop = 20
        paddingLeft = 20
        paddingRight = 20
        paddingBottom = 20

        label("TSP") {
            style {
                fontSize = 20.px
                fontWeight = FontWeight.BOLD
            }
        }
        alignment = Pos.CENTER
        form {
            fieldset {
                field("Source Directory") {
                    sourceButton = jfxbutton("Select", JFXButton.ButtonType.RAISED) {
                        action {
                            imageConstructor.sourceDirectory = chooseDirectory("Select Source Directory")
                            if (imageConstructor.sourceDirectory != null) {
                                this.disableProperty().set(true)
                            }
                        }
                        style {
                            backgroundColor += Color.valueOf("#2196f3")
                        }
                    }
                }

                field("Target Directory") {
                    targetButton =   jfxbutton("Select", JFXButton.ButtonType.RAISED) {
                        action {
                            imageConstructor.targetDirectory = chooseDirectory("Select Target Directory")
                            if (imageConstructor.targetDirectory != null) {
                                this.disableProperty().set(true)
                            }
                        }
                        style {
                            backgroundColor += Color.valueOf("#2196f3")
                        }
                    }
                }
            }
        }


        jfxbutton("Create images", JFXButton.ButtonType.RAISED) {
            action {
                if (imageConstructor.somethingIsNull()) {
                    errorMessage.text = "You must select both directories"
                    errorMessage.textFill = Color.RED
                    errorMessage.visibleProperty().set(true)
                } else {
                    var i = 0
                    if(!errorMessage.isVisible)
                        errorMessage.isVisible = true
                    errorMessage.textFill = Color.ORANGE
                    errorMessage.text = "Processing"
                    println(imageConstructor.sourceDirectory == null)
                    imageConstructor.sourceDirectory!!.walk().forEach {
                        val extension = getFileExtension(it.name)
                        when (getFileExtension(it.name)) {
                            isPNG, isJPEG, isJPG -> {
                                processImages(extension!!.get(), imageConstructor.targetDirectory!!, i, it)
                                i++
                            }
                        }
                    }

                    errorMessage.text = "Completed!"
                    errorMessage.textFill = Color.GREEN
                    imageConstructor.setNull()
                    targetButton.isDisable = false
                    sourceButton.isDisable = false
                }
            }
            style {
                backgroundColor += Color.valueOf("#2196f3")
            }
        }

        errorMessage = label("You must select both directories") {
            visibleProperty().set(false)
            style {
                textFill = Color.DARKRED
            }
        }


    }

}


data class ImageConstructor(var sourceDirectory : File?, var targetDirectory : File?){
    fun somethingIsNull() = sourceDirectory == null || targetDirectory == null
    fun setNull(){
        sourceDirectory = null
        targetDirectory = null
    }
}

fun getFileExtension(filename: String): Optional<String>? {
    return Optional.ofNullable(filename)
            .filter { f: String -> f.contains(".") }
            .map { f: String ->
                f.substring(
                        filename.lastIndexOf(".") + 1
                )
            }
}

fun processImages(extension : String, newFolder : File, i : Int, imageFile: File){
    val inputImage : BufferedImage = ImageIO.read(imageFile)
    val outputImage : BufferedImage
    outputImage = if(extension == "png") {
        BufferedImage(512, 512, 5)
    }
    else{
        BufferedImage(512, 512, inputImage.type)
    }

    val g2d = outputImage.createGraphics()
    g2d.drawImage(inputImage, 0, 0, 512, 512, null)
    g2d.dispose()

    newFolder.mkdir()

    ImageIO.write(outputImage, extension, File(newFolder.absolutePath + "/$i"));
}