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

fun refreshSubModule(module: File) {

    (module cd "ktlib").also {

        it exec "git checkout dev"
        it exec "git fetch"
        it exec "git reset --hard FETCH_HEAD"

    }

}

project cd "ktlib" exec "git push origin dev -f"
refreshSubModule(project cd "pm")
project exec "git add . --all"