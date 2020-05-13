package oop.ex6.main;

import oop.ex6.Sexcepsion.InvalidNameRecognizeException;
import oop.ex6.Sexcepsion.InvalidTypeException;
import oop.ex6.Sexcepsion.InvalidValueAssinmentException;
import oop.ex6.Sexcepsion.SExceptionsTypeOne;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * a function that represent Svariabeles objects inside the program
 */
public class SVariable {

    static final String VALID_VAR_NAME = "[^\\d]*[_]*\\w+";
    public static final Pattern NAME_PATTERN = Pattern.compile(VALID_VAR_NAME);

    public final static String INT_VALUE_PAT = "-?\\d+";
    public final static String DOUBLE_VALUE_PAT = INT_VALUE_PAT + "(\\.[0-9]+)?";
    public final static String BOOLEAN_VALUE_PAT = "(" + DOUBLE_VALUE_PAT + ")|" + "(false|true)";
    public final static String CHAR_VALUE_PAT = "\'.?\'";
    public final static String STRING_VALUE_PAT = "\"{1}(\\d|.)*\"{1}";

    public static final Pattern INT_VAL_PATTERN = Pattern.compile(INT_VALUE_PAT);
    public static final Pattern DOUBLE_VAL_PATTERN = Pattern.compile(DOUBLE_VALUE_PAT);
    public static final Pattern BOOLEAN_VAL_PATTERN = Pattern.compile(BOOLEAN_VALUE_PAT);
    public static final Pattern CHAR_VAL_PATTERN = Pattern.compile(CHAR_VALUE_PAT);
    public static final Pattern STRING_VAL_PATTERN = Pattern.compile(STRING_VALUE_PAT);

    public final static String INT_TYPE = "int";
    public final static String DOUBLE_TYPE = "double";
    public final static String BOOLEAN_TYPE = "boolean";
    public final static String CHAR_TYPE = "char";
    public final static String STRING_TYPE = "String";
    public final static String ALL_TYPES = "(" + INT_TYPE + "|" + DOUBLE_TYPE + "|" + BOOLEAN_TYPE + "|" +
            CHAR_TYPE + "|" + STRING_TYPE + ")";

    public static final Pattern INT_TYPE_PATTERN = Pattern.compile(INT_TYPE);
    public static final Pattern DOUBLE_TYPE_PATTERN = Pattern.compile(DOUBLE_TYPE);
    public static final Pattern BOOLEAN_TYPE_PATTERN = Pattern.compile(BOOLEAN_TYPE);
    public static final Pattern CHAR_TYPE_PATTERN = Pattern.compile(CHAR_TYPE);
    public static final Pattern STRING_TYPE_PATTERN = Pattern.compile(STRING_TYPE);
    public static final Pattern ALL_TYPES_PATTERN = Pattern.compile(ALL_TYPES);

    public static final String FINISH_CHECK = "FINISH!";


    public final boolean isFinal;
    public String name;
    public String type;
    public String val;


    /**
     * Svariable constructor
     * @param isFinal - is the variable is final
     * @param name - the name of the variable
     * @param type - the type of the variable
     * @param val - the value assigned to the variable
     */
    public SVariable(boolean isFinal,String name ,String type, String val){
        this.isFinal = isFinal;
        this.name = name;
        this.type = type;
        this.val = val;

    }

    /**
     * a function that works as a variable factory
     * @param declaration - a flag to know if the variable just declared or just assigned
     * @param splitLine - the splited line - already procceced
     * @param familierVars - all the variables already known in this scope or relate to it
     * @return - a list of variables created as demand
     * @throws Exception
     */
    public static ArrayList<SVariable> variableFactory(boolean declaration, ArrayList<String> splitLine ,
                                                       ArrayList<SVariable> familierVars)
            throws SExceptionsTypeOne {
        if (declaration){
            if (splitLine.get(0)!=null && splitLine.get(0).equals("final")) {
                return createVar(true,splitLine,familierVars);
            }else{
                return createVar(false,splitLine,familierVars);
            }
        }else{
            return assignOnly(splitLine.get(0),splitLine.get(1));
        }

    }

    /**
     * function that only assign a value to a variable
     * @param nameOfVar - name of the variable
     * @param valOfVar - the value of the variable
     * @return - list of reassigned variables
     */
    private static ArrayList<SVariable> assignOnly(String nameOfVar, String valOfVar) {
        SVariable tempVar = new SVariable(false,nameOfVar,null,valOfVar);
        ArrayList<SVariable> justAVar = new ArrayList<>();
        justAVar.add(tempVar);
        return justAVar;
    }

    /**
     * a function that create a single variable
     * @param isFinal - a flag to know if the variable is final
     * @param splitLine - the splited line - already procceced
     * @param familierVariables - all the variables already known in this scope or relate to it
     * @return - variable created as demand
     */
    private static ArrayList<SVariable> createVar(boolean isFinal, ArrayList<String> splitLine,
                                                  ArrayList<SVariable> familierVariables)
            throws
            SExceptionsTypeOne {
        boolean foundAValAlready = false;
        String tempName = "";
        String tempVal = "";
        int numberOfVarToCreate = ((splitLine.size()-2)/2);
        String typeOfVar = splitLine.get(1);
        ArrayList<SVariable> allVariablesCreated = new ArrayList<>();
        for (int i=2;i<splitLine.size();i++){
            if(i%2==0){
                checkValidName(splitLine.get(i));
                tempName = splitLine.get(i);
            }else if(i%2!=0){
                if(!(FINISH_CHECK.equals(checkValidValueType(typeOfVar,splitLine.get(i),false)))) {
                    if(!((checkValidValueType(typeOfVar,splitLine.get(i),false))==null)){
                        boolean blackFlagDontKnowAnyOneThisName = true;

                        for (int checkIndexOfFamilier = 0; checkIndexOfFamilier < familierVariables.size();
                             checkIndexOfFamilier++) {
                            if (splitLine.get(i).equals(familierVariables.get(checkIndexOfFamilier).name)) {
                                String result = checkValidValueType(typeOfVar, familierVariables.get
                                        (checkIndexOfFamilier)
                                        .val,true);
                                tempVal = familierVariables.get(checkIndexOfFamilier).val;
                                foundAValAlready = true;
                                blackFlagDontKnowAnyOneThisName = false;
                                break;
                            }

                        }
                        if (blackFlagDontKnowAnyOneThisName){
                            throw new InvalidValueAssinmentException();
                        }
                    }


                }
                if (!foundAValAlready){
                    tempVal=splitLine.get(i);
                }
                SVariable tempVar = new SVariable(isFinal,tempName,typeOfVar,tempVal);
                allVariablesCreated.add(tempVar);
            }
        }
        return allVariablesCreated;
    }

    /**
     * a function that check the validity of a given name
     * @param givenName - the name to check
     */
    private static void checkValidName(String givenName) throws SExceptionsTypeOne {
        Matcher m = NAME_PATTERN.matcher(givenName);
//        System.out.println("valid name check");
//        System.out.println(m.matches());
        if (!m.matches()){
            throw new InvalidNameRecognizeException();
        }
    }

    /**
     * a function that by the given value and type chack its validity
     * @param typeOfVar - the type of variable to check
     * @param givenVal - the value assigned to the variable
     * @param lastChance - boolean that called when its time to do only one check - that means some checks
     *                   for a valid type value relation are more forgiving - the last one is not.
     * @return - multy result - uses as more than one kind of answer
     * @throws SExceptionsTypeOne
     */
    public static String checkValidValueType(String typeOfVar, String givenVal,boolean lastChance) throws
            SExceptionsTypeOne {
        Matcher typeMathcer = ALL_TYPES_PATTERN.matcher(typeOfVar);
        if (!typeMathcer.matches()){
            throw new InvalidValueAssinmentException();
        }
        Matcher INT_MATCH = INT_TYPE_PATTERN.matcher(typeOfVar);
        Matcher DOUBLE_MATCH = DOUBLE_TYPE_PATTERN.matcher(typeOfVar);
        Matcher BOOLEAN_MATCH = BOOLEAN_TYPE_PATTERN.matcher(typeOfVar);
        Matcher CHAR_MATCHER = CHAR_TYPE_PATTERN.matcher(typeOfVar);
        Matcher STRING_MATCH = STRING_TYPE_PATTERN.matcher(typeOfVar);

        if (givenVal!=null){
            if(INT_MATCH.matches()){
                Matcher tempM = INT_VAL_PATTERN.matcher(givenVal);
                if (!tempM.matches()){
                    if(lastChance){
                        throw new InvalidValueAssinmentException();
                    }
                    return INT_TYPE;
                }
                return FINISH_CHECK;
            }else if(DOUBLE_MATCH.matches()){
                Matcher tempM = DOUBLE_VAL_PATTERN.matcher(givenVal);
                if(!tempM.matches()){
                    if(lastChance){
                        throw new InvalidValueAssinmentException();
                    }
                    return DOUBLE_TYPE;
                }
                return FINISH_CHECK;
            }else if(BOOLEAN_MATCH.matches()){
                Matcher tempM = BOOLEAN_VAL_PATTERN.matcher(givenVal);
                if(!tempM.matches()){
                    if(lastChance){
                        throw new InvalidValueAssinmentException();
                    }
                    return BOOLEAN_TYPE;
                }
                return FINISH_CHECK;
            }else if (CHAR_MATCHER.matches()){
                Matcher tempM = CHAR_VAL_PATTERN.matcher(givenVal);
                if(!tempM.matches()){
                    if(lastChance){
                        throw new InvalidValueAssinmentException();
                    }
                    return CHAR_TYPE;
                }
                return FINISH_CHECK;
            }else if(STRING_MATCH.matches()){
                Matcher tempM = STRING_VAL_PATTERN.matcher(givenVal);
                if (!tempM.matches()){
                    if(lastChance){
                        throw new InvalidValueAssinmentException();
                    }
                    return STRING_TYPE;
                }
                return FINISH_CHECK;
            }else{
                throw new InvalidValueAssinmentException();
            }
        }else{
            return null;
        }
    }


    /**
     * a function that checks the type of a given value
     * @param givenVal - the value that is given
     * @return - the type of the value
     */
    public static String checkType(String givenVal) throws SExceptionsTypeOne {
        Matcher tempM;
        if (givenVal!=null){
            tempM = INT_VAL_PATTERN.matcher(givenVal);
            if (tempM.matches()){
                return INT_TYPE;
            }
            tempM = DOUBLE_VAL_PATTERN.matcher(givenVal);
            if(tempM.matches()){
                return DOUBLE_TYPE;
            }
            tempM = BOOLEAN_VAL_PATTERN.matcher(givenVal);
            if(tempM.matches()){
                return BOOLEAN_TYPE;
            }
            tempM = CHAR_VAL_PATTERN.matcher(givenVal);
            if(tempM.matches()){
                return CHAR_TYPE;
            }
            tempM = STRING_VAL_PATTERN.matcher(givenVal);
            if (tempM.matches()){
                return STRING_TYPE;
            }else{
                throw new InvalidTypeException();
            }
        }else{
            return null;
        }
    }

    /**
     * help function to ourselfs to check the variables
     * @return the full details about a variable
     */
    @Override
    public String toString() {
        return "name:" + name + ", type: " + type + ",isFinal: " + isFinal + "   value: " + val;
    }

    /**
     * a function that collect variables to a method object
     * @param cuttedLine - the line to collect variables from
     * @return array list of all the variables needed to be addede to a method when created
     */
    public static ArrayList<SVariable> collectVarsForMethod(ArrayList<String> cuttedLine) throws
            SExceptionsTypeOne {
        ArrayList<SVariable> methodVars = new ArrayList<>();
//        System.out.println("the cutted line recieved to collectvars   " + cuttedLine);
        String typeOfVar ="";
        for (int index=1;index<cuttedLine.size();index++){
            if(index%2!=0){
                typeOfVar = cuttedLine.get(index);
                checkValidValueType(typeOfVar,null,false);
            }
            else if(index%2==0){
                String nameOfVal = cuttedLine.get(index);
                checkValidName(nameOfVal);
                SVariable tempVariable = new SVariable(false,nameOfVal,typeOfVar,null);
                methodVars.add(tempVariable);
            }
        }
        return methodVars;
    }
}
