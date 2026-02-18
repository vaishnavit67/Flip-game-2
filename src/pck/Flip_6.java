package pck;
import java.util.*;

public class Flip_6 {

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

        solveBoard(board);

        System.out.println("Final Board:");
        print(board);
    }

    // ==========================================
    // MAIN STRUCTURED DIVISION
    // ==========================================
    static void solveBoard(int[][] board) {

        int half = N / 2;

        System.out.println("Dividing into two " + half + "x" + N + " halves\n");

        // Top half
        solveHalf(board, 0, half - 1, 0, N - 1);

        // Bottom half
        solveHalf(board, half, N - 1, 0, N - 1);

        // Finally solve entire board
        solveSubBoard(board, 0, N - 1, 0, N - 1);
    }

    // ==========================================
    // SOLVE HALF (N/2 × N)
    // ==========================================
    static void solveHalf(int[][] board,
                          int r1, int r2,
                          int c1, int c2) {

        int half = N / 2;

        System.out.println("Dividing into two "
                + half + "x" + half + " squares\n");

        int midCol = (c1 + c2) / 2;

        // Left square
        solveSubBoard(board, r1, r2, c1, midCol);

        // Right square
        solveSubBoard(board, r1, r2, midCol + 1, c2);

        // Then solve the half rectangle
        solveSubBoard(board, r1, r2, c1, c2);
    }

    // ==========================================
    // FIRST ROW ENUMERATION + CHASE DOWN
    // ==========================================
    static void solveSubBoard(int[][] board,
                              int r1, int r2,
                              int c1, int c2) {

        System.out.println("Solving region: (" + r1 + "," + c1 +
                ") to (" + r2 + "," + c2 + ")\n");

        int width = c2 - c1 + 1;
        int[][] original = copy(board);

        for (int mask = 0; mask < (1 << width); mask++) {

            int[][] temp = copy(original);
            List<int[]> moves = new ArrayList<>();

            // First row guess
            for (int col = 0; col < width; col++) {
                if ((mask & (1 << col)) != 0) {
                    flip(temp, r1, c1 + col);
                    moves.add(new int[]{r1, c1 + col});
                }
            }

            // Chase down
            for (int row = r1 + 1; row <= r2; row++) {
                for (int col = c1; col <= c2; col++) {
                    if (temp[row - 1][col] == 0) {
                        flip(temp, row, col);
                        moves.add(new int[]{row, col});
                    }
                }
            }

            if (isAllWhite(temp, r1, r2, c1, c2)) {

                System.out.println("Solution found.\n");
                applyMoves(board, moves);
                return;
            }
        }

        System.out.println("No solution for this region.\n");
    }

    // ==========================================
    // APPLY MOVES STEP BY STEP
    // ==========================================
    static void applyMoves(int[][] board, List<int[]> moves) {

        for (int[] move : moves) {

            int r = move[0];
            int c = move[1];

            System.out.println("Flip (" + r + "," + c + ")");
            flip(board, r, c);
            print(board);
        }
    }

    // ==========================================
    // FLIP
    // ==========================================
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

    // ==========================================
    static boolean isAllWhite(int[][] board,
                              int r1, int r2,
                              int c1, int c2) {

        for (int i = r1; i <= r2; i++)
            for (int j = c1; j <= c2; j++)
                if (board[i][j] == 0)
                    return false;

        return true;
    }

    static int[][] copy(int[][] board) {
        int[][] newBoard = new int[N][N];
        for (int i = 0; i < N; i++)
            newBoard[i] = board[i].clone();
        return newBoard;
    }

    static void print(int[][] board) {
        for (int[] row : board) {
            for (int val : row)
                System.out.print(val + " ");
            System.out.println();
        }
        System.out.println();
    }
}
