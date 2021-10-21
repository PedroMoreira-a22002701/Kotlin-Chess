import sun.security.provider.ConfigFile
import kotlin.math.absoluteValue

fun main () {
    var numColumns = 0
    var numLines = 0
    var totalPiecesAndTurn: Array<Int>
    var pieces: Array<Pair<String,String>?>
    val msgErr= "Invalid response."
    val playerText = arrayOf("First player name?","Second player name?")
    val playerName = arrayOf(" "," ")
    val optionsText = arrayOf("Show legend (y/n)?\n" ,"Show pieces (y/n)?\n")
    val optionsFlag = arrayOf(false,false)
    print("Welcome to the Chess Board Game!\n")
    while (true) {
        println(buildMenu())
        val menuOpc = (readLine().toString())
        if (menuOpc == "2") {
            return
        }
        if(menuOpc != "1" && menuOpc != "2") {
            println(msgErr)
        } else {
            // Get White Player (playerName[0]) and Black Player (playerName[1]) names
            for (pos in 0..1) {
                println(playerText[pos] + "\n")
                playerName[pos] = readLine()!!
                if (checkName(playerName[pos]) == false) {
                    do {
                        println(msgErr + "\n" + playerText[pos] + "\n")
                        playerName[pos] = readLine()!!
                    } while (checkName(playerName[pos]) != true)
                }
            }
            // Get chess columns and lines
            do {
                do {
                    println("How many chess columns?\n")
                    val numero = readLine().toString()
                    if (checkIsNumber(numero) == true) {
                        numColumns = numero.toInt()
                    }
                    if (!(numColumns >= 4 && numColumns <= 8)) {
                        println(msgErr)
                    }
                } while (!(numColumns >= 4 && numColumns <= 8))
                println("How many chess lines?\n")
                val numero = readLine().toString()
                if (checkIsNumber(numero) == true) {
                    numLines = numero.toInt()
                }
                if (!(numLines >= 4 && numLines <= 8)) {
                    println(msgErr)
                }
            } while (!(numLines >= 4 && numLines <= 8))

            // Get Display Legends (optionsFlag[0]) and Display Pieces optionsFlag[1] boolean values
            var inputChoice = ""
            var inputPiece = ""
            for (pos in 0..1) {
                do {
                    println(optionsText[pos])
                    inputChoice = readLine().toString()
                    if (showChessLegendOrPieces(inputChoice) == null) {
                        println(msgErr)
                    }
                } while (showChessLegendOrPieces(inputChoice) == null)
                optionsFlag[pos] = showChessLegendOrPieces(inputChoice) ?: false
            }
            do {
                println("Do you want to play with only one type of piece on the board? (y/n)?")
                inputChoice = readLine().toString()
                wantSpecificPiece(inputChoice)
                if (wantSpecificPiece(inputChoice) == null) {
                    println(msgErr)
                }
            } while (wantSpecificPiece(inputChoice) == null)
            do {
                println("Choose a specific piece.")
                inputPiece = readLine().toString()
                isASpecificPieceValid(inputPiece)
                if (isASpecificPieceValid(inputPiece) == false) {
                    println(msgErr)
                }

            } while (isASpecificPieceValid(inputPiece) == false && wantSpecificPiece(inputChoice) == true)

            
            // Initialize and Start Game
            pieces = createInitialBoard(numColumns, numLines)
            totalPiecesAndTurn = createTotalPiecesAndTurn(numColumns, numLines)
            replaceWithSpecificPiece(pieces,inputPiece)
            startNewGame(
                playerName[0], playerName[1],
                pieces, totalPiecesAndTurn, numColumns, numLines, optionsFlag[0], optionsFlag[1])
        }
    }
}

// Verifica se Nome está correto
fun checkName(number: String): Boolean {
    var count = 0
    var controlo = 0
    val numletras = number.length - 1
    if (numletras < 2 ) {
        return false
    }
    if (number[count] < 'A' || number[count] > 'Z') {
        return false
    }
    count++
    while (controlo == 0 && count != numletras) {
        if (number[count] == ' ') {
            controlo = count
        }
        count++
    }
    if (number[controlo + 1] < 'A' || number[controlo + 1] > 'Z') {
        return false
    }
    return true
}

//Iniciar jogo
fun buildMenu(): String = ("""
    1-> Start New Game;
    2-> Exit Game.
    
""".trimIndent())

//
fun showChessLegendOrPieces(message: String): Boolean? {
    when(message.toUpperCase()){
        "Y" -> return true
        "N" -> return false
        else -> return null
    }
}

// Verificar se é um número
fun checkIsNumber(number: String): Boolean {
    val number1 =  number.toIntOrNull()
    if (number1 == null) {
        return false
    } else return number1 in 0..10
}

// Game loop
fun startNewGame(whitePlayer: String, blackPlayer: String,
    pieces: Array<Pair<String, String>?>,
    totalPiecesAndTurn: Array<Int>,
    numColumns: Int, numLines: Int,
    showLegend: Boolean= false,
    showPieces: Boolean = false) {
    val msgError = "Invalid response."
    val tabPlayers = arrayListOf(whitePlayer , blackPlayer)
    var currentCoord = Pair(0,0)
    var targetCoord = Pair(0,0)
    var inputFlow = false
    var inputCoord: Pair<Int, Int>?
    var inputCoordPos = 0
    var inputStr = ""
    var pieceColor = ""

    while (totalPiecesAndTurn[0] > 0 && totalPiecesAndTurn[1] > 0) {
        println(buildBoard(numColumns, numLines, showLegend, showPieces, pieces))
        inputFlow = false
        while (!inputFlow) {
            println(tabPlayers[totalPiecesAndTurn[2]] + ", choose a piece (e.g 2D).")
            println("Menu-> m;\n")
            inputStr = readLine().toString().replace(" ", "")
            if (inputStr.toUpperCase() == "M") {
                return
            }
            inputCoord = getCoordinates(inputStr)
            if (inputCoord != null) {
                if (isCoordinateInsideChess(inputCoord, numColumns, numLines)) {
                    inputCoordPos = getArrayPos(inputCoord, numColumns)
                    if (pieces[inputCoordPos] != null) {
                        pieceColor = pieces[inputCoordPos]!!.second
                        if (checkRightPieceSelected(pieceColor, totalPiecesAndTurn[2])) {
                            currentCoord = inputCoord
                            inputFlow = true
                        }
                    }
                }
            }
            if (!inputFlow) {
                println(msgError)
                println(buildBoard(numColumns, numLines, showLegend, showPieces, pieces))
            }
        }
        inputFlow = false
        while (!inputFlow) {
            println(tabPlayers[totalPiecesAndTurn[2]] + ", choose a target piece (e.g 2D).")
            println("Menu-> m;\n")
            inputStr = readLine().toString().replace(" ", "")
            if (inputStr.toUpperCase() == "M") {
                return
            }
            inputCoord = getCoordinates(inputStr)
            if (inputCoord != null) {
                if (isCoordinateInsideChess(inputCoord, numColumns, numLines)) {
                    targetCoord = inputCoord
                    if (movePiece(pieces, numColumns, numLines, currentCoord, targetCoord, totalPiecesAndTurn)) {
                        inputFlow = true
                    }
                }
            }
            if (!inputFlow) {
                println(msgError)
                inputFlow = true
            }
        }
    }
    if (totalPiecesAndTurn[1] == 0) {
        println("Congrats! $whitePlayer wins!")
    }
    if (totalPiecesAndTurn[2] == 0) {
        println("Congrats! $blackPlayer wins!")
    }
}


// Move peça (se movimeto válido)
fun movePiece(
    pieces: Array<Pair<String, String>?>,
    numColumns: Int,
    numLines: Int,
    currentCoord: Pair<Int, Int>,
    targetCoord: Pair<Int, Int>,
    totalPiecesAndTurn: Array<Int>)
    : Boolean {

    val currentCoordArrayPos = getArrayPos(currentCoord , numColumns)
    val targetCoordArrayPos = getArrayPos(targetCoord , numColumns)
    val currentSelectedPiece = pieces[ getArrayPos(currentCoord , numColumns)]
    if (isValidTargetPiece(currentSelectedPiece,currentCoord,targetCoord,pieces,numColumns,numLines)) {
        pieces[targetCoordArrayPos] = pieces[currentCoordArrayPos]
        pieces[currentCoordArrayPos] = null
        totalPiecesAndTurn[0] = 0
        totalPiecesAndTurn[1] = 0
        for (pos in 0..(numColumns * numLines) -1) {
            if (pieces[pos]?.second == "w") {
                totalPiecesAndTurn[0]++
            }
            if (pieces[pos]?.second == "b") {
                totalPiecesAndTurn[1]++
            }
        }
        totalPiecesAndTurn[2] = (totalPiecesAndTurn[2] - 1).absoluteValue // alterna entre "0" e "1"
        return true
    }
    return false
}



// Cria o Array com o numero de peças para o jogador 1, jogador 2, turno
fun createTotalPiecesAndTurn(
    numColumns: Int,
    numLines: Int)
    : Array<Int> {

    var numPiecesPerPlayer = 0
    if (numLines == 8 && numColumns == 8){
        numPiecesPerPlayer = 16
    }
    if (numLines == 7 && numColumns == 7){
        numPiecesPerPlayer = 14
    }
    if (numLines == 6 && numColumns == 6){
        numPiecesPerPlayer = 12
    }
    if (numLines == 7 && numColumns == 6){
        numPiecesPerPlayer = 12
    }
    if (numLines == 4 && numColumns == 4){
        numPiecesPerPlayer = 2
    }
    if (numPiecesPerPlayer > 0){
        return arrayOf(numPiecesPerPlayer, numPiecesPerPlayer, 0)
    }
    return emptyArray()
}

fun wantSpecificPiece(message:
                      String): Boolean? {
    when (message.toUpperCase()) {
        "Y" -> return true
        "N" -> return false
    }
    return null
}

fun isASpecificPieceValid(piece:
                              String): Boolean {
    when (piece.toUpperCase()) {
        "P" -> return true
        "H" -> return true
        "T" -> return true
        "B" -> return true
        "Q" -> return true
        "K" -> return true
    }
    return false
}
fun replaceWithSpecificPiece(pieces: Array<Pair<String,String>?>,
                                 piece: String) {
    for (arrayPos in 0..(pieces.size - 1)) {
        if (pieces[arrayPos] != null) {
            val peca = getPiece(pieces, arrayPos)
            var pecat = piece.toUpperCase()
            val cor = peca[1].toString()
            pieces[arrayPos] = Pair(pecat, cor)
            }
        }
    }


// Extrai do array pieces na posição arrayPos uma string composta pelo conteudo do pair
fun getPiece(pieces: Array<Pair<String, String>?>, arrayPos: Int): String {
    val par = pieces[arrayPos]
    var strPeca = "  "
    if (par != null) {
        strPeca = par.first + par.second
    }
    return strPeca
}
// Constroi a string para a apresentação do tabuleiro
fun buildBoard(
    numColumns: Int,
    numLines: Int,
    showLegend: Boolean = false,
    showPieces: Boolean = false,
    pieces: Array<Pair<String, String>?>)
    : String {

    val esc: String = Character.toString(27.toChar())
    val startGrey = "$esc[30;47m"
    val startBlue = "$esc[30;44m"
    val startWhite = "$esc[30;30m"
    val endColor = "$esc[0m"
    val topLegend = "ABCDEFGHIJ"
    var strPiece = ""
    var linha = ""
    var celula = ""
    var countCol = 0
    var countLin = 1
    if (showLegend) {
        linha+= startBlue + "   " + endColor
        countCol = 1
        while (countCol <= numColumns) {
            linha += startBlue + " " + topLegend[countCol - 1] + " " + endColor
            countCol++
        }
        linha += startBlue + "   " + endColor
        linha += "\n"
    }
    var arrayPos = -1
    while (countLin <= numLines) {
        if (showLegend == true) {
            linha += startBlue + " $countLin " + endColor
        } else {
            linha += ""
        }
        countCol = 1
        while (countCol <= numColumns) {
            arrayPos++
            if (showPieces == true) {
                strPiece = getPiece(pieces, arrayPos)
                celula = convertStringToUnicode(strPiece[0].toString(), strPiece[1].toString())
            } else {
                celula = " "
            }
            if ((countCol + countLin) % 2 == 0) {
                linha+= startWhite + " " + celula + " " + endColor
            } else {
                linha+= startGrey + " " + celula + " " + endColor
            }
            countCol++
        }
        if (showLegend) {
            linha+= startBlue + "   " + endColor
        }
        linha += "\n"
        countLin++
    }
    if (showLegend) {
        linha += startBlue + "   " + endColor
        countCol = 1
        while (countCol <= numColumns) {
            linha += startBlue + "   " + endColor
            countCol++
        }
        linha+= startBlue + "   " + endColor
        linha += "\n"
    }
    return linha
}


// Constroi o array unidimensional e coloca as peças no mesmo segundo pre-definicoes
fun createInitialBoard(
    numColumns: Int,
    numLines: Int)
    : Array<Pair<String, String>?> {

    var opcBlack = "        "
    var opcWhite = "        "
    var boardValid = false
    if (numColumns == 8 && numLines == 8) {
        opcBlack = "THBQKBHT"
        opcWhite = "THBKQBHT"
        boardValid = true
    }
    if (numColumns == 7 && numLines == 7) {
        opcBlack = "THBKBHT"
        opcWhite = "THBKBHT"
        boardValid = true
    }
    if (numColumns == 6 && numLines == 6) {
        opcBlack = "HBQKBT"
        opcWhite = "HBKQBT"
        boardValid = true
    }
    if (numColumns == 6 && numLines == 7) {
        opcBlack = "TBQKBH"
        opcWhite = "TBKQBH"
        boardValid = true
    }
    if (numColumns == 4 && numLines == 4) {
        opcBlack = "  TB"
        opcWhite = "TQ  "
        boardValid = true
    }
    if (boardValid) {
        val pieces: Array<Pair<String, String>?> = arrayOfNulls(numColumns * numLines)
        var posArray = -1
        for (lin in 1..numLines) {
            for (col in 1..numColumns) {
                posArray++
                if (lin == 1) {
                    if (opcBlack[col - 1] != ' ') {
                        pieces[posArray] = Pair(opcBlack[col - 1].toString(), "b")
                    }
                }
                if (lin == 2 && numLines > 4) {
                    pieces[posArray] = Pair("P", "b")
                }
                if (lin == numLines - 1 && numLines > 4) {
                    pieces[posArray] = Pair("P", "w")
                }
                if (lin == numLines) {
                    if (opcWhite[col - 1] != ' ') {
                        pieces[posArray] = Pair(opcWhite[col - 1].toString(), "w")
                    }
                }
            }
        }
        return pieces
    }
    return emptyArray()
}


// Obter o caracter para a peça de xadrex
fun convertStringToUnicode(
    piece: String,
    color: String)
    : String {

    when (piece){
        "K" -> if (color == "w"){
            return "\u2654"
        } else if (color == "b"){
            return "\u265A"
        }
        "Q" -> if (color == "w"){
            return "\u2655"
        } else if (color == "b") {
            return "\u265B"
        }
        "T" -> if (color == "w"){
            return "\u2656"
        } else if (color == "b") {
            return "\u265C"
        }
        "B" -> if (color == "w"){
            return "\u2657"
        } else if (color == "b") {
            return "\u265D"
        }
        "H" -> if (color == "w"){
            return "\u2658"
        } else if (color == "b") {
            return "\u265E"
        }
        "P" -> if (color == "w"){
            return "\u2659"
        } else if (color == "b") {
            return "\u265F"
        }
    }
    return " "
}


//Verificar se o PAIR(linha,coluna) é valido
fun isCoordinateInsideChess(
    coord: Pair<Int,Int>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    if (coord.first <= numLines && coord.first > 0 && coord.second <= numColumns && coord.second > 0) {
        return true
    }
    return false
}


// Obter coordenadas (Pair linha,coluna) a partir de uma string composta por
// um numero e uma letra. Pudemos se necessário testar para qualquer ordem de introdução da mesma.
fun getCoordinates(
    readText: String?)
    : Pair<Int, Int>? {

    var coordLinha = 0
    var coordColuna = 0
    var swapSeqCoord = 1 // Testar sequencia: 1 = Numero+Letra ou Letra+Numero, 0 = apenas Numero+Letra
    if (readText != null && readText.length == 2) {
        var readInput1 = readText[0].toString().toUpperCase()
        var readInput2 = readText[1].toString().toUpperCase()
        while (swapSeqCoord >= 0) {
            when (readInput1) {
                "1" -> coordLinha = 1
                "2" -> coordLinha = 2
                "3" -> coordLinha = 3
                "4" -> coordLinha = 4
                "5" -> coordLinha = 5
                "6" -> coordLinha = 6
                "7" -> coordLinha = 7
                "8" -> coordLinha = 8
            }
            when (readInput2) {
                "A" -> coordColuna = 1
                "B" -> coordColuna = 2
                "C" -> coordColuna = 3
                "D" -> coordColuna = 4
                "E" -> coordColuna = 5
                "F" -> coordColuna = 6
                "G" -> coordColuna = 7
                "H" -> coordColuna = 8
            }
            if (coordColuna != 0 && coordLinha != 0) {
                return Pair(coordLinha, coordColuna)
            } else { // Swap input order
                readInput1 = readText[1].toString().toUpperCase()
                readInput2 = readText[0].toString().toUpperCase()
                swapSeqCoord--
            }
        }
    }
    return null
}


// A partir de um pair (linha, coluna) e do numero de colunas determinar a posição no array das peças
fun getArrayPos(
    pos: Pair<Int,Int>,
    numColumns: Int)
    : Int {

    return ((pos.first - 1) * numColumns) + (pos.second - 1)
}

// verifica se o movimento é válido
fun isValidTargetPiece(
    currentSelectedPiece: Pair<String,String>?,
    currentCoord: Pair<Int, Int>,
    targetCoord: Pair<Int, Int>,
    pieces: Array<Pair<String, String>?>,
    numColumns: Int,
    numLines: Int)
        : Boolean {

    var retvalue = false
    when (currentSelectedPiece?.first) {
        "P" -> retvalue = isKnightValid(currentCoord, targetCoord, pieces, numColumns, numLines)
        "H" -> retvalue = isHorseValid(currentCoord, targetCoord, pieces, numColumns, numLines)
        "T" -> retvalue = isTowerValid(currentCoord, targetCoord, pieces, numColumns, numLines)
        "B" -> retvalue = isBishopValid(currentCoord, targetCoord, pieces, numColumns, numLines)
        "K" -> retvalue = isKingValid(currentCoord, targetCoord, pieces, numColumns, numLines)
        "Q" -> retvalue = isQueenValid(currentCoord, targetCoord, pieces, numColumns, numLines)
    }
    return retvalue
}

// Movimento do Bispo (diagonal)
fun isBishopValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movLinha == movColuna) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
        return false
}

// Movimento da Torre (só horizonal ou só vertical)
fun isTowerValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movColuna == 0 && movLinha > 0 || movLinha == 0 && movColuna > 0) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
    return false
}

// Movimento da Rainha (igual ao movimento da Torre ou do Bispo)
fun isQueenValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movColuna == 0 && movLinha > 0 || movLinha == 0 && movColuna > 0 || movLinha == movColuna) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
    return false
}

// Movimento do Rei (movimentos menores ou iguais que 1 em todas as coordenadas)
fun isKingValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movColuna <= 1 && movLinha <=1 ) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
    return false
}

// Movimento do Peão (movimentos verticais iguais que 1 (frente e trás permitidos)
fun isKnightValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movColuna == 0 && movLinha ==1 ) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
    return false
}

// Movimento do Cavalo (movimento em "L")
fun isHorseValid(
    currentCoord: Pair<Int,Int>,
    targetCoord: Pair<Int,Int>,
    pieces: Array<Pair<String,String>?>,
    numColumns: Int,
    numLines: Int)
    :Boolean {

    val arrayCurrentPos = getArrayPos(currentCoord , numColumns)
    val arrayTargetPos = getArrayPos(targetCoord , numColumns)
    val movLinha = (currentCoord.first - targetCoord.first).absoluteValue
    val movColuna = (currentCoord.second - targetCoord.second).absoluteValue
    if ( movColuna == 1 && movLinha == 2 || movColuna == 2 && movLinha == 1 ) {
        if (pieces[arrayCurrentPos]?.second != pieces[arrayTargetPos]?.second) {
            return true
        }
    }
    return false
}

// Verifica a partir de uma string contendo a cor da peça (w/b) e do turno se os mesmos estão corretos
fun checkRightPieceSelected(
    pieceColor: String,
    turn: Int)
    : Boolean {

    if (turn == 1 && pieceColor == "b" || turn == 0 && pieceColor == "w") {
        return true
    }
    return false
}
