package rs.ac.bg.etf.kdp.beans;

public class JobContainer {
    private String className;
    private Object[] construct;
    private String methodName;
    private Object[] arguments;

    public JobContainer() {
    }

    public JobContainer(String className, Object[] construct, String methodName, Object[] arguments) {
        this.className = className;
        this.construct = construct;
        this.methodName = methodName;
        this.arguments = arguments;
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
