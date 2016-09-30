package com.mcnedward.ii.service.metric.element;

import com.mcnedward.ii.element.JavaSolution;

import java.io.File;

/**
 * Created by Edward on 9/26/2016.
 */
public class MetricOptions {

    private JavaSolution mSolution;
    private File mDirectory;
    private boolean mExportDit, mExportNoc, mExportWmc, mExportFull, mUseCsvFormat;

    public MetricOptions() {
    }

    public MetricOptions(JavaSolution solution, File directory, boolean exportDit, boolean exportNoc, boolean exportWmc, boolean exportFull) {
        mSolution = solution;
        mDirectory = directory;
        mExportDit = exportDit;
        mExportNoc = exportNoc;
        mExportWmc = exportWmc;
        mExportFull = exportFull;
    }

    public JavaSolution getSolution() {
        return mSolution;
    }

    public void setSolution(JavaSolution solution) {
        mSolution = solution;
    }

    public File getDirectory() {
        return mDirectory;
    }

    public void setmDirectory(File directory) {
        mDirectory = directory;
    }

    public boolean exportDit() {
        return mExportDit;
    }

    public void setExportDit(boolean exportDit) {
        this.mExportDit = exportDit;
    }

    public boolean exportNoc() {
        return mExportNoc;
    }

    public void setExportNoc(boolean exportNoc) {
        this.mExportNoc = exportNoc;
    }

    public boolean exportWmc() {
        return mExportWmc;
    }

    public void setExportWmc(boolean exportWmc) {
        this.mExportWmc = exportWmc;
    }

    public boolean exportFull() {
        return mExportFull;
    }

    public void setExportFull(boolean exportFull) {
        this.mExportFull = exportFull;
    }

    public boolean useCsvFormt() {
        return mUseCsvFormat;
    }

    public void setUseCsvFormat(boolean useCsvFormat) {
        mUseCsvFormat = useCsvFormat;
    }
}
