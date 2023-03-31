package ec.app.cryptanalysis_tool;


// TODO Make a hell of a lot more robust. Currently only works specifically for my purposes.
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
    // p is plain text and should be a list of binary values
    /* 
     * Formatting should be p.shape = (w, r) 
     * w = width of s-box (for heys, 4 bits)
     * r = how many s-boxes the substitution matrix is. length of key should be (rounds + 1)* w bits long
     */
    public int[][] runSPN(int[][] p){
        int[][] u = p;     // intermediate cipher text and starts as plaintext
        int[][] c;     // final cipher text

        // N-1 whole rounds
        for ( int i=0; i < this.rounds; i++){
            u = this.subkeyMix(p,0);
            u = this.substitue(u);
            u = this.permute(u);
        }

        // final round
        u = this.subkeyMix(u,0);
        u = this.substitue(u);
        c = this.subkeyMix(u,0);

        // output cipher text
        return c;
    }


    // helper methods

    // methods to convert to and from binary
    public int binaryToInt(int[] bs){
        int i = 0;
        int len = (bs.length) - 1;

        for (int c = 0; c < bs.length; c++) {
            if (bs[c] == 1) {
                i+=Math.pow(2,len); 
            }
            len--;
        }
        return i;
    }

    // TODO make more robust for other SPNs
    public int[] intToBinary(int i){
        int[] bs = new int [4];
        for (int m = 3; m < -1; m--){
            bs[m] = i%2;
            i = i - ((i/2)+(i%2));
        }
        return bs;
    }

    // XOR values with subkey, r is round number
    public int[][] subkeyMix(int[][] t, int r){

        return t;
    }

    // substitutes a list of intermediate text inputs. Should be 16 in a [4][4] format
    public int[][] substitue(int[][] t){
        int[][] u = t;
        int num;

        // TRY / CATCH block for if there is an issue with mismatched substitution lengths and inputs.
        try{
            // iterates through each 'chunk' of 4 bits
            for(int i = 0; i < t.length; i++){
                num = this.substitutions[1][binaryToInt(t[i])];
                u[i] = intToBinary(num);
            }
        } catch(Exception e){
            // hopefully catches any issues with mismatched s-box lengths and inputs
            System.out.println(e);
        }
        return u;
    }

    // permutes a list text inputs 
    public int[][] permute(int[][] t){        

        return t;
    }
}
