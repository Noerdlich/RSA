import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RSA {
    //Attriubute
    private static long m;
    private static long e;
    private static long d;
    private static long p;
    private static long q;
    private static long phiN;
    private static long n;

    public static void main(String[] args) {
        int[] primes = getRandomPrimes(1000);
        init(primes[0], primes[1], 65);
        long c = encrypt();
        System.out.println(asString());
        System.out.println("c: " + c);
        System.out.println("m': " + decrypt(c));

//        ArrayList<Long > c2 = encrypt("Dies ist ein Test!", 17, 629);
//        System.out.println(mM_long(68105101115L, 17, 629));
//        System.out.println(mM_long(553, 305, 629));
//        System.out.println(c2);
//        System.out.println(decrypt(553, 305, 629));
//        ArrayList<Long > test = new ArrayList<>();
//        test.add(315);
//        System.out.println(decode(test));
//        System.out.println(decrypt(c2, 305, 629));
//
//        System.out.println(decode(encode("Dies ist ein Test!")));
    }

    /**
     * init RSA with just two primes and a message
     * @param prim1
     * @param prim2
     * @param message
     */
    private static void init(long prim1, long prim2, long message){
        p = prim1;
        q = prim2;
        n = p * q;
        phiN = (p-1)*(q-1);
        e = findE();
        d = multInvers(e, phiN);
        m = message;
    }

    /**
     * init RSA with given primes and given e
     * @param prim1
     * @param prim2
     * @param newE
     * @param message
     */
    private static void init(long prim1, long prim2, long newE, long message){
        p = prim1;
        q = prim2;
        n = p * q;
        phiN = (p-1)*(q-1);
        e = newE;
        d = multInvers(e, phiN);
        m = message;
    }

    /**
     * encrypt set message m using e and n according to the rsa algorithm
     * @return
     */
    private static long encrypt(){
        return mM(m, e, n);
    }

    /**
     * encrypt message using e and n, all given vai parameter
     * @param message
     * @param e
     * @param n
     * @return
     */
    private static long encrypt(long message, long e, long n){
        return mM(message, e, n);
    }
    private static ArrayList<Long > encrypt(String message, long e, long n){
        ArrayList<Long > result = new ArrayList<Long >();
        ArrayList<Long > ascii = encode(message);
        for(int i = 0; i < ascii.size(); i+=4) {
            long temp = 0;
            for (int j = 0; j < 4 && i+j < ascii.size(); j++) {
                temp *= 1000;
                temp += ascii.get(i+j);
            }
            result.add(mM_long(temp, e, n));
        }
        return result;
    }

    private static long decrypt(long cyphertext){
        return mM(cyphertext, d, n);
    }

    private static long decrypt(long cyphertext, long d, long n){
        return mM(cyphertext, d, n);
    }

    private static String decrypt(ArrayList<Long > cyphertext, long d, long n){
        ArrayList<Long> res = new ArrayList<Long >();
        for(int i = 0; i < cyphertext.size(); i++){
            long temp = mM(cyphertext.get(i), d, n);
            for(long j = 0; i < 4; j++){
                res.add(temp%1000);
                temp/=1000;
            }
        }
        return decode(res);
    }

    private static ArrayList<Long > encode(String message){
        ArrayList<Long > ascii = new ArrayList<Long >();
        char[] mac = message.toCharArray();
        for (long add : mac) {
            ascii.add(add);
        }
        return ascii;
    }

    private static String decode(ArrayList<Long > message){
        char[] mac = new char[message.size()];
        for(int i = 0; i < message.size(); i++){
            mac[i] = (char) message.get(i).intValue();
        }
        return Arrays.toString(mac);
    }

    private static long mM(long basis, long exponent, long modulo) {
        long ergebnis = 0;

        if(exponent == 1) {
            ergebnis = basis % modulo;
        } else if(exponent % 2 == 0) {
            ergebnis =  mM(basis, (exponent / 2), modulo) * mM(basis, (exponent / 2), modulo) % modulo;
        } else if(exponent % 2 != 0) {
            ergebnis = basis * mM(basis, (exponent - 1), modulo) % modulo;
        }
        return ergebnis;
    }

    private static long mM_long(long basis, long exponent, long modulo) {
        long ergebnis = 0;

        if(exponent == 1) {
            ergebnis = (int) (basis % modulo);
        } else if(exponent % 2 == 0) {
            ergebnis = (mM_long(basis, (exponent / 2), modulo) * mM_long(basis, (exponent / 2), modulo)) % modulo;
        } else if(exponent % 2 != 0) {
            ergebnis = (int) ((basis * mM_long(basis, (exponent - 1), modulo)) % modulo);
        }
        return ergebnis;
    }

    private static long[] eEuklid(long a, long b) {
        long[] ausgabe = new long[3];
        if(b == 0){  //Abbruchbedingung
            ausgabe[0] = a; //der ggT
            ausgabe[1] = 1;
            ausgabe[2] = 0;
        }else{
            ausgabe = eEuklid(b, a % b);
            long k = ausgabe[1];
            long l = ausgabe[2];
            ausgabe[1] = l; //Verschieben von l von index 2 auf 1
            ausgabe[2] = k - l * (a / b); //berechnen des neuen l
        }
        return ausgabe;
    }

    private static long multInvers(long a, long mod) {
        long[] vfsf = eEuklid(mod, a);
        if(vfsf[0] == 1){  //falls der ggT 1 ist
            return (vfsf[2] + mod) % mod; //Ausgabe
        } else return 0;
    }

    private static int findE(){
        for(int i = 3; i < phiN; i+=2){
            if(ggT(i, phiN) == 1) return i;
        }
        return -1;
    }

    private static long ggT(long a, long b){
        if(b == 0) return a;
        if(b > a) return ggT(b, a);
        return ggT(a % b,b);
    }

    private static int[] getRandomPrimes(int max) {
        int[] res = new int[2];
        Random rand = new Random();
        int num = rand.nextInt(max) + 1;
        for(int i = 0; i < res.length; i++){
            while (!istPrimzahl(num) || num == res[0]) {
                num = rand.nextInt(max) + 1;
            }
            res[i] = num;
            num = rand.nextInt(max) + 1;
        }
        return res;
    }

    private static boolean istPrimzahl(long x) {
        if(x < 0 || x == 1 || x == 0) return false;
        if(x == 2) return true;
        for(int i = 2; i <= x - 1; i ++) {
            if(x % i == 0) return false;
        }
        return true;
    }

    private static String asString() {
        return "p: " + p +
                "\nq: " + q +
                "\nn: " + n +
                "\nphiN: " + phiN +
                "\ne: " + e +
                "\nd: " + d +
                "\nm: " + m;
    }
}
