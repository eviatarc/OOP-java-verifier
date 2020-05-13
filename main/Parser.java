package oop.ex6.main;

import oop.ex6.Sexcepsion.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * a class that creates a parser that reades the lines and gives information about each one
 * also fillets each line and analyze it
 */
public class Parser {

    private static int VARIABLE_DECLARATION = 0;
    private static int VARIABLE_ASSIGNMENT = 1;
    private static int METHOD_DECLARATION = 2;
    private static final int METHOD_CALL = 3;
    private static final int CONDITION_ENTRANCE = 4;

    private static final String  METHOD_DEC =  "^\\s*void\\s*(\\S+)\\s*\\(\\s*((?:\\S+\\s*\\S+\\s*," +
            "\\s*)*(?:\\S+\\s*\\S+)?)\\s*\\)\\s*\\{\\s*$";
    private static final Pattern METHOD_DEC_PATTERN = Pattern.compile(METHOD_DEC);
    private static final String  EMPTY_LINE =  "^\\s*(?:\\/\\/.*)?$";
    private static final Pattern EMPTY_LINE_PATTERN = Pattern.compile(EMPTY_LINE);
    private static final String VAR_DEC = "^\\s*(final)?\\s*(int|String|double|boolean|char)\\s*" +
            "((?:\\S+\\s*(?:=\\s*[^,\\s]+)?\\s*,\\s*)*\\S+\\s*(?:=\\s*[^,\\s]+)?)\\s*;\\s*$";
    private static final Pattern VAR_DEC_PATTERN = Pattern.compile(VAR_DEC);
    private static final String VAR_ASSIG = "^\\s*(\\S+\\s*=\\s*[^,\\s]*)\\s*;\\s*$";
    private static final Pattern VAR_ASSIG_PATTERN = Pattern.compile(VAR_ASSIG);
    private static final String METHOD_CALL_LINE = "^\\s*(\\S+)\\s*\\(((?:\\s*\\S+\\s*,)" +
            "*(?:\\s*\\S+\\s*)?)\\)\\s*;\\s*$";
    private static final Pattern METHOD_CALL_PATTERN = Pattern.compile(METHOD_CALL_LINE);
    private static final String CONDITION = "^\\s*(?:if|while)\\s*\\(\\s*(\\S+)\\s*\\)\\s*\\{\\s*$";
    private static final Pattern CONDITION_PATTERN = Pattern.compile(CONDITION);


    Scanner scanner;
    String curLine;
    int scopeNum;
    int nextType;
    ArrayList<String> curSplitLine;
    private boolean insideMethod;
    String lastLine;

    /**
     * constructor for the parser object
     * @param FileRecived - the file the parser will read
     * @throws FileNotFoundException
     */
    public Parser(File FileRecived) throws FileNotFoundException {
        try{
            scanner = new Scanner(FileRecived);
        }catch (FileNotFoundException e) {
            throw new FileNotFoundException("2 - file not found!");
        }
        scopeNum = 0;
        nextType = -1;
        curSplitLine = new ArrayList<>();
        lastLine = "";
    }

    /** checks if a given line is empty - real empty
     * @param line - the line to check
     * @return true if the line is empty
     */
    private boolean isEmpty(String line) {
        Matcher m = EMPTY_LINE_PATTERN.matcher(line);
        return m.matches();
    }

    /**
     * a function that determinates if a given line is a method declaration line
     * @param line - the line that needed to be checked
     * @return - true if the line is a daclaration line of a method
     * @throws Exception
     */
    private boolean isMethod(String line) throws SExceptionsTypeOne {
        Matcher m = METHOD_DEC_PATTERN.matcher(line);
        if (m.matches()) {
            curSplitLine.add(m.group(1)); // func name
            if (m.group(2).length() == 0) {
                return true;
            }
            String[] split = m.group(2).split(",|\\s+", 0);
            for (int  i = 0; i < split.length; i += 1) {
                try {
                    while (split[i].length() == 0) {
                        i += 1;
                    }
                    curSplitLine.add(split[i]); // type
                    i += 1;
                    while (split[i].length() == 0) {
                        i += 1;
                    }
                    curSplitLine.add(split[i]); // name
                } catch (Exception e) {
                    throw new InvalidMethodDaclaration();
                }
            }
            return true;
        }
        return false;
    }

    /**
     * a function that determinates if a given line is a method call line
     * @param line - the line that needed to be checked
     * @return - true if the line is a call line of a method
     */
    private boolean isMethodCall(String line){
        Matcher m = METHOD_CALL_PATTERN.matcher(line);
        if(m.matches()){
            curSplitLine.add(m.group(1));
            String[] split = m.group(2).split(",|\\s+", 0);
            for (int  i = 0; i < split.length ; i += 1) {
                    while (split[i].length() == 0) {
                        i += 1;
                        if(i>=split.length){
                            break;
                        }
                    }
                    if(i>=split.length){
                        break;
                    }
                    curSplitLine.add(split[i]); // type
                    while (split[i].length() == 0) {
                        i += 1;
                    }

            }
                return true;
        }
        return false;
    }


    /**
     * a function that determinates if a given line is a variable daclaration line
     * @param line - the line that needed to be checked
     * @return - true if the line is a variable daclaration
     */
    private boolean isVarDec(String line) throws SExceptionsTypeOne {
        Matcher m = VAR_DEC_PATTERN.matcher(line);
        if (m.matches()) {
            try {
                if (line.contains("final")) {
                    curSplitLine.add(m.group(1)); // there's final
                } else {
                    curSplitLine.add(null); // there's no final
                }
                curSplitLine.add(m.group(2)); // type
                String[] split = m.group(3).split(",", 0);
                for (int i = 0; i < split.length; i += 1) {

                    while (split[i].length() == 0) {
                        i += 1;
                    }
                    split[i] = split[i].replaceAll("\\s+", "");
                    if (split[i].contains("=")) { // there's assignment
                        String[] subSplit = split[i].split("=", 0);
                        curSplitLine.add(subSplit[0]);
                        curSplitLine.add(subSplit[1]);
                    } else { // there's no assignment
                        curSplitLine.add(split[i]);
                        curSplitLine.add(null);
                    }
                }
            } catch (Exception e) {
                throw new InvalidValueAssinmentException();
            }
            return true;
        }
        return false;
    }

    /**
     * a function that determinates if a given line is a variable assignment line
     * @param line - the line that needed to be checked
     * @return - true if the line is a variable assignment
     */
    private boolean isVarAssig(String line) throws SExceptionsTypeOne {
        Matcher m = VAR_ASSIG_PATTERN.matcher(line);
        if (m.matches()) {
            try {
                line = m.group(1);
                line = line.replaceAll("\\s+", "");
                String[] split = line.split("=", 0);
                curSplitLine.add(split[0]);
                curSplitLine.add(split[1]);

            } catch (Exception e) {
                throw new InvalidValueAssinmentException();
            }

            return true;
        }
        return false;
    }

    /**
     * a function that determinates if a given line is a condition line
     * @param line - the line that needed to be checked
     * @return - true if the line is a condition of any kind
     */
    private boolean isConditionLine(String line) throws SExceptionsTypeOne {
        line = line.replaceAll("\\s+", "");
        Matcher m = CONDITION_PATTERN.matcher(line);
        if (m.matches()) {
            try {
                String condition = m.group(1);
                String[] split = condition.split("(\\|\\|)|(\\&\\&)", 0);
                curSplitLine.addAll(Arrays.asList(split));
                return true;

            } catch (Exception e) {
                throw new InvalidConditionArguments();
            }
        }
        return false;
    }

    /**
     * a function that determinates if the parser still have any relevant line to read left
     * @return true if there is for the first run of the program - since all the
     * types of lines are possible for our first run are not much and its easy to
     * recognize them
     */
    boolean hasNext() throws SExceptionsTypeOne {
        while (scanner.hasNext()) {
            curLine = scanner.nextLine();
            if (!isEmpty(curLine)) {
                if (scopeNum == 0) {
                    curSplitLine = new ArrayList<>();
                    if (isMethod(curLine)) {
                        scopeNum += 1;
                        nextType = METHOD_DECLARATION;
                        return true;
                    } else if (isVarDec(curLine)) {
                        nextType = VARIABLE_DECLARATION;
                        return true;
                    } else if (isVarAssig(curLine)) {
                        nextType = VARIABLE_ASSIGNMENT;
                        return true;
                    }
                    else {
                        throw new CannotRecognizeValidLine();
                    }
                }
                else {
                    if (curLine.contains("{")) {
                        scopeNum += 1;
                    } else if (curLine.contains("}")) {
                        if (curLine.trim().equals("}")) {
                            scopeNum -= 1;
                            if (scopeNum < 0) {
                                throw new TooMuchCloseScopeSignsException();
                            }
                        }
                        else {
                            throw new CannotRecognizeValidLine();
                        }
                    }
                }
            }
        }
        return false;
    }


    /**
     * same as hasNext() but for the second run that check for different kind of lines
     * @return true if found any relevant lines for the second run
     * @throws Exception
     */
    boolean hasNextSecond() throws SExceptionsTypeOne {
        lastLine = curLine;
        while (scanner.hasNext()) {
            curLine = scanner.nextLine();
            if (!isEmpty(curLine)) {
                curSplitLine = new ArrayList<>();
                if (isMethod(curLine)) {
                    scopeNum += 1;
                    nextType = METHOD_DECLARATION;
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * a function that reades inside a method and more specific inside any scope,
     * works recursively for inner scoopes and so on
     * @param isThisAMethod - a boolean that tells if this method called from inside a method or from
     *                      a condition since thier behavier inside is a bit different
     *                      determinates what kind of line is the current reading line
     * @return - not using it returns value just for self check
     */
    public boolean readInsideMethod(boolean isThisAMethod) throws SExceptionsTypeOne {
        int theDeepCounter = 1;
        while (scanner.hasNext()){
            lastLine = curLine;
            curSplitLine = new ArrayList<>();
            curLine = scanner.nextLine();
            if(!curLine.isEmpty()){
                if(isMethod(curLine)){
                    throw new MethodInsideMethodException();
                }
                else if(isVarDec(curLine)){
                    nextType=VARIABLE_DECLARATION;
                    return true;
                }
                else if(isVarAssig(curLine)){
                    nextType=VARIABLE_ASSIGNMENT;
                    return true;
                }
                else if(isMethodCall(curLine)){
                    nextType = METHOD_CALL;
                    return true;
                }
                else if(isConditionLine(curLine)){
                    nextType = CONDITION_ENTRANCE;
                    return true;
                }
                else if (curLine.trim().equals("}")) {
                    if(!(lastLine.trim().equals("return;")) && isThisAMethod){
                        throw new NoReturnStatementAtEndOfMethod();

                    }
                        return false;
                    }
                }
            }
            return false;
        }


    /**
     * @return the type of the current readed line
     */
    int getNext() {
        return nextType;
    }

    /**
     * @param line - the line that neede to be cutted
     * @return the cutted line to pieces as necsesery
     */
    ArrayList<String> getSplitLine(String line) {
        return curSplitLine;
    }

}









