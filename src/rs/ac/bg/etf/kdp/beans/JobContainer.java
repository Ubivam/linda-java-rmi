package rs.ac.bg.etf.kdp.beans;

public class JobContainer {

    public enum State {
        READY,
        RUNNING,
        SCHEDULED,
        DONE,
        FAILED,
        ABORTED
    }

    private String className;
    private Object[] construct;
    private String methodName;
    private Object[] arguments;
    private State jobState;

    public JobContainer() {
    }

    public JobContainer(String className, Object[] construct, String methodName, Object[] arguments) {
        this.className = className;
        this.construct = construct;
        this.methodName = methodName;
        this.arguments = arguments;
        jobState = State.READY;
    }

    public State getJobState() {
        return jobState;
    }

    public void setJobState(State jobState) {
        this.jobState = jobState;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object[] getConstruct() {
        return construct;
    }

    public void setConstruct(Object[] construct) {
        this.construct = construct;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }
}
