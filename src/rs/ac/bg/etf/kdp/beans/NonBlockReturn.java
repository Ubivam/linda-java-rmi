package rs.ac.bg.etf.kdp.beans;

public class NonBlockReturn {
    private boolean passed;
    private String[] tuple;

    public NonBlockReturn (){
    }
    public NonBlockReturn(boolean passed, String[] tuple){
        this.passed = passed;
        this.tuple = tuple;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String[] getTuple() {
        return tuple;
    }

    public void setTuple(String[] tuple) {
        this.tuple = tuple;
    }
}
