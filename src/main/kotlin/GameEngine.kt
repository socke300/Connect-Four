class GameEngine {

    var previouseMove: ArrayList<GameEngine> = ArrayList<GameEngine>()
    var savedMove: Int = 0
    var savedMove2: Int = 0
    var counter: Int = 0
    var bitBoardPlayerX: Long = 0L
    var bitBoardPlayerO: Long = 0L
    var height: IntArray = intArrayOf(0, 7, 14, 21, 28, 35, 42)
    var heightCounter: IntArray = intArrayOf(0, 0, 0, 0, 0, 0, 0)
    var winHashMap = HashMap<Int, Int>()

    constructor()

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


    fun calculateBestMoveLight(): Int {
        savedMove = 0
        if (givePlayer())
            max(10, Int.MIN_VALUE, Int.MAX_VALUE, bitBoardPlayerO)
        else
            max(10, Int.MIN_VALUE, Int.MAX_VALUE, bitBoardPlayerX)
        if (savedMove == 0)
            savedMove = monteCarlo()
        return savedMove
    }

    fun calculateBestMoveMedium(): Int {
        savedMove = 0
        if (winHashMap.containsKey(((bitBoardPlayerO.hashCode() + bitBoardPlayerX.hashCode()) * 31))) {
            savedMove = winHashMap.getValue(((bitBoardPlayerO.hashCode() + bitBoardPlayerX.hashCode()) * 31))
        }
      //  if (savedMove == 0) {
      //      if (givePlayer())
      //          max(0, Int.MIN_VALUE, Int.MAX_VALUE, bitBoardPlayerO)
      //      else
      //          max(0, Int.MIN_VALUE, Int.MAX_VALUE, bitBoardPlayerX)
      //  }
      //  if (savedMove == 0)
      //      savedMove = monteCarlo()
        return savedMove
    }

    fun calculateBestMoveHard(): Int {
        savedMove = 0
        //Datenbank
        return savedMove
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

    fun max(tiefe: Int, alpha: Int, beta: Int, bitboard: Long): Int {
        if (isWin(bitboard))
            return 1 + tiefe
        if (generatePossibleMoves().size == 0 || tiefe == 0)
            return -1
        var maxWert = alpha
        var firstValue = Int.MAX_VALUE
        val possibleMoves = generatePossibleMoves()
        for (i in possibleMoves.indices) {
            makeMove(possibleMoves[i])
            var giveBitBoard = bitBoardPlayerX
            if (givePlayer()) giveBitBoard = bitBoardPlayerO
            var wert = min(tiefe - 1, maxWert, beta, giveBitBoard)
            removeMove()
            if (wert > maxWert) {
                maxWert = wert
                if (tiefe == 10 && wert >= 1 && wert <= firstValue) {
                    savedMove = possibleMoves[i]
                    firstValue = wert
                }
                if (wert in 1..firstValue) {
                    firstValue = wert
                    winHashMap[((bitBoardPlayerO.hashCode() + bitBoardPlayerX.hashCode()))] = possibleMoves[i]
                }
                if (maxWert >= beta)
                    break
            }
        }
        return maxWert
    }

    fun min(tiefe: Int, alpha: Int, beta: Int, bitboard: Long): Int {
        if (isWin(bitboard) || generatePossibleMoves().size == 0 || tiefe == 0)
            return -1
        var minWert = beta
        val possibleMoves = generatePossibleMoves()
        for (i in possibleMoves.indices) {
            makeMove(possibleMoves[i])
            var giveBitBoard = bitBoardPlayerX
            if (givePlayer()) giveBitBoard = bitBoardPlayerO
            val wert = max(tiefe - 1, alpha, minWert, giveBitBoard)
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
