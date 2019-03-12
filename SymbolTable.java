import java.util.HashMap;

/**
 * Handles the symbolTable data structure. Includes methods for interacting with the table through symbolTable objects.
 * @author Mark Vincent II
 * @version 1.0
 */
public class SymbolTable {

    // constants
    private final String INITIAL_VALID_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_$.:";
    private final String ALL_VALID_CHARS = INITIAL_VALID_CHARS + "0123456789";


    // instanced variables.
    private HashMap<String, String> symbolTable;


    /**
     * Handles initializing the symbolTable with all predefined symbols.
     * pre: construct a symbolTable object within a class.
     * post: a default symbolTable hashMap with all predefined symbols as key, value pairs.
     */
    public SymbolTable() {

        // Initializes symbolTable with predefined values.
        symbolTable = new HashMap<>();
        symbolTable.put("R0", "0");
        symbolTable.put("R1", "1");
        symbolTable.put("R2", "2");
        symbolTable.put("R3", "3");
        symbolTable.put("R4", "4");
        symbolTable.put("R5", "5");
        symbolTable.put("R6", "6");
        symbolTable.put("R7", "7");
        symbolTable.put("R8", "8");
        symbolTable.put("R9", "9");
        symbolTable.put("R10", "10");
        symbolTable.put("R11", "11");
        symbolTable.put("R12", "12");
        symbolTable.put("R13", "13");
        symbolTable.put("R14", "14");
        symbolTable.put("R15", "15");
        symbolTable.put("SCREEN", "16384");
        symbolTable.put("KBD", "24576");
        symbolTable.put("SP", "0");
        symbolTable.put("LCL", "1");
        symbolTable.put("ARG", "2");
        symbolTable.put("THIS", "3");
        symbolTable.put("THAT", "4");

    }

    /**
     * Handles adding new key value pairs into the symbolTable.
     * @param symbol the symbol, or key.
     * @param address the address, or value. Though it's a String, I've done this to be in parallel with CInstructionMapper.class
     * @return if the entry had been added to the symbolTable successfully.
     */
    public boolean addEntry(String symbol, String address) {
        symbolTable.put(symbol, address);
        return symbolTable.containsKey(symbol);
    }

    /**
     * Handles checking if a key, or symbol, is located inside of the symbolTable.
     * @param symbol the key.
     * @return if the key is already inside of the symbolTable.
     */
    public boolean contains(String symbol) {
        return symbolTable.containsKey(symbol);
    }


    /**
     * Handles retreiving a integer value from the symbolTable, based on the symbol provided.
     * @param symbol the key to be used in looking up a value.
     * @return the int associated with the key provided.
     */
    public int getAddress(String symbol) {
        return Integer.parseInt(symbolTable.get(symbol));
    }

    /**
     * Handles checking if the symbol provided has a valid name. Goes off the predefined constants in this class.
     * @param symbol the symbol to check.
     * @return if the symbol is valid or not.
     */
    public boolean validName(String symbol) {

        // Checks first character to be valid.
        boolean valid = false;
        for (int i = 0; i < INITIAL_VALID_CHARS.length(); i++) {
            if (symbol.charAt(0) == INITIAL_VALID_CHARS.charAt(i)) {
                valid = true;
            }
        }

        // stops if the first character is not valid.
        if (!valid) {
            return false;
        }


        // Begins checking the rest of the symbol.
        String sub = symbol.substring(1);
        for (int i = 0; i < sub.length(); i++) {

            // checks each character of sub vs ALL_VALID_CHARS
            boolean thisCharValid = false;
            for (int j = 0; j < ALL_VALID_CHARS.length(); j++) {
                // if sub[i] is inside of ALL_VALID_CHARS, it's valid.
                if (sub.charAt(i) == ALL_VALID_CHARS.charAt(j)) {
                    thisCharValid = true;
                }
            }

            // if thisCharValid is false, then it's a invalid name.
            if (!thisCharValid) {
                return false;
            }
        }

        // if method didn't already return false, then this symbol is valid.
        return true;
    }

    // DEBUGGING METHOD
    public static void main(String[] args) {
        // tests valid name engine.
//        SymbolTable table = new SymbolTable();
//        System.out.println(table.validName("Mark5897_$*"));

        // label checking engine.
//        String string = "LABEL5897";
//        System.out.println(string);
//        System.out.println(string.toUpperCase());
//
//        boolean label = true;
//        String upper = string.toUpperCase();
//        for (int i = 0; i < upper.length(); i++) {
//            if (string.charAt(i) != upper.charAt(i)) {
//                label = false;
//            }
//        }
//
//        System.out.println("is " + string + " a label: " + label);
    }

}
