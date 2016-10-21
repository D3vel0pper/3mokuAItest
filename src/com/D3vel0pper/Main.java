package com.D3vel0pper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
	// write your code here
        BoardManager boardManager = new BoardManager();
        boardManager.run();
//        boardManager.test();
    }

    /**
     * BoardManager class
     * this is managing board that containing
     *  - State (the place's state 0-> nothing 1-> O put 2-> X put) :value
     *  - Tern (which player playing now) :value
     *  - Judge (judge is game over) :Method
     *  - CheckingState :Method
     *  - ChangeTern (Switch tern) :Method
     *  - Run (run game) :Method
     */
    public static class BoardManager {

        /**
         * Values
         */

        /**
         * tern: 1 -> O's tern -1 -> X's tern
         */
        private int tern;
        private int boardState[];

        private InputStreamReader isr;
        private BufferedReader br;

        private int historyPosition;

        /**
         * show amount of open(nothing put) place
         */
        private int openCount = 9;

        /**
         * position names
         *  ------------
         * | 0 | 1 | 2 |
         * | 3 | 4 | 5 |
         * | 6 | 7 | 8 |
         * -------------
         */
        private static final int TOP_LEFT = 0;
        private static final int TOP_CENTER = 1;
        private static final int TOP_RIGHT = 2;
        private static final int CENTER_LEFT = 3;
        private static final int CENTER = 4;
        private static final int CENTER_RIGHT = 5;
        private static final int BOTTOM_LEFT = 6;
        private static final int BOTTOM_CENTER = 7;
        private static final int BOTTOM_RIGHT = 8;

        /**
         * state name
         * O -> 1
         * X -> -1
         * nothing -> 0
         */
        private static final int NOTHING_PUT = 0;
        private static final int O_PUT = 1;
        private static final int X_PUT = -1;

        /**
         * Ai that computing
         */
        private Ai ai;

        /**
         * Getters
         */
        public int[] getBoardState(){
            return boardState;
        }

//        public int getOpenCount(){
//            return openCount;
//        }
//
//        public int getHistoryPosition(){
//            return historyPosition;
//        }
//
//        public int getTern(){
//            return tern;
//        }

        /**
         * Methods
         */

//        public void test(){
//            boardState[0] = 1;
//            boardState[1] = 0;
//            boardState[2] = 1;
//            boardState[3] = 0;
//            boardState[4] = 0;
//            boardState[5] = 0;
//            boardState[6] = 0;
//            boardState[7] = 0;
//            boardState[8] = 0;
//
//            printBoard();
//
//            Ai ai = new Ai(this);
//            ai.setAiTurn(1);
//            int isLeached = ai.isLeach();
//            if(isLeached == -1){
//                System.out.println("nothing leached");
//            } else {
//                System.out.println("if next is " + Integer.toString(isLeached) + ", game will over.");
//            }
//        }

        public BoardManager(){
            tern = 1;
            boardState = new int[9];
            for(int i = 0;i < 9;i++){
                boardState[i] = 0;
            }
            openCount = 9;
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
            ai = new Ai(this);
            historyPosition = -1;
        }

        public void run(){
            desideTurn();
            while(true) {
                //先攻ならここで走る
                if(ai.getAiTurn() == 1){
                    if(!runAi()){
                        System.out.println("AI shows unexpected work. Quit game....");
                        return;
                    }
                    if(isGameOver()){
                        printResult();
                        return;
                    } else if(checkIsBoardFull()){
                        printDraw();
                        return;
                    }
                }
                printBoard();
                inputPosition();
                if(isGameOver()){
                    printResult();
                    return;
                } else if(checkIsBoardFull()){
                    printDraw();
                    return;
                }
                //後攻ならここで走る
                if(ai.getAiTurn() == -1){
                    if(!runAi()){
                        System.out.println("AI shows unexpected work. Quit game....");
                        return;
                    }
                    if(isGameOver()){
                        printResult();
                        return;
                    } else if(checkIsBoardFull()){
                        printDraw();
                        return;
                    }
                }
            }
        }

        private void desideTurn(){
            System.out.println("先攻か後攻か選択してください。");
            System.out.println("先攻：１　後攻：２");
            try {
                String buf = br.readLine();
                int input = Integer.parseInt(buf);
                if(input == 1){
                    ai.setAiTurn(-1);
                } else if(input == 2){
                    ai.setAiTurn(1);
                } else {
                    System.out.println("正しい数字を入力してください。");
                    desideTurn();
                }
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        private void inputPosition(){
            System.out.println("置く場所を入力してください。");
            System.out.println("----番号の対応表----");
            showBoardNumber();
            System.out.println("--------------------");
            System.out.print(">> ");
            try {
                String buf = br.readLine();
                int position = Integer.parseInt(buf);
                if(checkState(boardState[position])){
                    putStone(position);
                } else {
                    System.out.println("その場所に置くことはできません。");
                    System.out.print("他の場所を入力してください。\n");
                    inputPosition();
                }
            } catch(IOException e){
                e.printStackTrace();
                System.out.println("IOException caused");
            }
        }

        private void printBoard(){
            System.out.println(" ------------");
            for(int i = 0;i < 9;i++){
                System.out.print("| ");
                System.out.print(convertState(boardState[i]));
                System.out.print(" | ");
                i++;
                System.out.print(convertState(boardState[i]));
                System.out.print(" | ");
                i++;
                System.out.print(convertState(boardState[i]));
                System.out.print(" |\n");
            }
            System.out.println(" ------------");
        }

        private void showBoardNumber(){
            System.out.println(" ------------");
            for(int i = 0;i < 9;i++){
                System.out.print("| ");
                System.out.print(i);
                System.out.print(" | ");
                i++;
                System.out.print(i);
                System.out.print(" | ");
                i++;
                System.out.print(i);
                System.out.print(" |\n");
            }
            System.out.println(" ------------");
        }

        public void putStone(int position){
            boardState[position] = tern;
            historyPosition = position;
            openCount--;
            switchTurn();
        }

        /**
         * @param target : the position on board
         * @return : string  O or X matched in state
         */
        private String convertState(int target){
            if(target == O_PUT){
                return "O";
            } else if(target == X_PUT){
                return "X";
            }
            return " ";
        }

        /**
         * return position is possible to put or not
         * @param target : the position to check
         * @return : true -> position has nothing
         *              false -> position has something
         */
        private boolean checkState(int target){
            if(target == NOTHING_PUT) {
                return true;
            }
            return false;
        }

        public void switchTurn(){
            if(tern == -1){
                tern = 1;
            } else if(tern == 1){
                tern = -1;
            } else {
                System.out.print("\n!!!--- Illegal tern. Please quit program... ---!!!\n");
            }
        }

        private void printResult(){
            System.out.println("～最終結果～");
            printBoard();
            System.out.print( "「" + convertState(boardState[historyPosition]) + "」の勝ち！！");
            try {
                br.close();
                isr.close();
            } catch (IOException e){
                e.printStackTrace();
                System.out.print("failed to close buffered reader or input stream reader");
            }
        }

        private void printDraw(){
            System.out.println("～最終結果～");
            printBoard();
            System.out.print( "ー引き分けー");
            try {
                br.close();
                isr.close();
            } catch (IOException e){
                e.printStackTrace();
                System.out.print("failed to close buffered reader or input stream reader");
            }
        }

//        private void judge(){
//            if (isGameOver()) {
//                //Print Result
//                printResult();
//                finishFlag = 1;
//            } else if(checkIsBoardFull()){
//                //Draw Game
//                printDraw();
//                finishFlag = 2;
//            }
//        }

        /**
         * Containing printing result.
         * @return :true -> game has already over , false -> not yet
         */
        private boolean isGameOver(){
            if(historyPosition < 0 || historyPosition > 8){
                return false;
            }
            if(checkCrossLine(historyPosition)){
                return true;
            } else if(cheackHorizontal(historyPosition)){
                return true;
            } else if(checkVertical(historyPosition)){
                return true;
            }
            return false;
        }

        private boolean cheackHorizontal(int position){
            if(position < 3){
                if(boardState[TOP_LEFT] == boardState[TOP_CENTER] && boardState[TOP_LEFT] == boardState[TOP_RIGHT]){
                    return true;
                }
            } else if(position < 6){
                if(boardState[CENTER_LEFT] == boardState[CENTER] && boardState[CENTER_LEFT] == boardState[CENTER_RIGHT]){
                    return true;
                }
            } else if(position < 9){
                if(boardState[BOTTOM_LEFT] == boardState[BOTTOM_CENTER] && boardState[BOTTOM_LEFT] == boardState[BOTTOM_RIGHT]){
                    return true;
                }
            }
            return false;
        }

        private boolean checkVertical(int position){
            //position % 3 == 0 ??
            if(position == 0 || position == 3 || position == 6){
                if(boardState[TOP_LEFT] == boardState[CENTER_LEFT] && boardState[TOP_LEFT] == boardState[BOTTOM_LEFT]){
                    return true;
                }
            }
            //position % 3 == 1 ??
            else if(position == 1 || position == 4 || position == 7){
                if(boardState[TOP_CENTER] == boardState[CENTER] && boardState[TOP_CENTER] == boardState[BOTTOM_CENTER]){
                    return true;
                }
            }
            //positiion % 3 == 2 ??
            else if(position == 2 || position == 5 || position == 8){
                if(boardState[TOP_RIGHT] == boardState[CENTER_RIGHT] && boardState[TOP_RIGHT] == boardState[BOTTOM_RIGHT]){
                    return true;
                }
            }
            return false;
        }

        private boolean checkCrossLine(int position){
            if(position == 4){
                if (boardState[TOP_LEFT] == boardState[CENTER] && boardState[TOP_LEFT] == boardState[BOTTOM_RIGHT]) {
                    return true;
                } else if (boardState[TOP_RIGHT] == boardState[CENTER] && boardState[TOP_RIGHT] == boardState[BOTTOM_LEFT]) {
                    return true;
                }
            }
            //position % 4 == 0 ??
            else if(position == 0 || position == 8) {
                if (boardState[TOP_LEFT] == boardState[CENTER] && boardState[TOP_LEFT] == boardState[BOTTOM_RIGHT]) {
                    return true;
                }
            }
            //position % 2 == 0 ??
            else if(position == 2 || position == 6) {
                if (boardState[TOP_RIGHT] == boardState[CENTER] && boardState[TOP_RIGHT] == boardState[BOTTOM_LEFT]) {
                    return true;
                }
            }
            return false;
        }

        /**
         * ---- for Debugging ----
         */
        private boolean checkIsBoardFull(){
            if(openCount == 0){
                return true;
            }
            return false;
        }

        private boolean runAi(){
            int nextPosition = ai.decideNext();
            if(nextPosition == -1){
                return false;
            }
            putStone(nextPosition);
            printBoard();
            return true;
        }

    }



    public static class Ai {

        public Ai(BoardManager boardManager){
            this.boardManager = boardManager;
            boardState = new int[9];
            for(int i = 0;i < 9;i++){
                boardState[i] = boardManager.getBoardState()[i];
            }
            //debug
//            evaValList = new ArrayList<Integer>();
//            bestPosListMy = new ArrayList<Integer>();
//            bestPosListOth = new ArrayList<Integer>();
        }

//        private List<Integer> evaValList;
//        private List<Integer> bestPosListMy;
//        private List<Integer> bestPosListOth;

        private BoardManager boardManager;
        private int myTurn;
        private int boardState[];

        /**
         * max thinking depth
         */
        private int maxDepth;
        private int tempMaxDepth = 2;

        /**
         * unDo putting
//         * @param historyPosition : history position that ai put before
         */
        private void unDo(int historyPosition){
            boardManager.getBoardState()[historyPosition] = 0;
            boardManager.switchTurn();
            boardManager.openCount++;
        }

        /**
         * set Which tern will Ai put stone
         * @param aiTern :tern that showing ai's tern
         */
        public void setAiTurn(int aiTern){
            myTurn = aiTern;
        }
        public int getAiTurn(){
            return myTurn;
        }

        /**
         * check board is leached or not
         * @return : -1 -> not yet , other -> position that will finish game if the place be put
         */
        //空いている場所を探して、そこからたどるほうが楽な気がする。
        public int isLeach(){
            int iterator = 0;
            for(;iterator < 9;iterator++) {
                if(boardManager.checkState(boardManager.getBoardState()[iterator])){
                    if(isRightLeach(iterator,myTurn)){
                        return iterator;
                    }
                    if(isUnderLeach(iterator,myTurn)){
                        return iterator;
                    }
                    if(isLeftLeach(iterator,myTurn)){
                        return iterator;
                    }
                    if(isUpLeach(iterator,myTurn)){
                        return iterator;
                    }
                    if(isCrossLeach(iterator,myTurn)){
                        return iterator;
                    }
                    if(isBetweenLeach(iterator,myTurn)){
                        return iterator;
                    }
                }
            }
            return -1;
        }

        private boolean isRightLeach(int startPosition, int checkTurn){
            if(startPosition == 0 || startPosition == 3 || startPosition == 6){
                if(boardManager.getBoardState()[startPosition + 1] == checkTurn &&
                (boardManager.getBoardState()[startPosition + 1] == boardManager.getBoardState()[startPosition + 2])){
                    return true;
                }
            }
            return false;
        }

        private boolean isUnderLeach(int startPosition, int checkTurn){
            if(startPosition == 0 || startPosition == 1 || startPosition == 2){
                if(boardManager.getBoardState()[startPosition + 3] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 3] == boardManager.getBoardState()[startPosition + 6])){
                    return true;
                }
            }
            return false;
        }

        private boolean isLeftLeach(int startPosition, int checkTurn){
            if(startPosition == 2 || startPosition == 5 || startPosition == 8){
                if(boardManager.getBoardState()[startPosition - 1] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 1] == boardManager.getBoardState()[startPosition - 2])){
                    return true;
                }
            }
            return false;
        }

        private boolean isUpLeach(int startPosition, int checkTurn){
            if(startPosition == 6 || startPosition == 7 || startPosition == 8){
                if(boardManager.getBoardState()[startPosition - 3] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 3] == boardManager.getBoardState()[startPosition - 6])){
                    return true;
                }
            }
            return false;
        }

        private boolean isCrossLeach(int startPosition, int checkTurn){
            if(startPosition == 4){
                if(boardManager.getBoardState()[startPosition - 4] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 4] == boardManager.getBoardState()[startPosition + 4])){
                    return true;
                } else if(boardManager.getBoardState()[startPosition - 2] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 2] == boardManager.getBoardState()[startPosition + 2])){
                    return true;
                }
            } else if(startPosition == 0){
                if(boardManager.getBoardState()[startPosition + 4] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 4] == boardManager.getBoardState()[startPosition + 8])){
                    return true;
                }
            } else if(startPosition == 2){
                if(boardManager.getBoardState()[startPosition + 2] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 2] == boardManager.getBoardState()[startPosition + 4])){
                    return true;
                }
            } else if(startPosition == 6){
                if(boardManager.getBoardState()[startPosition - 2] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 2] == boardManager.getBoardState()[startPosition - 4])){
                    return true;
                }
            } else if(startPosition == 8){
                if(boardManager.getBoardState()[startPosition - 4] == checkTurn &&
                        (boardManager.getBoardState()[startPosition - 4] == boardManager.getBoardState()[startPosition - 8])){
                    return true;
                }
            }
            return false;
        }

        private boolean isBetweenLeach(int startPosition, int checkTurn){
            if(startPosition == 4){
                if(boardManager.getBoardState()[startPosition + 1] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 1] == boardManager.getBoardState()[startPosition - 1])){
                    return true;
                } else if(boardManager.getBoardState()[startPosition + 3] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 3] == boardManager.getBoardState()[startPosition - 3])){
                    return true;
                }
            } else if(startPosition == 1 || startPosition == 7){
                if(boardManager.getBoardState()[startPosition + 1] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 1] == boardManager.getBoardState()[startPosition - 1])){
                    return true;
                }
            } else if(startPosition == 3 || startPosition == 5){
                if(boardManager.getBoardState()[startPosition + 3] == checkTurn &&
                        (boardManager.getBoardState()[startPosition + 3] == boardManager.getBoardState()[startPosition - 3])){
                    return true;
                }
            }
            return false;
        }

        /**
         * decide next
         * @return :nextPosition -> position ai will put , -1 -> unexpected work happen;
         */
        public int decideNext(){
            int nextPosition;
//            if(boardManager.getBoardState()[4] == 0){
//                nextPosition = 4;
//            }else {
//                nextPosition = minMax(myTurn * -1, 0);
//            }
//            nextPosition = minMax(myTurn * -1, 0);
            DoMinMax doMinMax = new DoMinMax(myTurn,tempMaxDepth);
            doMinMax.start();
            try {
                doMinMax.join();
                nextPosition = doMinMax.getReturnValue();
                if(nextPosition < 0 || nextPosition > 8){
                    return -1;
                }
                if(boardManager.checkState(boardManager.boardState[nextPosition])){
                    return nextPosition;
                }
            } catch (InterruptedException e){
                e.printStackTrace();
            }
//            if(boardManager.checkState(boardManager.boardState[nextPosition])){
//                    return nextPosition;
//                }
            return -1;
        }

        public int desideTempNext(){
            for(int i = 0;i < 9;i++) {
                if(boardManager.checkState(boardManager.getBoardState()[i])) {
                    return i;
                }
            }
            return 0;
        }

        /**
         * mini max algorithm
         * @param turn :
         * @param depth : remained open place (constant value)
         * @return : Child node returns evaluated value, Root node returns NextPosition
         */
        private int minMax(int turn,int depth){

            int evaluatedValue;
            int childValue = 0;
            int bestNextPosition = 0;
            int historyPosition = -1;

            depth += 1;

            //check is game over or board full
            if(boardManager.isGameOver()){
                if(turn == myTurn){
//                    evaValList.add(10 - depth);
                    return 10 - depth;
                } else {
//                    evaValList.add(depth - 10);
                    return depth - 10;
                }
            } else if(boardManager.checkIsBoardFull()){
//                evaValList.add(0);
                return 0;
            }

            //check is depth max
//            if(depth == tempMaxDepth){
//                return 0;
//            }

            if(turn == myTurn){
                evaluatedValue = Integer.MAX_VALUE;
            } else {
                evaluatedValue = Integer.MIN_VALUE;
            }

            for(int i = 0;i < 9;i++){
                if(boardManager.checkState(boardManager.getBoardState()[i])){

                    boardManager.putStone(i);
                    historyPosition = i;
//                    childValue = minMax(turn * -1,depth + 1);
                    childValue = minMax(turn * -1,depth);

                    //if next is opposite turn
                    if((turn * -1) == (myTurn * -1)){
                        if(childValue < evaluatedValue){
                            evaluatedValue = childValue;
                            bestNextPosition = historyPosition;
//                            bestPosListMy.add(historyPosition);
                        }
                    }
                    //if next is my turn
                    else if((turn * -1) == myTurn){
                        if(childValue > evaluatedValue){
                            evaluatedValue = childValue;
                            bestNextPosition = historyPosition;
//                            bestPosListOth.add(historyPosition);
                        }
                    }
                    //------------------
//                    boardManager.printBoard();
                    //------------------

                    unDo(historyPosition);

                    if((depth - 1) == 0){
                        System.out.println("pass #" + Integer.toString(i));
                    }

                }
            }

            if((depth - 1)  == 0){
                return bestNextPosition;
            }
            return childValue;
        }

        public class DoMinMax extends Thread {
            private int turn,depth;
            private int returnValue;
            public DoMinMax(int turn, int depth){
                this.turn = turn;
                this.depth = depth;
                this.returnValue = -1;
            }

            public void run(){
                returnValue = minMax(turn,depth);
            }

            private int minMax(int turn,int depth){

                int evaluatedValue;
                int childValue = 0;
                int bestNextPosition = 0;
                int historyPosition = -1;

                //check is game over or board full
                if(depth != maxDepth) {
                    if (boardManager.isGameOver()) {
                        //if before this, is myTurn
                        if (turn == myTurn) {
//                            evaValList.add(10 - depth);
                            return 10 - depth;
                        } else {
//                            evaValList.add(depth - 10);
                            return depth - 10;
                        }
                    }
                    if (boardManager.checkIsBoardFull()) {
//                        evaValList.add(0);
                        return 0;
                    }
                }

                depth -= 1;

                if(turn != myTurn){
                    evaluatedValue = Integer.MAX_VALUE;
                } else {
                    evaluatedValue = Integer.MIN_VALUE;
                }

                for(int i = 0;i < 9;i++){
                    if(boardManager.checkState(boardManager.getBoardState()[i])){

                        boardManager.putStone(i);
                        historyPosition = i;
                        DoMinMax doMinMax = new DoMinMax(turn * -1,depth);
                        doMinMax.start();
                        try {
                            doMinMax.join();
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                        childValue = doMinMax.getReturnValue();

                        //if next is opposite turn
                        if((turn * -1) == (myTurn * -1)){
                            if(childValue > evaluatedValue){
                                evaluatedValue = childValue;
                                bestNextPosition = historyPosition;
//                                bestPosListMy.add(historyPosition);
                            }
                        }
                        //if next is my turn
                        else if((turn * -1) == myTurn){
                            if(childValue < evaluatedValue){
                                evaluatedValue = childValue;
                                bestNextPosition = historyPosition;
//                                bestPosListOth.add(historyPosition);
                            }
                        }
                        //------------------
//                        boardManager.printBoard();
                        //------------------

                        unDo(historyPosition);

                    }
                }

                if((depth + 1) == tempMaxDepth){
                    return bestNextPosition;
                }
                return childValue;
            }

            public int getReturnValue(){
                return this.returnValue;
            }

        }


    }

}
