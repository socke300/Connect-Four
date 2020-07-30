import java.io.File

class GameEngine {

    var previouseMove: ArrayList<GameEngine> = ArrayList<GameEngine>()
    var savedMove: Int = 0
    var savedMove2: Int = 0
    var counter: Int = 0
    var bitBoardPlayerX: Long = 0L
    var bitBoardPlayerO: Long = 0L
    var height: IntArray = intArrayOf(0, 7, 14, 21, 28, 35, 42)
    var heightCounter: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    var winHashMap = HashMap<String, Pair<Int, Int>>()
    var favTiefe = 10

    constructor()

    constructor(winHashMap: HashMap<String, Pair<Int, Int>>) {
        this.winHashMap = winHashMap
    }

    constructor(
        savedMove: Int,
        savedMove2: Int,
        counter: Int,
        bitBoardPlayerX: Long,
        bitBoardPlayerO: Long,
        height: IntArray,
        heightCounter: IntArray
    ) {
        this.savedMove = savedMove
        this.savedMove2 = savedMove2
        this.counter = counter
        this.bitBoardPlayerX = bitBoardPlayerX
        this.bitBoardPlayerO = bitBoardPlayerO
        this.height = height
        this.heightCounter = heightCounter
    }

    constructor(
        savedMove: Int,
        savedMove2: Int,
        counter: Int,
        bitBoardPlayerX: Long,
        bitBoardPlayerO: Long,
        height: IntArray,
        heightCounter: IntArray,
        winHashMap: HashMap<String, Pair<Int, Int>>,
        favTiefe: Int
    ) {
        this.savedMove = savedMove
        this.savedMove2 = savedMove2
        this.counter = counter
        this.bitBoardPlayerX = bitBoardPlayerX
        this.bitBoardPlayerO = bitBoardPlayerO
        this.height = height
        this.heightCounter = heightCounter
        this.winHashMap = winHashMap
        this.favTiefe = favTiefe
    }

    fun calculateBestMove(): Int {
        savedMove = -1
        var key = ("$bitBoardPlayerO:$bitBoardPlayerX")
        if (winHashMap.containsKey(key) && winHashMap[key]!!.second >= counter) {
            savedMove = winHashMap[key]!!.first
            //print("\n Hashmap = $savedMove")
        }
        if (savedMove == -1) {
            max(favTiefe, Int.MIN_VALUE, Int.MAX_VALUE)
            //print("\n Minimax = $savedMove")
        }
        if (savedMove == -1) {
            savedMove = monteCarlo()
            //print("\n Monte = $savedMove")
        }
        return savedMove
    }

    fun generateHashmap() {
        for (i in 0..5) {
            print("\n Hashmap = ${winHashMap.size}")
            print("\n Spiel I = $i")
            var temp = 0
            while (!isWin(bitBoardPlayerO) && !isWin(bitBoardPlayerX) && counter != 42) {
                makeMove(calculateBestMove())
                makeMove(generatePossibleMoves().shuffled()[0])
                temp += 2
            }
            for (i in 1..temp)
                removeMove()
        }
        for (i in 0..5) {
            print("\n Hashmap = ${winHashMap.size}")
            print("\n Spiel II = $i")
            var temp = 0
            while (!isWin(bitBoardPlayerO) && !isWin(bitBoardPlayerX) && counter != 42) {
                makeMove(generatePossibleMoves().shuffled()[0])
                makeMove(calculateBestMove())
                temp += 2
            }
            for (i in 1..temp)
                removeMove()
        }
        for (i in 0..5) {
            print("\n Hashmap = ${winHashMap.size}")
            print("\n Spiel III = $i")
            var temp = 0
            while (!isWin(bitBoardPlayerO) && !isWin(bitBoardPlayerX) && counter != 42) {
                makeMove(calculateBestMove())
                temp += 1
            }
            for (i in 1..temp)
                removeMove()
        }
        saveHashmap()
    }

    fun giveHashmap() {
        var temp = ArrayList<String>()
        File("data.txt").forEachLine { temp.add(it) }
        for (i in temp.indices) {
            val elements = temp[i].split(",")
            val key = elements[0]
            winHashMap[key] = Pair(elements[1].trim().toInt(), elements[2].trim().toInt())
        }
    }

    fun saveHashmap() {
        File("data.txt").printWriter().use { out ->
            winHashMap.forEach {
                out.println("${it.key}, ${it.value.first}, ${it.value.second}")
            }
        }
    }

    fun makeMove(col: Int): Boolean {
        if (heightCounter[col] < 6) {
            previouseMove.add(
                GameEngine(
                    savedMove,
                    savedMove2,
                    counter,
                    bitBoardPlayerX,
                    bitBoardPlayerO,
                    height.copyOf(),
                    heightCounter.copyOf()
                )
            )
            savedMove2 = height[col]
            val move = 1L shl height[col]++
            heightCounter[col]++
            if (givePlayer()) bitBoardPlayerO = bitBoardPlayerO xor move
            else bitBoardPlayerX = bitBoardPlayerX xor move
            counter++
            return true
        }
        return false
    }

    fun givePlayer(): Boolean {
        if (counter % 2 == 0) return true     //true ist O
        return false                        //false ist X
    }

    fun monteCarlo(): Int {
        var winArray = ArrayList<Int>()
        var possibleMoves = generatePossibleMoves()
        var player = givePlayer()
        for (i in possibleMoves.indices) {
            makeMove(possibleMoves[i])
            winArray.add(0)
            for (k in 0..10000) {
                var temp = 0
                while (generatePossibleMoves().size != 0) {
                    var giveBitBoard = bitBoardPlayerX
                    if (player) giveBitBoard = bitBoardPlayerO
                    if (isWin(giveBitBoard)) {
                        winArray[i] = winArray[i] + 1
                        break
                    }
                    giveBitBoard = bitBoardPlayerX
                    if (!player) giveBitBoard = bitBoardPlayerO
                    if (isWin(giveBitBoard))
                        break

                    makeMove(generatePossibleMoves().shuffled()[0])
                    temp++

                    giveBitBoard = bitBoardPlayerX
                    if (player) giveBitBoard = bitBoardPlayerO
                    if (isWin(giveBitBoard)) {
                        winArray[i] = winArray[i] + 1
                        break
                    }
                    giveBitBoard = bitBoardPlayerX
                    if (!player) giveBitBoard = bitBoardPlayerO
                    if (isWin(giveBitBoard))
                        break
                }
                for (j in 1..temp)
                    removeMove()
            }
            removeMove()
        }
        var move = 0
        var temp = 0
        for (i in winArray.indices) {
            if (winArray[i] >= temp) {
                temp = winArray[i]
                move = i
            }
        }
        return possibleMoves[move]
    }

    fun removeMove() {
        this.bitBoardPlayerX = previouseMove[previouseMove.size - 1].bitBoardPlayerX
        this.bitBoardPlayerO = previouseMove[previouseMove.size - 1].bitBoardPlayerO
        this.height = previouseMove[previouseMove.size - 1].height
        this.heightCounter = previouseMove[previouseMove.size - 1].heightCounter
        this.counter = previouseMove[previouseMove.size - 1].counter
        this.savedMove = previouseMove[previouseMove.size - 1].savedMove
        this.savedMove2 = previouseMove[previouseMove.size - 1].savedMove2
        this.previouseMove.removeAt(previouseMove.size - 1)
    }

    fun mirrorBitboard(bitboard: Long): Long{
        var arr = longArrayOf(0b0000000_0000000_0000000_0000000_0000000_0000000_0111111L, 0b0000000_0000000_0000000_0000000_0000000_0111111_0000000L, 0b0000000_0000000_0000000_0000000_0111111_0000000_0000000L, 0b0000000_0000000_0000000_0111111_0000000_0000000_0000000L, 0b0000000_0000000_0111111_0000000_0000000_0000000_0000000L, 0b0000000_0111111_0000000_0000000_0000000_0000000_0000000L, 0b0111111_0000000_0000000_0000000_0000000_0000000_0000000L)
        val bitboardMirror = ((arr[0] and bitboard)shl(42)) xor ((arr[1] and bitboard)shl(28)) xor ((arr[2] and bitboard)shl(14)) xor (arr[3] and bitboard) xor ((arr[4] and bitboard)shr(14)) xor ((arr[5] and bitboard)shr(28)) xor ((arr[6] and bitboard)shr(42))
        return bitboardMirror
    }

    fun isWin(bitboard: Long): Boolean {
        val directions = intArrayOf(1, 7, 6, 8)
        for (direction in directions) if (bitboard and (bitboard shr direction) and (bitboard shr (2 * direction)) and (bitboard shr (3 * direction)) != 0L)
            return true
        return false
    }

    fun generatePossibleMoves(): ArrayList<Int> {
        var moves = ArrayList<Int>()
        val top = 0b1000000_1000000_1000000_1000000_1000000_1000000_1000000L
        for (col in 0..6) {
            if (top and (1L shl height[col]) == 0L) moves.add(col)
        }
        return moves
    }

    fun giveBitboardPlayer(player: Boolean): Long {
        var giveBitBoard = bitBoardPlayerX
        if (player) giveBitBoard = bitBoardPlayerO
        return giveBitBoard
    }

    fun max(tiefe: Int, alpha: Int, beta: Int): Int {
        if (isWin(giveBitboardPlayer(givePlayer())))
            return 1 + tiefe
        if (generatePossibleMoves().size == 0 || tiefe == 0 || isWin(giveBitboardPlayer(!givePlayer())))
            return -1
        var maxWert = alpha
        var firstValue = Int.MIN_VALUE
        val possibleMoves = generatePossibleMoves().shuffled()
        for (i in possibleMoves.indices) {
            makeMove(possibleMoves[i])
            var wert = min(tiefe - 1, maxWert, beta)
            removeMove()
            if (wert > maxWert) {
                maxWert = wert
                if (tiefe == favTiefe && wert >= 1) {
                    savedMove = possibleMoves[i]
                    winHashMap[("$bitBoardPlayerO:$bitBoardPlayerX")] = Pair(possibleMoves[i], counter + tiefe)
                    winHashMap[("${mirrorBitboard(bitBoardPlayerO)}:${mirrorBitboard(bitBoardPlayerX)}")] = Pair(possibleMoves[i], counter + tiefe)
                }
                if (wert >= 1 && wert >= firstValue) {
                    firstValue = wert
                    winHashMap[("$bitBoardPlayerO:$bitBoardPlayerX")] = Pair(possibleMoves[i], counter + tiefe)
                    winHashMap[("${mirrorBitboard(bitBoardPlayerO)}:${mirrorBitboard(bitBoardPlayerX)}")] = Pair(possibleMoves[i], counter + tiefe)
                }
                if (maxWert >= beta)
                    break
            }
        }
        return maxWert
    }

    fun min(tiefe: Int, alpha: Int, beta: Int): Int {
        if (isWin(giveBitboardPlayer(givePlayer())) || generatePossibleMoves().size == 0 || tiefe == 0)
            return -1
        if (isWin(giveBitboardPlayer(!givePlayer())))
            return 1 + tiefe
        var minWert = beta
        val possibleMoves = generatePossibleMoves().shuffled()
        for (i in possibleMoves.indices) {
            makeMove(possibleMoves[i])
            val wert = max(tiefe - 1, alpha, minWert)
            removeMove()
            if (wert < minWert) {
                minWert = wert
                if (minWert <= alpha)
                    break
            }
        }
        return minWert
    }
}
