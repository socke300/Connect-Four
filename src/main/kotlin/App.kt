import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import java.math.RoundingMode
import java.text.DecimalFormat

class App {
    init {
        var game = GameEngine()

        var app: Javalin = Javalin.create() { t: JavalinConfig -> t.addStaticFiles("index.html") }.start(7070)

        app.get("/drop") { ctx ->
            var temp = game.height[ctx.queryParam("id")!!.toInt()]
            if (game.makeMove(ctx.queryParam("id")!!.toInt())) {
                if (game.givePlayer()) {
                    ctx.result("$temp,#4CAF50")
                } else {
                    ctx.result("$temp,#f50000")
                }
            }
        }

        app.get("/computerTurn") { ctx ->
            if (ctx.queryParam("id")!!.toInt() == 1) {
                game.calculateBestMoveLight()
            }
            if (ctx.queryParam("id")!!.toInt() == 2) {
                game.calculateBestMoveMedium()
            }
            var temp = game.height[game.savedMove]
            game.makeMove(game.savedMove)
            if (game.givePlayer())
                ctx.result("$temp,#4CAF50")
            else
                ctx.result("$temp,#f50000")

        }

        app.get("/undoTurn") { ctx ->
            if (game.counter > 0) {
                var temp = game.savedMove2.toString()
                game.removeMove()
                ctx.result(temp)
            }
        }

        app.get("/test") { ctx ->
        }
    }
}

fun main() {
    App()
}
