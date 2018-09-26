package pl.Vorpack.app.HelperClassess;

import java.security.InvalidParameterException;

public class Filter {
    private Boolean check(String setsValue, String fieldValue){
        return setsValue.contains(fieldValue);
    }

    private Boolean check(String firstSetsValue, String secondSetsValue, String firstFieldsValue,
                                              String secondFieldsValue){
        return firstSetsValue.contains(firstFieldsValue) &&
                secondSetsValue.contains(secondFieldsValue);
    }

    private Boolean check(String firstSetsValue, String secondSetsValue, String thirdSetsValue,
                          String firstFieldsValue, String secondFieldsValue, String thirdFieldsValue){
        return firstSetsValue.contains(firstFieldsValue) && secondSetsValue.contains(secondFieldsValue) &&
                thirdSetsValue.contains(thirdFieldsValue);
    }

    private Boolean check(String firstSetsValue, String secondSetsValue, String thirdSetsValue, String fourthSetsValue,
                          String firstFieldsValue, String secondFieldsValue, String thirdFieldsValue, String fourthFieldsValue){
        return firstSetsValue.contains(firstFieldsValue) && secondSetsValue.contains(secondFieldsValue) &&
                thirdSetsValue.contains(thirdFieldsValue) && fourthSetsValue.contains(fourthFieldsValue);
    }

    public Boolean isDimensionInSet(String firstSetsValue, String secondSetsValue, String thirdSetsValue, String fourthSetsValue,
                                    String firstFieldsValue, String secondFieldsValue, String thirdFieldsValue, String fourthFieldsValue){
        if(secondFieldsValue.isEmpty() && thirdFieldsValue.isEmpty()
                && fourthFieldsValue.isEmpty()) {
            return check(firstSetsValue, firstFieldsValue);
        }
        else if(!secondFieldsValue.isEmpty() && thirdFieldsValue.isEmpty()
                && fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, secondSetsValue, firstFieldsValue, secondFieldsValue);
        }
        else if(secondFieldsValue.isEmpty() && !thirdFieldsValue.isEmpty()
                && fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, thirdSetsValue, firstFieldsValue, thirdFieldsValue);
        }
        else if(secondFieldsValue.isEmpty() && thirdFieldsValue.isEmpty()
                && !fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, fourthSetsValue, firstFieldsValue, fourthFieldsValue);
        }
        else if(!secondFieldsValue.isEmpty() && !thirdFieldsValue.isEmpty()
                && fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, secondSetsValue, thirdSetsValue,
                    firstFieldsValue, secondFieldsValue, thirdFieldsValue);
        }
        else if(!secondFieldsValue.isEmpty() && thirdFieldsValue.isEmpty()
                && !fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, secondSetsValue, fourthSetsValue,
                    firstFieldsValue, secondFieldsValue, fourthFieldsValue);
        }
        else if(secondFieldsValue.isEmpty() && !thirdFieldsValue.isEmpty()
                && !fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, thirdSetsValue, fourthSetsValue,
                    firstFieldsValue, thirdFieldsValue, fourthFieldsValue);
        }
        else if(!secondFieldsValue.isEmpty() && !thirdFieldsValue.isEmpty()
                && !fourthFieldsValue.isEmpty()){
            return check(firstSetsValue, secondSetsValue, thirdSetsValue, fourthSetsValue,
                    firstFieldsValue, secondFieldsValue, thirdFieldsValue, fourthFieldsValue);
        }
        throw new InvalidParameterException();
    }
}
