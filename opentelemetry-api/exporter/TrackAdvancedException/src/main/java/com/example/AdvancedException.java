package com.example;

public class AdvancedException extends Exception {

    private static final StackTraceElement[] STACK_TRACE_ELEMENTS;

    static {
        STACK_TRACE_ELEMENTS = new StackTraceElement[]{
            new StackTraceElement("declaringClass1", "methodName1", "filename1", 123),
            new StackTraceElement("declaringClass2", "methodName2", "filename2", 456),
            new StackTraceElement("declaringClass3", "methodName3", "filename3", 789)
        };
    }

    public AdvancedException(String name) {
        super(name);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return STACK_TRACE_ELEMENTS;
    }
}
