package ec.app.cryptanalysis_tool;

public class SPN {
    int[][] substitutions;  // table for substitutions
    int[][] permutations;   // table for permutations
    int[] subkey;           // key for mixing
    int rounds;             // number of rounds an SPN has

    /*  A round consists of a subkey mixing stage, an S-Box stage
     *  and a permutation stage.
     *  The final round has no permutation stage and is followed by
     *  an extra subkey mixing stage
     * 
     *  For an SPN with N rounds, it will have
     *  N-1 permutations
     *  N sub-boxes
     *  N+1 subkey mixers
     */

    // constructor for SPN with Heys Cipher Network
    public SPN(int[] k){
        int[][] s = {{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15},
                    {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7}};
        substitutions = s;

        int[][] p = {{1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16},
                    {1,5,9,13,2,6,10,14,3,7,11,15,4,8,12,16}};
        permutations = p;

        subkey = k;
        rounds = 4;
    }

    // blank constructor for SPN
    public SPN(int[][] s, int[][] p, int[] k, int r){
        substitutions = s;
        permutations = p;
        subkey = k;
        rounds = 4;
    }

    // run SPN
    static int[] runSPN(int[] pt){
        int[] ciphertext = new int[16]; 

        return ciphertext;
    }

    // helper methods



}
