package pl.Vorpack.app.Dto;

public class DimensionDTO {
    private String firstDim = "";
    private String secondDim = "";
    private String thick = "";
    private String weight = "";

    public DimensionDTO(String firstDim, String secondDim, String thick, String weight) {
        this.firstDim = firstDim;
        this.secondDim = secondDim;
        this.thick = thick;
        this.weight = weight;
    }

    public String getFirstDim() {
        return firstDim;
    }

    public void setFirstDim(String firstDim) {
        this.firstDim = firstDim;
    }

    public String getSecondDim() {
        return secondDim;
    }

    public void setSecondDim(String secondDim) {
        this.secondDim = secondDim;
    }

    public String getThick() {
        return thick;
    }

    public void setThick(String thick) {
        this.thick = thick;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
