import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles parsing lines from a file. Does this by:
 * 1: Gathering a rawLine.
 * 2: Sanitizing the rawLine into a cleanLine.
 * 3: Gathering the COMMAND_TYPE from the cleanLine.
 * 4: Parses the cleanLine based upon what COMMAND_TYPE has been retrieved.
 *
 * Repeat this process on a per line basis, until text file has been fully read.
 *
 * @author Mark Alan Vincent II
 * @version 1.0
 */
public class Parser {

    // constant instanced variables.
    private final Command NO_COMMAND = Command.NO_COMMAND;
    private final Command A_COMMAND = Command.A_COMMAND;
    private final Command C_COMMAND = Command.C_COMMAND;
    private final Command L_COMMAND = Command.L_COMMAND;

    // file instanced variables
    private Scanner inputFile;
    private int     lineNumber;
    private String  rawLine;

    // parsed instanced variables.
    private String  cleanLine;
    private Command commandType;
    private String  symbol;
    private String  destMnemonic;
    private String  compMnemonic;
    private String  jumpMnemonic;

    /*
    Drivers
     */

    /**
     * Constructor for parsing a file.
     * pre: provided file is ASM file
     * post: if file can't be opened, throws and exception, else opens a I/O stream with file.
     * @param fileName the name of the file to parse.
     */
    public Parser(String fileName) {
        try {
            inputFile = new Scanner(new File(fileName));
        } catch (IOException e) {
            System.err.println("File not found, or could not be accessed. Please try with a different path / file.");
            System.exit(0);
        }
    }

    /**
     * returns boolean if more commands left, closes stream if else.
     * pre: file stream is open.
     * post: returns true if more commands exist, returns false if else and closes the stream.
     * @return boolean if more commands exist.
     */
    public boolean hasMoreCommands() {
        if (inputFile.hasNextLine()) {
            return true;
        } else {
            inputFile.close();
            return false;
        }
    }

    /**
     * Advances the parser by 1 line of the file.
     * pre: file stream is open, called only if hasMoreCommands() is true.
     * post: current instruction parts put into instance variables.
     */
    public void advance() {
        if (hasMoreCommands()) {
            rawLine = inputFile.nextLine();
            cleanLine();
            parse();
            // increments lineNumber
            lineNumber++;
        }

    }



    /*
    Parsing helpers
     */

    // cleans the current line.
    private void cleanLine() {
        cleanLine = rawLine.trim();
        int index  = cleanLine.indexOf("//");
        cleanLine = (index!= -1) ?
                    cleanLine.substring(0, index).trim().replaceAll(" ", "")
                    : cleanLine.replaceAll(" ", "");
    }

    // gathers the command type.
    private void parseCommandType(){
        if (cleanLine.length() == 0) {
            // NO
            commandType = NO_COMMAND;
        } else {
            char indexZero = cleanLine.charAt(0);
            switch (indexZero) {
                case '@':
                    commandType = A_COMMAND;
                    break;
                case '(':
                    commandType = L_COMMAND;
                    break;
                default:
                    commandType = C_COMMAND;
                    break;
            }
        }
    }

    // parses the cleanLine.
    private void parse() {
        // gets the commandType
        parseCommandType();

        // parses depending upon commandType
        switch (commandType) {
            case NO_COMMAND:
                // nothing happens.
                break;
            case C_COMMAND:
                // parse as a C instruction.
                parseDest();
                parseComp();
                parseJump();
                break;
            default:
                // parse as a A or L command.
                parseSymbol();
                break;
        }

    }

    // parses symbol for A- or L- commands
    private void parseSymbol() {
        if (cleanLine.charAt(0) == '(') {
            // removes ( and ) from symbol
            String label = cleanLine.substring(1);
            symbol = label.substring(0, (label.indexOf(')')));
        } else {
            // removes @, everything afterward.
            symbol = cleanLine.substring(1);
        }
    }

    // parses the destination values
    private void parseDest() {
        // checks if the = sign exist or not.
        if (cleanLine.indexOf('=') != -1) {
            destMnemonic = cleanLine.substring(0, cleanLine.indexOf('='));

        } else {
            destMnemonic = null;
        }
    }

    // parses the computation values
    private void parseComp() {
        // checks if the = sign exist or not.
        if (cleanLine.indexOf('=') != -1) {
            // checks if the ; sign exist or not.
            if (cleanLine.indexOf(';') != -1) {
                compMnemonic = cleanLine.substring(cleanLine.indexOf('=') + 1, cleanLine.indexOf(';'));
            } else {
                compMnemonic = cleanLine.substring(cleanLine.indexOf('=') + 1);
            }
        } else {
            // no =, checks if ';' exists.
            if (cleanLine.indexOf(';') != -1) {
                compMnemonic = cleanLine.substring(0, cleanLine.indexOf(';'));
            } else {
                // no =,  no ;, full value.
                compMnemonic = cleanLine;
            }
        }
    }

    // parses the jump values.
    private void parseJump() {
        // checks if the ; exist or not.
        if (cleanLine.indexOf(';') != -1) {
            jumpMnemonic = cleanLine.substring(cleanLine.indexOf(';') + 1);
        } else {
            // no ;, no jump, null value.
            jumpMnemonic = null;
        }
    }



    /*
    useful getters
     */

    /**
     * pre: parse has already been run.
     * post: gives a valid commandType variable.
     * @return the commandType variable.
     */
    public Command getCommandType() {
        return commandType;
    }

    /**
     * pre: parse has already been run.
     * post: gives a valid symbol if commandType is A or L.
     * @return the symbol variable.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * pre: the parse method has already been run.
     * post: gives a valid destination mnemonic for use with a lookup table.
     * @return the destMnemonic variable.
     */
    public String getDestMnemonic() {
        return destMnemonic;
    }

    /**
     * pre: the parse method has already been run.
     * post: gives a valid computation mnemonic for use with a lookup table.
     * @return the compMnemonic variable.
     */
    public String getCompMnemonic() {
        return compMnemonic;
    }

    /**
     * pre: the parse method has already been run.
     * post: gives a valid jump mnemonic for use with a lookup table.
     * @return the jumpMnemonic variable.
     */
    public String getJumpMnemonic() {
        return jumpMnemonic;
    }



    /*
    debugging getters
     */

    /**
     * returns the current rawLine variable.
     * pre:
     * post:
     * @return rawLine variable.
     */
    public String getRawLine() {
        return rawLine;
    }

    /**
     * returns the current cleanLine variable.
     * pre:
     * post:
     * @return cleanLine variable.
     */
    public String getCleanLine() {
        return cleanLine;
    }

    /**
     * returns the current lineNumber variable.
     * pre:
     * post:
     * @return lineNumber variable.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    // DEBUGGING Method.
    public static void main(String[] args) {

        // For testing parser's ability to gather data.
//        Parser parser = new Parser("test.asm");
//        parser.cleanLine = "0;JMP";
//        parser.parse();
//        System.out.println(parser.getCleanLine());
//        System.out.println("C:" + parser.getCommandType() + " S:" + parser.getSymbol() + " D:" + parser.getDestMnemonic() + " C:"
//                           + parser.getCompMnemonic() + " J:" + parser.getJumpMnemonic());
//
//        // For testing translation of parser's data.
//        CInstructionMapper mapper = new CInstructionMapper();
//        // OP CODE
//        String boolValue = "111";
//        // A value + compValue
//        boolValue = boolValue + mapper.comp(parser.getCompMnemonic());
//        // destValue + jumpValue
//        boolValue = boolValue + mapper.dest(parser.getDestMnemonic()) + mapper.jump(parser.getJumpMnemonic());
//
//        System.out.println(parser.getCleanLine() + "  =>  " + boolValue);

    }

}
