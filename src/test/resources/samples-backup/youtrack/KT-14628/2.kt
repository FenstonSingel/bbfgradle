// Original bug: KT-35045
// Duplicated bug: KT-14628

fun main() {
    println(Resources.b.cMp3)
}

open class ContentRoot(val path: String) {
    open inner class Directory(val name: String) : ContentRoot("${this@ContentRoot.path}/${name}")
    inner class File(val name: String, val path: String = "${this@ContentRoot.path}/${name}")
}

object Resources : ContentRoot("samples") {
    val aPng = File("a.png")

    object b : Directory("b") {
        val cMp3 = File("c.mp3")

        object x : Directory("x") {
            val cMp3 = "c.mp3"
        }
    }

    val bGif = File("b.gif")
}
