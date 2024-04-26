package spartacodingclub.nbcamp.kotlinspring.assignment

import spartacodingclub.nbcamp.kotlinspring.assignment.core.InteractiveManager

fun main() {
    val interactiveManager = InteractiveManager()
    val menuExit = 4

    while (interactiveManager.menu != menuExit){
        interactiveManager.showMenu()
        interactiveManager.selectMenu()

        interactiveManager.perform()
        println()
    }

    println("Shutting down...")
}