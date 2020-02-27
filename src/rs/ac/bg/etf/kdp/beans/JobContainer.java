package rs.ac.bg.etf.kdp.beans;

import java.util.Date;
import java.util.UUID;

public class JobContainer {

    public enum State {
        READY,
        RUNNING,
        SCHEDULED,
        DONE,
        FAILED,
        ABORTED
    }

    private final Date startTime;
    private Date endTime;
    private UUID managerId;
    private String className;
    private Object[] construct;
    private String methodName;
    private Object[] arguments;
    private State jobState;
    private byte[] jobJar;
    private byte[] libJar;


    public JobContainer(UUID managerId ,String className, Object[] construct, String methodName, Object[] arguments, byte[] jobJar, byte[] libJar) {
        this.managerId = managerId;
        this.className = className;
        this.construct = construct;
        this.methodName = methodName;
        this.arguments = arguments;
        this.jobJar = jobJar;
        this.libJar = libJar;
        jobState = State.READY;
        startTime = new Date(System.currentTimeMillis());
        endTime = null;
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

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public byte[] getJobJar() {
        return jobJar;
    }

    public void setJobJar(byte[] jobJar) {
        this.jobJar = jobJar;
    }

    public byte[] getLibJar() {
        return libJar;
    }

    public void setLibJar(byte[] libJar) {
        this.libJar = libJar;
    }

    public UUID getManagerId() {
        return managerId;
    }

    public void setManagerId(UUID managerId) {
        this.managerId = managerId;
    }
}
