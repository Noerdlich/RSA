import java.util.ArrayList;
import java.util.Random;

public class RSA {
    //Attribute
    private static long m;
    private static String mas;
    private static long e;
    private static long d;
    private static long p;
    private static long q;
    private static long phiN;
    private static long n;

    public static void main(String[] args) {
        //RSA based on given example
        init(17, 37, 17);
        m = 65;
        long c0 = encrypt(m);
        System.out.println(asString());
        System.out.println("c: " + c0);
        System.out.println("m': " + decrypt(c0));

        //RSA with random primes between 2 and 10000 and number as message
        long[] primes = getRandomPrimes(10000);
        init(primes[0], primes[1]);
        m = 65;
        long c1 = encrypt(m);
        System.out.println(asString());
        System.out.println("c: " + c1);
        System.out.println("m': " + decrypt(c1));

        //RSA with random primes between 2 and 10000 and string as message
        primes = getRandomPrimes(1000);
        init(primes[0], primes[1]);
        mas = "Hallo, dies ist ein Test";
        ArrayList<Long> c2 = encrypt(mas);
        System.out.println(asString());
        System.out.println("c: " + c2);
        System.out.println("m': " + decrypt(c2));
    }

    /**
     * init RSA with just two primes
     * @param prim1 long
     * @param prim2 long
     */
    private static void init(long prim1, long prim2){
        p = prim1;
        q = prim2;
        n = p * q;
        phiN = (p-1)*(q-1);
        e = findE();    //find the first suitable e
        d = multInverse(e, phiN);
        m = 0;      //reset message
        mas = "";   //reset message
    }

    /**
     * init RSA with given primes and given e
     * @param prim1 long
     * @param prim2 long
     * @param newE long
     */
    private static void init(long prim1, long prim2, long newE){
        init(prim1, prim2);
        e = newE;
        d = multInverse(e, phiN);
    }

    /**
     * encrypt number message using already init e and n according to the rsa algorithm.
     * makes Use of the modularMultiplication method as c = m^e mod n
     * @return long
     */
    private static long encrypt(long message){
        return mM(message, e, n);
    }

    /**
     * encrypt number message using custom e and n according to the rsa algorithm.
     * makes Use of the modularMultiplication method as c = m^e mod n
     * @param message long
     * @param e long
     * @param n long
     * @return long
     */
    private static long encrypt(long message, long e, long n){
        return mM(message, e, n);
    }
    
    /**
     * encrypt String message using already init e and n according to the rsa algorithm.
     * @param message String
     * @return ArrayList<Long>
     */
    private static ArrayList<Long> encrypt(String message){
        return encrypt(message, e, n);
    }

    /**
     * encrypt String message using custom e and n according to the rsa algorithm.
     * Each character of the String is converted to it's ascii representation and
     * then encrypted using modularMultiplication as c = m^e mod n
     * @param message String
     * @param e long
     * @param n long
     * @return ArrayList<Long>
     */
    private static ArrayList<Long> encrypt(String message, long e, long n){
        ArrayList<Long> result = new ArrayList<>();
        ArrayList<Long> ascii = encode(message);
        for (long val : ascii) {
            result.add(mM(val, e, n));
        }
        return result;
    }

    /**
     * decrypt number ciphertext using already init d and n according to the rsa algorithm.
     * makes use of modularMultiplication  as m' = c^d mod n
     * @param ciphertext long
     * @return long
     */
    private static long decrypt(long ciphertext){
        return mM(ciphertext, d, n);
    }

    /**
     * decrypt number ciphertext using already init d and n according to the rsa algorithm.
     * makes use of modularMultiplication as m' = c^d mod n
     * @param ciphertext long
     * @param d long
     * @param n long
     * @return long
     */
    private static long decrypt(long ciphertext, long d, long n){
        return mM(ciphertext, d, n);
    }

    /**
     * decrypt ArrayList ciphertext that comes from String message using already init d and n according to the rsa algorithm.
     * makes use of the equivalent decrypt() method.
     * @param ciphertext ArrayList<Long>
     * @return decoded String
     */
    private static String decrypt(ArrayList<Long> ciphertext){
        return decrypt(ciphertext, d, n);
    }

    /**
     * decrypt ArrayList ciphertext that comes from String message using custom d and n according to the rsa algorithm.
     * makes use of the equivalent decrypt() method.
     * The given ciphertext ArrayList is being decrypted item by item making use of modularMultiplication.
     * After every item of the ciphertext has been decrypted, the resulting ArrayList is being decoded back
     * to a String according to ascii values.
     * @param ciphertext ArrayList<Long>
     * @param d long
     * @param n long
     * @return decoded String
     */
    private static String decrypt(ArrayList<Long> ciphertext, long d, long n){
        ArrayList<Long> res = new ArrayList<>();
        for (long val : ciphertext) {
            res.add(mM(val, d, n)); //decrypt
        }
        return decode(res); //decode
    }

    /**
     * A given String is being encoded using ascii.
     * Each character is therefore being translated into it's base 10 representation and stored in an ArrayList
     * @param message String
     * @return ArrayList<Long>
     */
    private static ArrayList<Long> encode(String message){
        ArrayList<Long> ascii = new ArrayList<>();
        char[] mac = message.toCharArray();
        for (long add : mac) {
            ascii.add(add);
        }
        return ascii;
    }

    /**
     * A given ArrayList is being decoded using ascii.
     * Each value is therefore being translated into it's alphanumerical representation and stored in a String
     * @param message ArrayList<Long>
     * @return String
     */
    private static String decode(ArrayList<Long> message){
        StringBuilder res = new StringBuilder();
        char[] mac = new char[message.size()];
        for(int i = 0; i < message.size(); i++){
            mac[i] = (char) message.get(i).intValue();
            res.append(mac[i]);
        }
        return res.toString();
    }

    /**
     * this method uses the square and multiply algorithm to solve modular exponential terms.
     * The algorithm is recursive and works as follows:
     * if the exponent is even -> half it and square the result
     * if the exponent is odd -> subtract one to make it even and multiply the result by the base
     * @param basis long
     * @param exponent long
     * @param modulo long
     * @return long
     */
    private static long mM(long basis, long exponent, long modulo) {
        if(exponent == 1) return basis % modulo;
        else if(exponent % 2 == 0) return (mM(basis, (exponent / 2), modulo) * mM(basis, (exponent / 2), modulo)) % modulo;
        else return (basis * mM(basis, (exponent - 1), modulo)) % modulo;
    }

    /**
     * this method performs the enhanced Euclidean algorithm
     * @param a long
     * @param b long
     * @return long[]
     */
    private static long[] eEuclid(long a, long b) {
        long[] res = new long[3];
        if(b == 0) {  //termination condition
            res[0] = a; //the gcd
            res[1] = 1;
        } else {
            res = eEuclid(b, a % b);
            long k = res[1];
            long l = res[2];
            res[1] = l; //move l from index 2 to 1
            res[2] = k - l * (a / b); //calculate new l
        }
        return res;
    }

    /**
     * calculate the multiplicative inverse for 'a' in number space of mod
     * @param a long
     * @param mod long
     * @return long
     */
    private static long multInverse(long a, long mod) {
        long[] msf = eEuclid(mod, a);   //multiple sum formula calculated with enhanced Euclid
        if(msf[0] == 1){  //if gcd is 1
            return (msf[2] + mod) % mod;
        } else return 0;    //an error occurred
    }

    /**
     * finding a suitable e for the rsa-algorithm.
     * requirement: the greatest common divisor of e and phiN must be 1
     * @return long
     */
    private static long findE(){
        for(int i = 3; i < phiN; i+=2){
            if(gcd(i, phiN) == 1) return i;
        }
        return -1;  //an error occurred
    }

    /**
     * simple recursive algorithm to find the greatest common divisor of two numbers
     * @param a long
     * @param b long
     * @return long
     */
    private static long gcd(long a, long b){
        if(b == 0) return a;
        if(b > a) return gcd(b, a);
        return gcd(a % b,b);
    }

    /**
     * method returns two random primes between 2 and the maximum value provided in the parameter
     * @param max long
     * @return long[]
     */
    private static long[] getRandomPrimes(int max) {
        long[] res = new long[2];
        Random rand = new Random();
        long num = rand.nextInt(max) + 1;
        for(int i = 0; i < res.length; i++){
            while (!isPrime(num) || num == res[0]) {
                //generate new random number while it's not a prime or is a prime, but was already generated
                num = rand.nextInt(max) + 1;
            }
            res[i] = num;
            num = rand.nextInt(max) + 1;
        }
        return res;
    }

    /**
     * algorithm that checks whether a given number is a prime or not.
     * @param x long
     * @return boolean
     */
    private static boolean isPrime(long x) {
        if(x < 0 || x == 1 || x == 0) return false;
        if(x == 2) return true;
        for(int i = 2; i <= Math.sqrt(x); i ++) {
            if(x % i == 0) return false;
        }
        return true;
    }

    /**
     * returns a String containing all the calculated values needed to use the rsa algorithm
     * @return String
     */
    private static String asString() {
        return "\np: " + p +
                "\nq: " + q +
                "\nn: " + n +
                "\nphiN: " + phiN +
                "\ne: " + e +
                "\nd: " + d +
                (m == 0 ? "\nm: " + mas : "\nm: " + m); //use number or String message depending on which is used
    }
}