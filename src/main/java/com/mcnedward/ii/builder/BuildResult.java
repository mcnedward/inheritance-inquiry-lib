package com.mcnedward.ii.builder;

import com.mcnedward.ii.element.JavaSolution;

/**
 * Created by Edward on 9/23/2016.
 */
public class BuildResult {

    private JavaSolution mJavaSolution;
    private Exception mException;

    public boolean buildComplete() {
        return mException == null;
    }

    public JavaSolution getJavaSolution() {
        return mJavaSolution;
    }

    public void setJavaSolution(JavaSolution javaSolution) {
        this.mJavaSolution = javaSolution;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        this.mException = exception;
    }
}
