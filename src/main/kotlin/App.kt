import io.javalin.Javalin
import io.javalin.core.JavalinConfig
import kotlin.system.measureTimeMillis

class App {
    init {
        var game = GameEngine()
        game.giveHashmap()

        //game.generateHashmap()

        var app: Javalin = Javalin.create() { t: JavalinConfig -> t.addStaticFiles("public/") }.start(7070)

        app.get("/drop") { ctx ->
            if (!game.isWin(game.bitBoardPlayerO) && !game.isWin(game.bitBoardPlayerX)) {
                var temp = game.height[ctx.queryParam("id")!!.toInt()]
                if (game.makeMove(ctx.queryParam("id")!!.toInt())) {
                    if (game.givePlayer()) {
                        ctx.result("$temp,#4CAF50")
                    } else {
                        ctx.result("$temp,#f50000")
                    }
                }
            }
        }

        app.get("/computerTurn") { ctx ->
            if (!game.isWin(game.bitBoardPlayerO) && !game.isWin(game.bitBoardPlayerX)) {
                var temp = 0
                var time = measureTimeMillis {
                    game.calculateBestMove()
                    temp = game.height[game.savedMove]
                    game.makeMove(game.savedMove)
                }
                if (game.givePlayer())
                    ctx.result("$temp,#4CAF50,$time")
                else
                    ctx.result("$temp,#f50000,$time")

            }
        }

        app.get("/undoTurn") { ctx ->
            if (game.counter > 0) {
                var temp = game.savedMove2.toString()
                game.removeMove()
                ctx.result(temp)
            }
        }

        app.get("/info") { ctx ->
            var temp = ""
            if (game.givePlayer())
                temp = "Rot,"
            else
                temp = "Grün,"
            if (game.isWin(game.bitBoardPlayerO))
                temp += "Rot"
            if (game.isWin(game.bitBoardPlayerX))
                temp += "Grün"
            if (!game.isWin(game.bitBoardPlayerX) && !game.isWin(game.bitBoardPlayerO))
                temp += "Niemand"
            ctx.result(temp)
        }

        app.get("/clearAll") { ctx ->
            game = GameEngine(game.winHashMap)
        }

        app.get("/test") { ctx ->
            if (ctx.queryParam("id")!!.toInt() == 1) {
                var game2 = GameEngine(game.winHashMap)
                game2.makeMove(0)
                game2.makeMove(2)
                game2.makeMove(0)
                game2.makeMove(3)
                game2.makeMove(0)
                game2.makeMove(3)
                print("\n\n- Test 1: Start -\nAusgangssituation: \n---------\n|.......|\n|.......|\n|.......|\n|O......|\n|O..X...|\n|O.XX...|\n---------\nEngine (O) macht den Besten Zug, in Spalte (1-7): " + (game2.calculateBestMove() + 1) + ", da er dann Gewonnen hat.\nGewonnen O: ")
                game2.makeMove(game2.calculateBestMove())
                print("" + game2.isWin(game2.bitBoardPlayerO) + "\n- Test 1: Ende -\n")
            }
            if (ctx.queryParam("id")!!.toInt() == 2) {
                var game2 = GameEngine(game.winHashMap)
                game2.makeMove(0)
                game2.makeMove(1)
                game2.makeMove(1)
                game2.makeMove(2)
                game2.makeMove(3)
                game2.makeMove(2)
                game2.makeMove(1)
                game2.makeMove(0)
                game2.makeMove(0)
                game2.makeMove(3)
                print("\n\n- Test 2: Start -\nAusgangssituation: \n---------\n|.......|\n|.......|\n|.......|\n|OO.....|\n|XOXX...|\n|OXXO...|\n---------" + "\nEngine (O) macht den Besten Zug, in Spalte (1-7): " + (game2.calculateBestMove() + 1) + ", da er dann nach 2 weiteren abwechselnden Zügen Gewonnen hat.\nGewonnen O: ")
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                print("" + game2.isWin(game2.bitBoardPlayerO) + "\n- Test 2: Ende -\n")

            }
            if (ctx.queryParam("id")!!.toInt() == 3) {
                var game2 = GameEngine(game.winHashMap)
                game2.makeMove(0)
                game2.makeMove(1)
                game2.makeMove(0)
                game2.makeMove(2)
                game2.makeMove(1)
                game2.makeMove(3)
                game2.makeMove(4)
                game2.makeMove(4)
                game2.makeMove(0)
                game2.makeMove(0)
                game2.makeMove(1)
                game2.makeMove(1)
                print("\n\n- Test 3: Start -\nAusgangssituation: \n---------\n|.......|\n|.......|\n|XX.....|\n|OO.....|\n|OO..X..|\n|OXXXO..|\n---------" + "\nEngine (O) macht den Besten Zug, in Spalte (1-7): " + (game2.calculateBestMove() + 1) + ", da er dann nach 4 weiteren abwechselnden Zügen Gewonnen hat.\nGewonnen O: ")
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                print("" + game2.isWin(game2.bitBoardPlayerO) + "\n- Test 3: Ende -\n")
            }
            if (ctx.queryParam("id")!!.toInt() == 4) {
                var game2 = GameEngine(game.winHashMap)
                game2.makeMove(0)
                game2.makeMove(6)
                game2.makeMove(0)
                game2.makeMove(5)
                game2.makeMove(1)
                game2.makeMove(3)
                print(
                    "\n\n- Test 4: Start -\nAusgangssituation: \n---------\n|.......|\n|.......|\n|.......|\n|.......|\n|O......|\n|OO.X.XX|\n---------" + "\nEngine (O) macht den Besten Zug, in Spalte (1-7): " + (game2.calculateBestMove() + 1) + ", da er dann den Gegner am gewinnen gehindert hat, nach 1 weiteren Zug.\nGewonnen X: "
                )
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                print("" + game2.isWin(game2.bitBoardPlayerX) + "\n- Test 4: Ende -\n")
            }
            if (ctx.queryParam("id")!!.toInt() == 5) {
                var game2 = GameEngine(game.winHashMap)
                game2.makeMove(3)
                game2.makeMove(0)
                game2.makeMove(3)
                game2.makeMove(1)
                game2.makeMove(0)
                game2.makeMove(2)
                game2.makeMove(1)
                game2.makeMove(2)
                game2.makeMove(1)
                game2.makeMove(2)
                game2.makeMove(2)
                print("\n\n- Test 5: Start -\nAusgangssituation: \n---------\n|.......|\n|.......|\n|..O....|\n|.OX....|\n|OOXO...|\n|XXXO...|\n---------" + "\nEngine (O) macht den Besten Zug, in Spalte (1-7): " + (game2.calculateBestMove() + 1) + ", da er dann den Gegner am gewinnen gehindert hat, nach 3 weiteren Zügen.\nGewonnen X: ")
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                game2.makeMove(game2.calculateBestMove())
                print("" + game2.isWin(game2.bitBoardPlayerX) + "\n- Test 5: Ende -\n")
            }
        }
    }
}

fun main() {
    App()
}
