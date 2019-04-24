package com.nigel.wenreader.model.local;

public class FileBean {
    private String fileName;
    private String filePath;
    private String fileSize;
    private String fileDate;
    private int subCount;
    private boolean isAdded;
    private boolean isFolder;
    private boolean isChecked;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public boolean isAdded() {
        return isAdded;
    }

    public void setAdded(boolean added) {
        isAdded = added;
        if (isAdded){
            isFolder=false;
            isChecked=false;
        }
    }

    public boolean isFolder() {
        return isFolder;
    }

    public void setFolder(boolean folder) {
        isFolder = folder;
        if(isFolder){
            isAdded=false;
            isChecked=false;
        }
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if(isChecked){
            isAdded=false;
            isFolder=false;
        }
    }
}
