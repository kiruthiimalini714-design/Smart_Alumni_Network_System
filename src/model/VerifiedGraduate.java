package model;

public class VerifiedGraduate {
    private int gradId;
    private String registerNo;
    private String fullName;
    private int deptId;
    private String deptName;
    private int batchYear;
    private String degree;
    private boolean isRegistered;

    public VerifiedGraduate() {}

    public VerifiedGraduate(int gradId, String registerNo, String fullName, int deptId, String deptName, int batchYear, String degree, boolean isRegistered) {
        this.gradId = gradId;
        this.registerNo = registerNo;
        this.fullName = fullName;
        this.deptId = deptId;
        this.deptName = deptName;
        this.batchYear = batchYear;
        this.degree = degree;
        this.isRegistered = isRegistered;
    }

    public int getGradId() { return gradId; }
    public void setGradId(int gradId) { this.gradId = gradId; }

    public String getRegisterNo() { return registerNo; }
    public void setRegisterNo(String registerNo) { this.registerNo = registerNo; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public int getBatchYear() { return batchYear; }
    public void setBatchYear(int batchYear) { this.batchYear = batchYear; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public boolean isRegistered() { return isRegistered; }
    public void setRegistered(boolean registered) { isRegistered = registered; }
}
