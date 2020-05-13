package oop.ex6.main;

import oop.ex6.Sexcepsion.InvalidAssignmentException;
import oop.ex6.Sexcepsion.InvalidConditionArguments;
import oop.ex6.Sexcepsion.InvalidNameAlreadyTaken;
import oop.ex6.Sexcepsion.SExceptionsTypeOne;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * a class that runs the program - the master class
 */
public class Sjavac {

    public static final int VARIABLE_DECLARATION = 0;
    public static final int VARIABLE_ASSIGNMENT = 1;
    public static final int METHOD_DECLARATION = 2;
    public static final int CONDITION_DECLARATION = 4;
    public static final int METHOD_CALL = 3;

    public static final boolean DECLARATION = true;

    public static int KIND_OF_LINE_INDEX = 0;
    public static int VARIABLE_DATA = 1;


    ArrayList<SVariable> GlobalVariableArray;
    ArrayList<SMethod> MethodsArray;
    File SjavaFile;

    /**
     * Sjava object that it is a program
     * @param fileLocation - the lication of the file
     */
    public Sjavac(String fileLocation) {
        this.GlobalVariableArray = new ArrayList<>();
        this.MethodsArray = new ArrayList<>();
        this.SjavaFile = new File(fileLocation);

    }


    /**
     * @param args - the argumants that is given to the program
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("2");
            System.err.println(" - Invalid number of argumants");
        }
        try {
            Sjavac newSjavaChecker = new Sjavac(args[0]);
            newSjavaChecker.FirstRun(newSjavaChecker.SjavaFile);
            newSjavaChecker.secondRun(newSjavaChecker.SjavaFile);
        } catch (SExceptionsTypeOne e) {
            System.out.println("1");
            System.err.println(e.getClass());
            return;
        } catch (Exception e) {
            System.out.println(2);
            System.err.println("something went wrong, cant read file");
            return;
        }
        System.out.println("0");


    }

    /**
     * a function that do the first run of the program that means basic syntax checks, that
     * all the glibal variables are well declaired and might be assigned, ignoring the
     * scopes - not deal with them, just collect method objects
     * @param recivedFile - the file recieved to check
     * @throws Exception - number 2 exception in case the file is unreadable or missing
     */
    public void FirstRun(File recivedFile) throws Exception {
        Parser firstParser = new Parser(recivedFile);
        while (firstParser.hasNext()) {
            int kindOfLine = firstParser.getNext();

            switch (kindOfLine) {
                case VARIABLE_ASSIGNMENT:
                    ArrayList<SVariable> newAssignedVar = SVariable.variableFactory(!DECLARATION,
                            firstParser.getSplitLine(firstParser.curLine),GlobalVariableArray);
                    replaceValue(newAssignedVar.get(0));
                    firstParser.nextType = -1;
                    break;

                case VARIABLE_DECLARATION:
                    ArrayList<SVariable> newBatch = SVariable.variableFactory(DECLARATION, firstParser
                            .getSplitLine(firstParser.curLine),GlobalVariableArray);
                    for (int j = 0; j < newBatch.size(); j++) {
                        checkIfVarTaken(newBatch.get(j));
                        GlobalVariableArray.add(newBatch.get(j));
                    }
                    firstParser.nextType = -1;
                    break;

                case METHOD_DECLARATION:
                    SMethod newMethod = new SMethod((firstParser.curSplitLine.get(0)));
                    ArrayList<SVariable> needToAddVarsAtDeclartion = SVariable.collectVarsForMethod
                            (firstParser.curSplitLine);
                    newMethod.localVariables.addAll(needToAddVarsAtDeclartion);
                    newMethod.myCallVariables.addAll(needToAddVarsAtDeclartion);
                    newMethod.allVariables.addAll(needToAddVarsAtDeclartion);
                    MethodsArray.add(newMethod);
                    firstParser.nextType = -1;

                    break;

                default:
                    break;
            }
        }
        for(int eachIndex = 0 ; eachIndex<MethodsArray.size();eachIndex++){
            MethodsArray.get(eachIndex).knownVariabels.addAll(GlobalVariableArray);
            MethodsArray.get(eachIndex).allVariables.addAll(GlobalVariableArray);
        }
    }

    /**
     * a function that replace a value as assigned
     * @param assign - the variable that is needed to assign at
     * @throws Exception
     */
    private void replaceValue(SVariable assign) throws SExceptionsTypeOne {
        for (int k=0; k<GlobalVariableArray.size();k++){
            SVariable toCompare = GlobalVariableArray.get(k);
            if(assign.name.equals(toCompare.name) && !toCompare.isFinal){
                SVariable.checkValidValueType(toCompare.type, assign.val,false);
                toCompare.val = assign.val;
                return;
            }
        }
        throw new InvalidAssignmentException();
    }

    /**
     * a function that replace a value inside a already daclared variable - this method work for scopes
     * @param assign - the variable that need to assign
     * @param currentMethod - the method or scope we are in
     */
    private void replaceValueLocally(SVariable assign,SMethod currentMethod) throws SExceptionsTypeOne {
        for (int k=0; k<currentMethod.localVariables.size();k++){
            SVariable toCompare = currentMethod.localVariables.get(k);
            if(assign.name.equals(toCompare.name) && !toCompare.isFinal){
                SVariable.checkValidValueType(toCompare.type, assign.val,false);
                toCompare.val = assign.val;
                return;
            }
        }
        for (int k=0; k<currentMethod.knownVariabels.size();k++){
            SVariable toCompare = currentMethod.knownVariabels.get(k);
            if(assign.name.equals(toCompare.name) && !toCompare.isFinal){
                SVariable.checkValidValueType(toCompare.type, assign.val,false);
                toCompare.val = assign.val;
                return;
            }
        }
        throw new InvalidAssignmentException();
    }


    /**
     * since some variables have different ways that may have the same name this method tells is
     * the name is already taken by another known variable
     * @param sVariableToCheck - the variable to check if its already taken
     */
    private void checkIfVarTaken(SVariable sVariableToCheck) throws SExceptionsTypeOne {
        for (int k=0; k<GlobalVariableArray.size();k++){
            SVariable toCompare = GlobalVariableArray.get(k);
            if((sVariableToCheck.name.equals(toCompare.name))) { // dec
                throw new InvalidNameAlreadyTaken();
            }
        }
    }

    /**
     * since some variables have different ways that may have the same name this method tells is
     * the name is already taken by another known variable but this method works inside a specific scope
     * @param sVariableToCheck -the variable that needed to be check if its name already taken
     * @param currentMethod - the method or scope that is specific called from
     */
    private void checkIfVarTakenLocaly(SVariable sVariableToCheck,SMethod currentMethod)
            throws SExceptionsTypeOne {
        for (int k=0; k<currentMethod.localVariables.size();k++){
            SVariable toCompare = currentMethod.localVariables.get(k);
            if((sVariableToCheck.name.equals(toCompare.name))){
                throw new InvalidNameAlreadyTaken();
            }
        }
    }


    /**
     * a function that for the parser do the second run , the second run deal only with
     * scopes with out everything that happens out side of them,
     * works for every scope collected in the previues run
     * @param recivedFile - the file that is needed to be readed from
     */
    public void secondRun(File recivedFile ) throws SExceptionsTypeOne , IOException {
        Parser secondParser = new Parser(recivedFile);
        int numberOfMethod = -1;
        while (secondParser.hasNextSecond()) {
            numberOfMethod+=1;
            SMethod currentMethod = MethodsArray.get(numberOfMethod);
            secondRunnerHelperForSingleScoop(secondParser,currentMethod,true);
        }
    }

    /**
     * helper that runs on a single scope for the second run - check each line inside a scoop
     * and react as needed for each case
     * @param secondParser - the parser that uses this method
     * @param currentMethod - the method or scope is called from - its very important to know for its
     *                      variables
     * @param isThisAMethod - a boolean that tells if its called from a method with a specific rules ir
     *                      from a different kind of scope
     */
    public void secondRunnerHelperForSingleScoop(Parser secondParser, SMethod currentMethod, boolean
            isThisAMethod)
            throws SExceptionsTypeOne {
        while (secondParser.readInsideMethod(isThisAMethod)){
            int kindOfLine = secondParser.nextType;
            switch (kindOfLine) {
                case VARIABLE_ASSIGNMENT:
                    ArrayList<SVariable> newAssignedVar = SVariable.variableFactory(!DECLARATION,
                            secondParser.getSplitLine(secondParser.curLine),currentMethod.allVariables);
                    replaceValueLocally(newAssignedVar.get(0),currentMethod);
                    secondParser.nextType = -1;
                    break;
                case VARIABLE_DECLARATION:
                    ArrayList<SVariable> newBatch = SVariable.variableFactory(DECLARATION, secondParser
                            .getSplitLine(secondParser.curLine),currentMethod.allVariables);
                    for (int j = 0; j < newBatch.size(); j++) {
                        checkIfVarTakenLocaly(newBatch.get(j),currentMethod);
                        currentMethod.localVariables.add(newBatch.get(j));
                        currentMethod.allVariables.add(newBatch.get(j));
                    }
                    secondParser.nextType = -1;
                    break;
                case METHOD_CALL:
                    ArrayList<String> typesArray = new ArrayList<>();
                    for(int i=1;i<secondParser.curSplitLine.size();i++) {
                        String varName = secondParser.curSplitLine.get(i);
                        String tempType=null;
                        for(SVariable varr : currentMethod.localVariables) {
                            if (varr.name.equals(varName)) {
                                tempType = varr.type;
                                break;
                            }
                        }
                        if(tempType==null){
                            for(SVariable varr : currentMethod.knownVariabels) {
                                if (varr.name.equals(varName)) {
                                    tempType = varr.type;
                                    break;
                                }
                            }
                        }
                        if (tempType == null) {
                            tempType = SVariable.checkType(varName);
                        }
                        typesArray.add(tempType);
                    }
                    SMethod.checkValidCall(MethodsArray, secondParser.curSplitLine.get(0), typesArray);
                    break;
                case CONDITION_DECLARATION:
                    for(int i=0;i<secondParser.curSplitLine.size();i++) {
                        String varName = secondParser.curSplitLine.get(i);
                        String tempType=null;
                        for(SVariable varr : currentMethod.localVariables) {
                            if (varr.name.equals(varName)) {
                                tempType = varr.type;
                                break;
                            }
                        }
                        if(tempType==null){
                            for(SVariable varr : currentMethod.knownVariabels) {
                                if (varr.name.equals(varName)) {
                                    tempType = varr.type;
                                    break;
                                }
                            }
                        }
                        if (tempType == null) {
                            tempType = SVariable.checkType(varName);
                        }
                        if(tempType.equals(SVariable.CHAR_TYPE) || tempType.equals(SVariable.STRING_TYPE)){
                            throw new InvalidConditionArguments();
                        }
                    }
                    SMethod conditionObject = new SMethod("");
                    conditionObject.knownVariabels.addAll(currentMethod.localVariables);
                    for(int g=0;g<currentMethod.knownVariabels.size();g++){
                        SVariable currTempVar = currentMethod.knownVariabels.get(g);
                        boolean foundInLocal = false;
                        for(int g2=0;g2<currentMethod.localVariables.size();g2++){
                            if(currTempVar.name.equals(currentMethod.localVariables.get(g2).name)){
                                foundInLocal = true;
                                break;
                            }
                        }if(!foundInLocal){
                            conditionObject.knownVariabels.add(currTempVar);
                        }
                    }secondRunnerHelperForSingleScoop(secondParser,conditionObject, false);
                default:
                    break;
            }
        }



    }
}



