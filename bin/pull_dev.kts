import java.io.File

val project = File("..")

infix fun File.exec(shell: String) {

    println("[$canonicalPath] $shell")

    val process = ProcessBuilder(shell.split(" "))
            .directory(this)
            .redirectErrorStream(true)
            .start()

    process.inputStream.copyTo(System.out)

    val exitCode = process.waitFor()

    if (exitCode != 0) error("Exit code $exitCode")

}

infix fun File.cd(subDir: String): File {

    return File(this, subDir)

}

fun pull(module: File) {

    module exec "git checkout dev"
    module exec "git fetch"
    module exec "git reset --hard FETCH_HEAD"

}

pull(project cd "ktlib")
pull(project cd "pm")

project exec "git add . --all"