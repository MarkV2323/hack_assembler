import java.util.HashMap;

/**
 * CInstructionMapper is a class used for testing specific mnemonics, codes, literal C Instructions against a specific
 * hash table filled with binary values that represent the instruction in machine code.
 * @author vincentii
 * @version 1.0
 */
public class CInstructionMapper {

    // Hash Map, key: String | value: String.
    // Example: a, c1 - c6
    //                 a c1 - c6
    // Key: 0 | Value: 0 101010
    //                 a c1 - c6
    // Key: 1 | Value: 0 111111
    private HashMap<String, String> compCodes;
    private HashMap<String, String> jumpCodes;
    private HashMap<String, String> destCodes;

    // Constructor

    /**
     * Default constructor for building a CInstructionMapper Object.
     * pre: comp code = 7 bits (includes a), dest/jump codes = 3 bits
     * post: all hashMaps have lookups for valid codes.
     */
    public CInstructionMapper() {

        // Initializes compCodes and jumpCodes and destCodes
        compCodes = new HashMap<>();
        compCodes.put("0",   "0101010");
        compCodes.put("1",   "0111111");
        compCodes.put("-1",  "0111010");
        compCodes.put("D",   "0001100");
        compCodes.put("A",   "0110000");
        compCodes.put("!D",  "0001101");
        compCodes.put("!A",  "0110001");
        compCodes.put("-D",  "0001111");
        compCodes.put("-A",  "0110011");
        compCodes.put("D+1", "0011111");
        compCodes.put("A+1", "0110111");
        compCodes.put("D-1", "0001110");
        compCodes.put("A-1", "0110010");
        compCodes.put("D+A", "0000010");
        compCodes.put("D-A", "0010011");
        compCodes.put("A-D", "0000111");
        compCodes.put("D&A", "0000000");
        compCodes.put("D|A", "0010101");
        compCodes.put("M",   "1110000");
        compCodes.put("!M",  "1110001");
        compCodes.put("-M",  "1110011");
        compCodes.put("M+1", "1110111");
        compCodes.put("M-1", "1110010");
        compCodes.put("D+M", "1000010");
        compCodes.put("D-M", "1010011");
        compCodes.put("M-D", "1000111");
        compCodes.put("D&M", "1000000");
        compCodes.put("D|M", "1010101");


        jumpCodes = new HashMap<>();
        jumpCodes.put(null, "000");
        jumpCodes.put("JGT",  "001");
        jumpCodes.put("JEQ",  "010");
        jumpCodes.put("JGE",  "011");
        jumpCodes.put("JLT",  "100");
        jumpCodes.put("JNE",  "101");
        jumpCodes.put("JLE",  "110");
        jumpCodes.put("JMP",  "111");


        destCodes = new HashMap<>();
        destCodes.put(null, "000");
        destCodes.put("M",    "001");
        destCodes.put("D",    "010");
        destCodes.put("MD",   "011");
        destCodes.put("A",    "100");
        destCodes.put("AM",   "101");
        destCodes.put("AD",   "110");
        destCodes.put("AMD",  "111");


    }

    /**
     * Uses a string value as a key, to look up a value inside of the compCodes Hash Table.
     * pre: hashMaps are built with valid values.
     * post: returns a String of bits if valid, else returns null.
     * @param mnemonic the key.
     * @return the value associated inside of the compCodes Hash Table with passed key.
     */
    public String comp(String mnemonic) {
        // if the mnemonic String value exist within the compCode hash table, will return the binary value.
        return compCodes.get(mnemonic);
    }

    /**
     * Uses a string value as a key, to look up a value inside of the destCodes Hash Table.
     * pre: hashMaps are built with valid values.
     * post: returns a String of bits if valid, else returns null.
     * @param mnemonic the key.
     * @return the value associated inside of the compCodes Hash Table with passed key.
     */
    public String dest(String mnemonic) {
        return destCodes.get(mnemonic);
    }

    /**
     * Uses a string value as a key, to look up as value inside of the destCodes Hash Table.
     * pre: hashMaps are built with valid values.
     * post: returns a String of bits if valid, else returns null.
     * @param mnemonic the key.
     * @return the value associated inside of the compCodes Hash Table with passed key.
     */
    public String jump(String mnemonic) {
        return jumpCodes.get(mnemonic);
    }

}
