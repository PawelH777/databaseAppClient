package pl.Vorpack.app.Data;

public enum TraysStatus {
    WAITING("Do zrobienia"),
    IN_PROGRESS("W trakcie"),
    FINISHED("Ukończono"),
    ABANDONED("Porzucono");

    private final String polishText;
    TraysStatus(String polishText){ this.polishText = polishText; }
    public String getValue(){ return polishText; }

    public TraysStatus next() throws IllegalAccessException {
        switch(this){
            case WAITING: return TraysStatus.IN_PROGRESS;
            case IN_PROGRESS: return TraysStatus.FINISHED;
            case FINISHED: return TraysStatus.ABANDONED;
            case ABANDONED: return TraysStatus.WAITING;
            default: throw new IllegalAccessException("This should never happen :O");
        }
    }
}
