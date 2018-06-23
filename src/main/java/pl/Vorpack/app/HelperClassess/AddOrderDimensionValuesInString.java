package pl.Vorpack.app.HelperClassess;

public class AddOrderDimensionValuesInString {
    public String objFirstDim = null;
    public String objSecondDim = null;
    public String objThickness = null;
    public String objWeight = null;

    public AddOrderDimensionValuesInString() {
    }

    public AddOrderDimensionValuesInString(String objFirstDim, String objSecondDim, String objThickness, String objWeight) {
        this.objFirstDim = objFirstDim;
        this.objSecondDim = objSecondDim;
        this.objThickness = objThickness;
        this.objWeight = objWeight;
    }

    private Boolean checkFirstDim(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue){
        return dimValuesFromSet.objFirstDim.contains(FieldValue);
    }

    private Boolean checkSecondDim(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue){
        return dimValuesFromSet.objSecondDim.contains(FieldValue);
    }

    private Boolean checkThickness(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue){
        return dimValuesFromSet.objThickness.contains(FieldValue);
    }

    private Boolean checkWeight(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue){
        return dimValuesFromSet.objWeight.contains(FieldValue);
    }

    private Boolean checkFirstDimAndSecondDim(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                              String FieldValue_2){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1) &&
                dimValuesFromSet.objSecondDim.contains(FieldValue_2);
    }

    private Boolean checkFirstDimAndThickness(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                              String FieldValue_2){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1)
                && dimValuesFromSet.objThickness.contains(FieldValue_2);
    }

    private Boolean checkFirstDimAndWeight(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                           String FieldValue_2){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1)
                && dimValuesFromSet.objWeight.contains(FieldValue_2);
    }

    private Boolean checkSecondDimAndThickness(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                               String FieldValue_2){
        return dimValuesFromSet.objSecondDim.contains(FieldValue_1)
                && dimValuesFromSet.objThickness.contains(FieldValue_2);
    }

    private Boolean checkSecondDimAndWeight(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                            String FieldValue_2){
        return dimValuesFromSet.objSecondDim.contains(FieldValue_1) && dimValuesFromSet.objWeight.contains(FieldValue_2);
    }

    private Boolean checkThicknessAndWeight(AddOrderDimensionValuesInString dimValuesFromSet, String FieldValue_1,
                                            String FieldValue_2){
        return dimValuesFromSet.objThickness.contains(FieldValue_1) && dimValuesFromSet.objWeight.contains(FieldValue_2);
    }

    private Boolean checkFirstDimAndSecondDimAndThickness(AddOrderDimensionValuesInString dimValuesFromSet,
                                                          String FieldValue_1, String FieldValue_2, String FieldValue_3){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1) && dimValuesFromSet.objSecondDim.contains(FieldValue_2)
                && dimValuesFromSet.objThickness.contains(FieldValue_3);
    }

    private Boolean checkFirstDimAndSecondDimAndWeight(AddOrderDimensionValuesInString dimValuesFromSet,
                                                       String FieldValue_1, String FieldValue_2, String FieldValue_3){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1) && dimValuesFromSet.objSecondDim.contains(FieldValue_2)
                && dimValuesFromSet.objWeight.contains(FieldValue_3);
    }

    private Boolean checkFirstDimAndThicknessAndWeight(AddOrderDimensionValuesInString dimValuesFromSet,
                                                       String FieldValue_1, String FieldValue_2, String FieldValue_3){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1) && dimValuesFromSet.objThickness.contains(FieldValue_2)
                && dimValuesFromSet.objWeight.contains(FieldValue_3);
    }

    private Boolean checkSecondDimAndThicknessAndWeight(AddOrderDimensionValuesInString dimValuesFromSet,
                                                        String FieldValue_1, String FieldValue_2, String FieldValue_3){
        return dimValuesFromSet.objSecondDim.contains(FieldValue_1) && dimValuesFromSet.objThickness.contains(FieldValue_2)
                && dimValuesFromSet.objWeight.contains(FieldValue_3);
    }

    private Boolean checkFirstDimAndSecondDimAndThicknessAndWeight(AddOrderDimensionValuesInString dimValuesFromSet,
                                                                   String FieldValue_1, String FieldValue_2,
                                                                  String FieldValue_3, String FieldValue_4){
        return dimValuesFromSet.objFirstDim.contains(FieldValue_1) && dimValuesFromSet.objSecondDim.contains(FieldValue_2) &&
                dimValuesFromSet.objThickness.contains(FieldValue_3) && dimValuesFromSet.objWeight.contains(FieldValue_4);
    }

    public Boolean checkIfDimensionIsInSetToFirstDim(AddOrderDimensionValuesInString dimValuesFromFields,
                                                     AddOrderDimensionValuesInString dimValuesFromSet){
        Boolean result = false;
        if(dimValuesFromFields.objSecondDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()) {
            result = dimValuesFromSet.checkFirstDim(dimValuesFromSet, dimValuesFromFields.objFirstDim);
        }
        else if(!dimValuesFromFields.objSecondDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDim(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                        dimValuesFromFields.objSecondDim);
        }
        else if(dimValuesFromFields.objSecondDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndThickness(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                        dimValuesFromFields.objThickness);
        }
        else if(dimValuesFromFields.objSecondDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkFirstDimAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                        dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objSecondDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkFirstDimAndSecondDimAndThickness(dimValuesFromSet,
                    dimValuesFromFields.objFirstDim, dimValuesFromFields.objSecondDim, dimValuesFromFields.objThickness);

        }
        else if(!dimValuesFromFields.objSecondDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objSecondDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objSecondDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        return result;
    }

    public Boolean checkIfDimensionIsInSetToSecondDim(AddOrderDimensionValuesInString dimValuesFromFields,
                                                     AddOrderDimensionValuesInString dimValuesFromSet){
        Boolean result = false;
        if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()) {
            result = dimValuesFromSet.checkSecondDim(dimValuesFromSet, dimValuesFromFields.objSecondDim);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDim(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndThickness(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objThickness);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkSecondDimAndWeight(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkFirstDimAndSecondDimAndThickness(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objThickness);

        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objThickness.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight, dimValuesFromFields.objThickness);
        }
        return result;
    }

    public Boolean checkIfDimensionIsInSetToThickness(AddOrderDimensionValuesInString dimValuesFromFields,
                                                      AddOrderDimensionValuesInString dimValuesFromSet){
        Boolean result = false;
        if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()) {
            result = dimValuesFromSet.checkThickness(dimValuesFromSet, dimValuesFromFields.objThickness);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndThickness(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objThickness);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndThickness(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objThickness);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objThickness,
                    dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromFields.checkFirstDimAndSecondDimAndThickness(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objThickness);

        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objWeight.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight, dimValuesFromFields.objThickness);
        }
        return result;
    }

    public Boolean checkIfDimensionIsInSetToWeight(AddOrderDimensionValuesInString dimValuesFromFields,
                                                      AddOrderDimensionValuesInString dimValuesFromSet){
        Boolean result = false;
        if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objThickness.isEmpty()) {
            result = dimValuesFromSet.checkWeight(dimValuesFromSet, dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndWeight(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromFields.checkThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objThickness,
                    dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromFields.checkFirstDimAndSecondDimAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight);

        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromSet.checkSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objSecondDim,
                    dimValuesFromFields.objThickness, dimValuesFromFields.objWeight);
        }
        else if(!dimValuesFromFields.objFirstDim.isEmpty() && !dimValuesFromFields.objSecondDim.isEmpty()
                && !dimValuesFromFields.objThickness.isEmpty()){
            result = dimValuesFromSet.checkFirstDimAndSecondDimAndThicknessAndWeight(dimValuesFromSet, dimValuesFromFields.objFirstDim,
                    dimValuesFromFields.objSecondDim, dimValuesFromFields.objWeight, dimValuesFromFields.objThickness);
        }
        return result;
    }
}
