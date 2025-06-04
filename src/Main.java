import java.util.*;

public class Main {

    // Function to calculate the value of y
    static int calculateY(int x, List<Integer> poly) {
        int y = 0;
        int temp = 1;
        for (int coeff : poly) {
            y += coeff * temp;
            temp *= x;
        }
        return y;
    }

    // Function to perform the secret sharing algorithm
    static void secretSharing(int S, List<int[]> points, int N, int K) {
        Random rand = new Random();
        List<Integer> poly = new ArrayList<>(Collections.nCopies(K, 0));
        poly.set(0, S);

        for (int j = 1; j < K; j++) {
            int p = 0;
            while (p == 0) {
                p = rand.nextInt(997); // Keep value < 1000 and not zero
            }
            poly.set(j, p);
        }

        for (int j = 1; j <= N; j++) {
            int x = j;
            int y = calculateY(x, poly);
            points.add(new int[]{x, y});
        }
    }

    // Helper class for handling fractions
    static class Fraction {
        int num, den;

        Fraction(int num, int den) {
            this.num = num;
            this.den = den;
            reduce();
        }

        void reduce() {
            int gcd = gcd(num, den);
            num /= gcd;
            den /= gcd;
        }

        static int gcd(int a, int b) {
            return b == 0 ? a : gcd(b, a % b);
        }

        Fraction multiply(Fraction other) {
            return new Fraction(this.num * other.num, this.den * other.den);
        }

        Fraction add(Fraction other) {
            int numerator = this.num * other.den + this.den * other.num;
            int denominator = this.den * other.den;
            return new Fraction(numerator, denominator);
        }
    }

    // Generate the secret from given points using Lagrange Interpolation
    static int generateSecret(int[] x, int[] y, int M) {
        Fraction ans = new Fraction(0, 1);

        for (int i = 0; i < M; i++) {
            Fraction l = new Fraction(y[i], 1);
            for (int j = 0; j < M; j++) {
                if (i != j) {
                    l = l.multiply(new Fraction(-x[j], x[i] - x[j]));
                }
            }
            ans = ans.add(l);
        }

        return ans.num;
    }

    // Combined operation: Share and reconstruct the secret
    static void operation(int S, int N, int K) {
        List<int[]> points = new ArrayList<>();
        secretSharing(S, points, N, K);

        System.out.println("Secret is divided into " + N + " parts:");
        for (int[] point : points) {
            System.out.println(point[0] + " " + point[1]);
        }

        System.out.println("We can generate the secret from any " + K + " parts.");

        int M = K; // Should be >= K

        int[] x = new int[M];
        int[] y = new int[M];

        for (int i = 0; i < M; i++) {
            x[i] = points.get(i)[0];
            y[i] = points.get(i)[1];
        }

        System.out.println("Our Secret Code is: " + generateSecret(x, y, M));
    }

    public static void main(String[] args) {
        int S = 65;
        int N = 4;
        int K = 2;

        operation(S, N, K);
    }
}
