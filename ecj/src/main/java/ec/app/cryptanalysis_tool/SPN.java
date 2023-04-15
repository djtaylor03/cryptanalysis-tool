package ec.app.cryptanalysis_tool;

import java.util.Random;

// TODO Make a lot more robust. Currently only works specifically for Heys' SPN purposes.
// TODO better implementation of plaintext and keys
// TODO figure out why substitute is giving all 0's
// TODO add plain text as input with SPN constructor
public class SPN {

    // Main Class
    public static void main(String[] args) {
        // generate seeded random keys
        Random rand = new Random(0);

        // generate key ( 5 being number of subkeys needed, 16 being length of pt)
        int[] key = new int[80];

        for (int i = 0; i < key.length; i++) {
            key[i] = rand.nextInt() % 2;
        }

        int[][] pt = {
            {0,0,0,1},
            {0,0,1,0},
            {0,1,0,0},
            {1,0,0,0}};

        // run SPN with given input with seeded random keys
        SPN spn = new SPN(key);
        int[][][] spnOuts = spn.runSPN(pt);


        // create a triple of plaintext, intermediate cipher text and cipher text
        int[][][] PUCTriple = new int[3][4][4];
        
        PUCTriple[0] = pt;
        PUCTriple[1] = spnOuts[0];
        PUCTriple[2] = spnOuts[1];

        // save outputs to csv file
        //System.out.println(PUCTriple.length);

        /*
        for (int a = 0; a < PUCTriple.length; a++){

            System.out.println(PUCTriple[a].length);

            for (int b = 0; b < PUCTriple[a].length; b++){

                System.out.println();

                for (int c = 0; c < PUCTriple[b].length; c++){
                    System.out.print(c);
                    //System.out.print(PUCTriple[a][b][c] + ",");
                }
            }
        }
        */

    }

    int[] substitutions;  // table for substitutions
    int[] permutations;   // table for permutations
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
        int[] s = {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7};
        substitutions = s;

        int[] p = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
        permutations = p;

        subkey = k;
        rounds = 4;
    }

    // blank constructor for SPN
    public SPN(int[] s, int[] p, int[] k, int r){
        substitutions = s;
        permutations = p;
        subkey = k;
        rounds = r;
    }

    // run SPN
    // p is plain text and should be a list of binary values
    /* 
     * Formatting of p should be (w, r) 
     * w = width of s-box (for heys, 4 bits)
     * r = how many s-boxes the substitution matrix is. length of key should be (rounds + 1)* w bits long
     * 
     * NOTE : formatting with rounds is such to keep consistency with description in Hey's Tutorial
     */
    public int[][][] runSPN(int[][] p){
        int[][] u = p;              // intermediate cipher text and starts as plaintext
        int[][] c;                  // final cipher text
        int[][][] texts = {p,p};    // output texts

        System.out.println("Initial Input");
        for (int i = 0; i < p.length; i++){
            for (int j = 0; j < p[i].length; j++){
                System.out.print(p[i][j]);
            }
            System.out.println();
        }

        // N-1 whole rounds
        for ( int i = 1; i < this.rounds; i++ ){
            System.out.println("round" + i);
            
            System.out.println("key");
            u = this.subkeyMix(u,i);
            for (int q = 0; q < u.length; q++){
                for (int j = 0; j < u[q].length; j++){
                    System.out.print(u[q][j]);
                }
                System.out.println();
            }

            System.out.println("sub");
            u = this.substitue(u);
            for (int q = 0; q < u.length; q++){
                for (int j = 0; j < u[q].length; j++){
                    System.out.print(u[q][j]);
                }
                System.out.println();
            }

            System.out.println("permute");
            u = this.permute(u);
            for (int q = 0; q < u.length; q++){
                for (int j = 0; j < u[q].length; j++){
                    System.out.print(u[q][j]);
                }
                System.out.println();
            }
        }

        // final round
        System.out.println("final round");
        System.out.println("key");
        u = this.subkeyMix(u,this.rounds);
        for (int i = 0; i < u.length; i++){
            for (int j = 0; j < u[i].length; j++){
                System.out.print(u[i][j]);
            }
            System.out.println();
        }

        System.out.println("sub");
        c = this.substitue(u);
        for (int i = 0; i < u.length; i++){
            for (int j = 0; j < u[i].length; j++){
                System.out.print(u[i][j]);
            }
            System.out.println();
        }

        System.out.println("key");
        c = this.subkeyMix(c,this.rounds + 1);
        for (int i = 0; i < u.length; i++){
            for (int j = 0; j < u[i].length; j++){
                System.out.print(u[i][j]);
            }
            System.out.println();
        }

        // output cipher text
        texts[0] = u;
        texts[1] = c;

        return texts;
    }

    /*
     * MAIN METHODS FOR SPN
     */

    // XOR values with subkey, r is round number
    public int[][] subkeyMix(int[][] t, int r){
        int[][] u = t;
        int offset = (16*(r-1));
        try {
            for( int i = 0; i < t.length; i++ ){
                for ( int j = 0; j < t[i].length; j++ ){
                    int ans = 0;
                    if (t[i][j] != this.subkey[i+j + offset]){
                        ans = 1;
                    }
                    u[i][j] = ans;
                }
                offset+=3;
            }
        } catch(Exception e){
            System.out.println(e);
        }
        return u;
    }

    // substitutes a list of intermediate text inputs. Should be 16 in a [4][4] format
    public int[][] substitue(int[][] t){
        int[][] u = t;
        for (int i = 0; i < t.length; i++ ){
            int[] q = t[i];
            int index = binaryToInt(q);
            int subbedNum = this.substitutions[index];
            u[i] = intToBinary(subbedNum);
        }
        return u;
    }

    // permutes a list text inputs 
    public int[][] permute(int[][] t){     
        int[][] u = t;

        for (int i = 0; i < t.length; i++){
            for (int j = 0; j < t[i].length; j++){
                int permIndex = (i*4) + j;
                u[permIndex / 4][permIndex % 4] = t[i][j];
            }
        }

        return u;
    }

    /*
     * HELPER METHODS
     */

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
}
