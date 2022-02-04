interface Engine {
    fun min(tiefe: Int, alpha: Int, beta: Int): Int
    fun max(tiefe: Int, alpha: Int, beta: Int): Int
    fun giveBitboardPlayer(player: Boolean): Long
    fun generatePossibleMoves(): ArrayList<Int>
    fun isWin(bitboard: Long): Boolean
    fun mirrorMove(move: Int): Int
    fun mirrorBitboard(bitboard: Long): Long
    fun removeMove()
    fun monteCarlo(): Int
    fun givePlayer(): Boolean
    fun makeMove(col: Int): Boolean
    fun saveHashmap()
    fun giveHashmap()
    fun generateHashmap()
    fun calculateBestMove(): Int
}
