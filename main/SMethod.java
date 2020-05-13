package oop.ex6.main;

import oop.ex6.Sexcepsion.CalledToUnfamilierMethod;
import oop.ex6.Sexcepsion.MethodCallWithInvalidParameters;

import java.util.ArrayList;

/**
 * a class that represent a method class and also might be used as scoop object like a loop of a condition
 * but first of all it works as a class of methods than a condition for example is a private case
 * of a method
 */
public class SMethod {

    ArrayList<SVariable> allVariables;
    ArrayList<SVariable> myCallVariables;
    String nameMethod;
    ArrayList<SVariable> knownVariabels;
    ArrayList<SVariable> localVariables;

    /**
     * constructor
     * @param nameMethod - the name of the method
     */
    public SMethod(String nameMethod){
        this.nameMethod = nameMethod;
        this.knownVariabels = new ArrayList<>();
        this.myCallVariables = new ArrayList<>();
        this.localVariables = new ArrayList<>();
        this.allVariables = new ArrayList<>();
    }

    /**
     * help function for ourselfs to check the method object
     * @return a method represented as a string
     */
    @Override
    public String toString() {
        return this.nameMethod +" this VARIABLES " + this.knownVariabels;
    }

    /**
     * a check function for the method object that checks if the given variable is known
     * @param varThatIWantToUse - the variable that want to check if the method knows
     * @return true if the method know this variable
     */
    public boolean doIKnowThisVar(SVariable varThatIWantToUse){
        String relName = varThatIWantToUse.name;
        for(int eachVarIndex = 0; eachVarIndex<knownVariabels.size(); eachVarIndex++){
            if(knownVariabels.get(eachVarIndex).name.equals(varThatIWantToUse.name)){
                return true;
            }
        }
        return false;
    }

    /**
     * @param methodsArray - the array of the collected methods
     * @param nameMethod - the method that is called
     * @param typesArray - the array that holds all the types of the vars
     *                  that the method called with
     * @return - boolean that is never used but good for ourself to check
     * @throws MethodCallWithInvalidParameters
     * @throws CalledToUnfamilierMethod
     */
    public static boolean checkValidCall(ArrayList<SMethod> methodsArray,String nameMethod,
                                         ArrayList<String> typesArray)throws
            MethodCallWithInvalidParameters, CalledToUnfamilierMethod {

        for(int h=0; h<methodsArray.size();h++){
            SMethod cuurToCheck = methodsArray.get(h);
            if(nameMethod.equals(cuurToCheck.nameMethod)){
                for(int p=0;p<cuurToCheck.myCallVariables.size();p++){
                    if(!cuurToCheck.myCallVariables.get(p).type.equals(typesArray.get(p))){
                        throw new MethodCallWithInvalidParameters();
                    }
                }
                return true;

            }
        }
        throw new CalledToUnfamilierMethod();
    }
}
