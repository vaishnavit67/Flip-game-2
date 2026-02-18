package pck;
import java.util.*;

public class Flip_7 {

    static int N;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

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

        print(board);

        while (!isAllWhite(board)) {

            // ================= USER MOVE =================
            System.out.print("Your move (row col): ");
            int r = sc.nextInt();
            int c = sc.nextInt();

            flip(board, r, c);
            System.out.println("You flipped (" + r + "," + c + ")");
            print(board);

            if (isAllWhite(board))
                break;

            // ================= SYSTEM MOVE =================
            List<int[]> plan = buildStructuredPlan(board);

            if (plan == null || plan.isEmpty()) {
                System.out.println("System: No move possible.");
                break;
            }

            int[] move = plan.get(0);

            flip(board, move[0], move[1]);
            System.out.println("System flipped (" + move[0] + "," + move[1] + ")");
            print(board);
        }

        if (isAllWhite(board))
            System.out.println("🎉 Solved together!");
    }

    // =========================================================
    // BUILD STRUCTURED PLAN FOLLOWING:
    // N×N → halves → squares → halves → full
    // =========================================================
    static List<int[]> buildStructuredPlan(int[][] board) {

        int[][] copy = copy(board);
        List<int[]> moves = new ArrayList<>();

        int half = N / 2;

        // ----- Solve 4 squares -----
        collectMoves(copy, 0, half - 1, 0, half - 1, moves);
        collectMoves(copy, 0, half - 1, half, N - 1, moves);
        collectMoves(copy, half, N - 1, 0, half - 1, moves);
        collectMoves(copy, half, N - 1, half, N - 1, moves);

        // ----- Solve 2 halves -----
        collectMoves(copy, 0, half - 1, 0, N - 1, moves);
        collectMoves(copy, half, N - 1, 0, N - 1, moves);

        // ----- Solve full board -----
        collectMoves(copy, 0, N - 1, 0, N - 1, moves);

        return moves;
    }

    // =========================================================
    // FIRST ROW ENUMERATION + CHASE DOWN FOR A REGION
    // =========================================================
    static void collectMoves(int[][] board,
                             int r1, int r2,
                             int c1, int c2,
                             List<int[]> globalMoves) {

        int width = c2 - c1 + 1;
        int[][] original = copy(board);

        for (int mask = 0; mask < (1 << width); mask++) {

            int[][] temp = copy(original);
            List<int[]> localMoves = new ArrayList<>();

            // First row guess
            for (int col = 0; col < width; col++) {
                if ((mask & (1 << col)) != 0) {
                    flip(temp, r1, c1 + col);
                    localMoves.add(new int[]{r1, c1 + col});
                }
            }

            // Chase down
            for (int row = r1 + 1; row <= r2; row++) {
                for (int col = c1; col <= c2; col++) {
                    if (temp[row - 1][col] == 0) {
                        flip(temp, row, col);
                        localMoves.add(new int[]{row, col});
                    }
                }
            }

            if (isAllWhite(temp, r1, r2, c1, c2)) {

                // Commit moves to working board copy
                for (int[] move : localMoves)
                    flip(board, move[0], move[1]);

                globalMoves.addAll(localMoves);
                return;
            }
        }
    }

    // =========================================================
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

    static boolean isAllWhite(int[][] board,
                              int r1, int r2,
                              int c1, int c2) {

        for (int i = r1; i <= r2; i++)
            for (int j = c1; j <= c2; j++)
                if (board[i][j] == 0)
                    return false;

        return true;
    }

    static boolean isAllWhite(int[][] board) {
        for (int[] row : board)
            for (int val : row)
                if (val == 0)
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
