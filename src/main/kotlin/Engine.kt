interface Engine {

    fun calculateBestMoveLight(): Int
    fun calculateBestMoveMedium(): Int
    fun calculateBestMoveHard(): Int
    fun makeMove(col: Int)
    fun givePlayer(): Boolean
    fun monteCarlo(): String
    fun removeMove()
    fun isWin(bitboard: Long): Boolean
    fun generatePossibleMoves(): ArrayList<Int>
    fun max(tiefe: Int, alpha: Int, beta: Int, bitboard: Long): Int
    fun min(tiefe: Int, alpha: Int, beta: Int, bitboard: Long): Int
    override fun toString(): String
}
