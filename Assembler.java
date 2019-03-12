import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Handles the driving portion of the program, assembling ASM code into HACK machine language.
 * The first pass handles translating symbols such as Labels and Variables into integers.
 * The second pass handles translating ASM with integers into HACK machine language.
 * @author Mark Alan Vincent II
 * @version 1.0
 */
public class Assembler {

    // Main Method of the Assembler.
    public static void main(String[] args) {

        // constructs a new symbolTable, asks user for path to ASM File Name.
        SymbolTable table = new SymbolTable();
        Scanner keyboard = new Scanner(System.in);
        String fileName;

        // User Interaction
        System.out.println("Welcome to HACK assembly, written by Mark Alan Vincent II.");
        System.out.print("Please enter the path to the ASM file you'd like to assemble: ");
        fileName = keyboard.nextLine();
        System.out.println("starting to assemble!");

        // start time
        long startTime = System.nanoTime();

        // assemble
        firstPass(fileName, table);

        // time for successful assemble.
        System.out.println("Assembled in " + Long.toString((System.nanoTime() - startTime) / 1000000) + " MS");

    }

    // Method for the first pass of assembling machine code.
    // Cleans the initial ASM file for easy reading on second pass.
    private static void firstPass(String asmFileName, SymbolTable table) {

        // Creates some tools to work with.
        Parser parser = new Parser(asmFileName);
        BufferedWriter writer = null;
        String passOneText = "";
        int currentVariableValue = 16;
        int currentROMAddress = 0;

        // creates temp file to write to.
        try {
            File aFile = new File("temp.asm");
            aFile.deleteOnExit();
            writer = new BufferedWriter(new FileWriter(aFile));
        } catch (IOException e) {
            handleError(" BAD BINARY FILE NAME. COULD NOT RESOLVE. ");
        }

        // Begins the first pass of the ASM file.
        while (parser.hasMoreCommands()) {
            // parse line.
            parser.advance();

            // translate line.
            String currentLine = "";

            switch (parser.getCommandType()) {
                case C_COMMAND:
                    // sets currentLine to the clean line from parser.
                    currentLine = parser.getCleanLine();
                    break;
                case A_COMMAND:
                    // A instruction.
                    // checks symbol to see if it's valid.
                    String variable = parser.getSymbol();
                    boolean isNum = true;

                    // checks if this is a value ex:@256
                    try {
                        Integer.parseInt(variable);
                    } catch (NumberFormatException e) {
                        isNum = false;
                    } catch (NullPointerException e) {
                        isNum = false;
                    }

                    if (!table.validName(variable)) {
                        if (!isNum) {
                            System.out.println(variable);
                            handleError("Bad variable name at line " + parser.getLineNumber());
                        }
                    }

                    // valid name, must tell difference between @x and @LABEL. All caps check.
                    boolean label = true;
                    String upper = variable.toUpperCase();
                    for (int i = 0; i < upper.length(); i++) {
                        if (variable.charAt(i) != upper.charAt(i)) {
                            label = false;
                        }
                    }

                    // if isNum is true, than label must be false.
                    if (isNum) {
                        label = false;
                    }

                    // handles different case for variable being a label
                    if (!label) {
                        // variable, ex: @mark
                        // check symbolTable -> true: get key value into string | false: add key value into table, string
                        if (isNum) {
                            currentLine = "@" + variable;
                        } else if (table.contains(variable)) {
                            currentLine = "@" + table.getAddress(variable);
                        } else {
                            table.addEntry(variable, Integer.toString(currentVariableValue));
                            currentLine = "@" + table.getAddress(variable);
                            currentVariableValue++;
                        }
                    } else if (!isNum){
                        // label, ex: (MARK)
                        // going to add invalid character for marking purposes as we don't know the line value yet.
                        // this character will be a ?
                        if (table.contains(variable)) {
                            // Label value already exists, can just grab it now.
                            currentLine = "@" + table.getAddress(variable);
                        } else {
                            // marking of unknown, will be checked later on.
                            currentLine = "?@" + variable;
                        }
                    }

                    break;
                case L_COMMAND:
                    // L instruction.

                    // checks symbol to see if it's valid.
                    String var = parser.getSymbol().toUpperCase();

                    if (!table.validName(var)) {
                        handleError("Bad variable name at line " + parser.getLineNumber());
                    }

                    // valid name, must be all caps for a label though.
                    boolean isLabel = true;
                    String lUpper = var.toUpperCase();
                    for (int i = 0; i < lUpper.length(); i++) {
                        if (var.charAt(i) != lUpper.charAt(i)) {
                            isLabel = false;
                        }
                    }

                    // if not all caps, throws an error and exits program.
                    if (!isLabel) {
                        handleError("Bad label name at line " + parser.getLineNumber());
                    }

                    // decides what to do with valid label name.
                    if (table.contains(var)) {
                        // already contains this label...? going to ignore...
                    } else {
                        // does not contain this label. place in table, ignore any new text.
                        // value is based off of the current ROM line num, + 1 (always points to below label)
                        table.addEntry(var, Integer.toString(currentROMAddress));
                    }

                    // current line is also a blank.
                    currentLine = "";

                    break;
                default:
                    // NO_COMMAND
                    // currentLine will be a blank.
                    currentLine = "";
                    break;
            }

            // writes to passOneText String.
            // A into A into C into NO into L
            // notice how NO and L are both blanks.
            // format looks like: @23;@100;D=D+D;JMP;;;
            // increments the current ROM address IF currentLine isn't a blank.
            if (currentLine.length() != 0) {
                // DEBUG
                // System.out.println(currentROMAddress + ": " + currentLine);
                passOneText = passOneText + "|" + currentLine;
                currentROMAddress++;
            }
            // End of file.
        }


        // Now we need to finish translating the passOneText that has missing label markers, and write to a temp file
        // for the second pass to translate.
        // breaks down passOneText into tokens, each token is separated by a /
        // filters out first |
        passOneText = passOneText.substring(1);
        StringTokenizer tokenizer = new StringTokenizer(passOneText, "|");
        int amountTokens = tokenizer.countTokens();

        // begins processing the tokens into the temp file.
        for (int i = 0; i < amountTokens; i++) {
            // checks token for marked symbol, ?, otherwise writes token directly to file.
            String token = tokenizer.nextToken();
            if (token.charAt(0) == '?') {
                // does a loop up in the symbolTable for the address of this label.
                token = "@" + table.getAddress(token.substring(2));
                // write
                try {
                    writer.write(token + "\n");
                    writer.flush();
                } catch (IOException e) {
                    handleError("Problem writing to temp file.");
                }
            } else {
                // write
                try {
                    writer.write(token + "\n");
                    writer.flush();
                } catch (IOException e) {
                    handleError("Problem writing to temp file.");
                }
            }

            // DEBUG
            // System.out.println(i + ": " + token);
        }

        // first pass has completed, now passes temp file to second pass for
        // translation into machine language. writes hack file as fileName.hack
        try {
            writer.close();
        } catch (IOException e) {
            handleError("");
        }
        secondPass("temp.asm", table, (asmFileName.substring(0, asmFileName.indexOf('.')) + ".hack"));

    }

    // Method for the second pass of assembling machine code.
    // Actually writes to a binary file.
    private static void secondPass(String asmFileName, SymbolTable table, String binaryFileName) {
        // Creates three object tools for parsing, translating, and writing.
        Parser parser = new Parser(asmFileName);
        CInstructionMapper mapper = new CInstructionMapper();
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(binaryFileName));
        } catch (IOException e) {
            handleError(" BAD BINARY FILE NAME. COULD NOT RESOLVE. ");
        }

        // instanced variable.
        String boolValue = "";

        // Parses the ASM file.
        while (parser.hasMoreCommands()) {
            // parse line.
            parser.advance();

            // translate line.
            switch (parser.getCommandType()) {
                case C_COMMAND:
                    // OP CODE
                    boolValue = "111";
                    // A value + compValue
                    boolValue = boolValue + mapper.comp(parser.getCompMnemonic());
                    // destValue + jumpValue
                    boolValue = boolValue + mapper.dest(parser.getDestMnemonic()) + mapper.jump(parser.getJumpMnemonic());
                    break;
                case A_COMMAND:
                    // A instruction is a simple binary translation.
                    boolValue = decimalToBinary(Integer.valueOf(parser.getSymbol()));
                    break;
                case L_COMMAND:
                    break;
                default:        // NO_COMMAND
                    break;
            }

            // Writing to Hack file.
            if (boolValue.length() != 0) {
                // DEBUG LINE
                // System.out.println(parser.getCleanLine() + "  => " + boolValue);
                try {
                    writer.write(boolValue);
                    writer.newLine();
                    writer.flush();
                } catch (IOException e) {
                    handleError(" ISSUE WRITING TO HACK FILE. PLEASE CHECK NAME, FILE LOCATION, AND FILE NAME. ");
                }
            }

            // End of file.
        }

        // End of file, displaying some stats.
        System.out.println("Total amount of lines in ASM file: " + parser.getLineNumber());

    }

    // Method for converting a number in a binary value.
    private static String decimalToBinary(int number) {
        // Creates an array of 100 number values. Overkill, though simple.
        int numArray[] = new int[16];

        // Creates a counter.
        int counter = 0;
        int x = number;

        while (x > 0) {
            numArray[counter] = x % 2;
            x = x / 2;
            counter++;
        }

        String value = "";
        for (int j = 15; j >= 0; j--) {
            value = value + (Integer.toString(numArray[j]));
        }
        return value;
    }

    // Method for handling an error, allows the program to place a custom error MSG tag for tracing.
    private static void handleError(String msg) {
        System.err.println("There was an error, exiting assembler. Please try again. CODE: " + msg);
        System.exit(0);
    }

}
