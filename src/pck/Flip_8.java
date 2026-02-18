package pck;
import java.util.*;

public class Flip_8 {

    static int N;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter board size (even number): ");
        N = sc.nextInt();

        if (N % 2 != 0) {
            System.out.println("Board size must be even.");
            return;
        }

        int[][] board = new int[N][N];

        System.out.println("Enter board (1=White, 0=Black):");

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                board[i][j] = sc.nextInt();

        System.out.println("\nInitial Board:");
        print(board);

        playGame(board);
    }

    // =====================================================
    // TURN BASED GAME LOOP
    // =====================================================
    static void playGame(int[][] board) {

        Scanner sc = new Scanner(System.in);

        while (!isAllWhite(board)) {

            // ========== USER MOVE ==========
            System.out.println("Your move (row col): ");
            int r = sc.nextInt();
            int c = sc.nextInt();

            if (r < 0 || r >= N || c < 0 || c >= N) {
                System.out.println("Invalid move.");
                continue;
            }

            flip(board, r, c);
            System.out.println("After your move:");
            print(board);

            if (isAllWhite(board))
                break;

            // ========== COMPUTER MOVE ==========
            List<int[]> solutionMoves = solveBoardAndReturnMoves(board);

            if (solutionMoves.isEmpty()) {
                System.out.println("No solution possible from here.");
                break;
            }

            int[] move = solutionMoves.get(0);

            System.out.println("Computer flips (" + move[0] + "," + move[1] + ")");
            flip(board, move[0], move[1]);
            print(board);
        }

        System.out.println("Game Over!");
    }

    // =====================================================
    // ORIGINAL STRUCTURED SOLVER (NOW RETURNS MOVES)
    // =====================================================
    static List<int[]> solveBoardAndReturnMoves(int[][] board) {

        int[][] copyBoard = copy(board);
        List<int[]> allMoves = new ArrayList<>();

        int half = N / 2;

        solveHalf(copyBoard, 0, half - 1, 0, N - 1, allMoves);
        solveHalf(copyBoard, half, N - 1, 0, N - 1, allMoves);
        solveSubBoard(copyBoard, 0, N - 1, 0, N - 1, allMoves);

        if (isAllWhite(copyBoard))
            return allMoves;

        return new ArrayList<>();
    }

    // =====================================================
    static void solveHalf(int[][] board,
                          int r1, int r2,
                          int c1, int c2,
                          List<int[]> moves) {

        int midCol = (c1 + c2) / 2;

        solveSubBoard(board, r1, r2, c1, midCol, moves);
        solveSubBoard(board, r1, r2, midCol + 1, c2, moves);
        solveSubBoard(board, r1, r2, c1, c2, moves);
    }

    // =====================================================
    // FIRST ROW ENUMERATION + CHASE DOWN (UNCHANGED LOGIC)
    // =====================================================
    static void solveSubBoard(int[][] board,
                              int r1, int r2,
                              int c1, int c2,
                              List<int[]> globalMoves) {

        int width = c2 - c1 + 1;
        int[][] original = copy(board);

        for (int mask = 0; mask < (1 << width); mask++) {

            int[][] temp = copy(original);
            List<int[]> tempMoves = new ArrayList<>();

            // First row guess
            for (int col = 0; col < width; col++) {
                if ((mask & (1 << col)) != 0) {
                    flip(temp, r1, c1 + col);
                    tempMoves.add(new int[]{r1, c1 + col});
                }
            }

            // Chase down
            for (int row = r1 + 1; row <= r2; row++) {
                for (int col = c1; col <= c2; col++) {
                    if (temp[row - 1][col] == 0) {
                        flip(temp, row, col);
                        tempMoves.add(new int[]{row, col});
                    }
                }
            }

            if (isAllWhiteRegion(temp, r1, r2, c1, c2)) {

                // Apply moves to real board
                for (int[] move : tempMoves) {
                    flip(board, move[0], move[1]);
                    globalMoves.add(move);
                }
                return;
            }
        }
    }

    // =====================================================
    static void flip(int[][] board, int r, int c) {

        int[] dr = {0, 1, -1, 0, 0};
        int[] dc = {0, 0, 0, 1, -1};

        for (int i = 0; i < 5; i++) {

            int nr = r + dr[i];
            int nc = c + dc[i];

            if (nr >= 0 && nr < N && nc >= 0 && nc < N)
                board[nr][nc] ^= 1;
        }
    }

    // =====================================================
    static boolean isAllWhite(int[][] board) {

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (board[i][j] == 0)
                    return false;

        return true;
    }

    static boolean isAllWhiteRegion(int[][] board,
                                    int r1, int r2,
                                    int c1, int c2) {

        for (int i = r1; i <= r2; i++)
            for (int j = c1; j <= c2; j++)
                if (board[i][j] == 0)
                    return false;

        return true;
    }

    // =====================================================
    static int[][] copy(int[][] board) {

        int[][] newBoard = new int[N][N];

        for (int i = 0; i < N; i++)
            newBoard[i] = board[i].clone();

        return newBoard;
    }

    // =====================================================
    static void print(int[][] board) {

        for (int[] row : board) {
            for (int val : row)
                System.out.print(val + " ");
            System.out.println();
        }
        System.out.println();
    }
}
