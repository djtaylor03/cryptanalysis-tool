package ec.app.cryptanalysis_tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO add robustness and streamlining
public class SPN {

    // Main Class
    public static void main(String[] args) throws IOException {

        int max = 1000;    // number of iterations
        File file = new File("C:\\Users\\Dan\\Dissertation\\results.txt");  // file path TODO add as param
        FileWriter writer = new FileWriter(file);
        writer.write("SEED\t[PLAIN-TEXT]\t[INTERMEDIATE-CIPHER-TEXT]\t[CIPHER-TEXT]\n");

        // loop through, incrementing seeds for random generator
        for (int i = 0; i < max; i++ ){
            // generate seeded random keys
            Random rand = new Random(i);

            // generate key (5 being number of subkeys needed * 16 being length of pt)
            int[] key = new int[80];

            for (int k = 0; k < key.length; k++) {
                key[k] = rand.nextInt() % 2;
            }

            int[][] pt = {
                {0,0,0,1},
                {0,0,1,0},
                {0,1,0,0},
                {1,0,0,0}};

            // run SPN with given input with seeded random keys
            SPN spn = new SPN(key, pt);
            int[][][] spnOuts = spn.runSPN();

            // add spnOuts to csv file
            List<String> data = new ArrayList<String>();
            // convert to strings SEED, PT[][], UT[][], CT[] 
            data.add(Integer.toString(i) + "\t");
            for (int a = 0; a < spnOuts.length; a++){
                String s = "";
                for (int b = 0; b < spnOuts[a].length; b++){
                    if (b == 0) { s = s + "["; }
                    for (int c = 0; c < spnOuts[a][b].length; c++){
                        s = s + Integer.toString(spnOuts[a][b][c]);
                        if (c == (spnOuts[a][b].length - 1) & (b != (spnOuts[a].length - 1))) { s = s + ","; }
                    }
                    if (b == (spnOuts[a].length - 1)) { s = s + "]\t"; }
                }
                data.add(s);
            }

            // file handling
            for (String s: data){
                writer.write(s);
            }
            writer.write("\n");
        }
        writer.close();  
    }

    int[] substitutions;    // table for substitutions
    int[] permutations;     // table for permutations
    int[] subkey;           // key for mixing
    int rounds;             // number of rounds an SPN has

    // different text types
    int [][] plaintext;
    int [][] intermediatetext = new int[4][4];
    int [][] ciphertext = new int[4][4];

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
    public SPN(int[] k, int[][] pt){
        int[] s = {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7};
        substitutions = s;

        int[] p = {0,4,8,12,1,5,9,13,2,6,10,14,3,7,11,15};
        permutations = p;

        subkey = k;
        rounds = 4;

        plaintext = pt;
        for (int i = 0; i < pt.length; i++){
            for (int j = 0; j < pt[i].length; j++){
                intermediatetext[i][j] = pt[i][j];
            }
        }
    }

    // blank constructor for SPN
    public SPN(int[] s, int[] p, int[] k, int r, int[][] pt){
        substitutions = s;
        permutations = p;
        subkey = k;
        rounds = r;
        plaintext = pt;
        intermediatetext = pt;
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
    public int[][][] runSPN(){

        // N-1 whole rounds
        for ( int i = 1; i < this.rounds; i++ ){
            
            this.intermediatetext = this.subkeyMix(this.intermediatetext,i);
            
            this.intermediatetext = this.substitue(this.intermediatetext);
            
            this.intermediatetext = this.permute(this.intermediatetext);
           
        }

        // final round
        this.intermediatetext = this.subkeyMix(this.intermediatetext,this.rounds);
        
        // push intermediatetext values to ciphertext
        for (int i = 0; i < this.intermediatetext.length; i++){
            for (int j = 0; j < this.intermediatetext[i].length; j++){
                this.ciphertext[i][j] = this.intermediatetext[i][j];
            }
        }

        this.ciphertext = this.substitue(this.ciphertext);
        
        this.ciphertext = this.subkeyMix(this.ciphertext,this.rounds + 1);
        
        // output cipher text
        int[][][] texts = {this.plaintext, this.intermediatetext,this.ciphertext};

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
            int subbedNum = this.substitutions[binaryToInt(t[i])];
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
        int[] bs = new int[4];
        for (int p = 3; p > -1; p--){
            int remainder = i % (int)Math.pow(2,p);
            if ( i == remainder) { bs[3-p] = 0; }
            else { 
                bs[3-p] = 1;
                i = remainder;
            }
        }
        return bs;
    }

    // quick little print function for int[][] structures
    public void print(int[][] t){
        for (int i = 0; i < t.length; i++){
            for (int j = 0; j < t[i].length; j++){
                System.out.print(t[i][j]);
            }
            System.out.println();
        }
    }

    public void print(int[] t){
        System.out.println();
        for (int i = 0; i < t.length; i++){
            System.out.print(t[i]);
        }
        System.out.println();
    }
}
