package model;

import java.math.BigDecimal;

public class Assessment {
    private Integer id;
    private Integer enrollmentId;
    private double totalUnits;
    private BigDecimal tuitionFee;
    private BigDecimal miscFee;
    private BigDecimal labFee;
    private BigDecimal otherFee;
    private BigDecimal totalDue;
    private String currency;
    private String status;
    private String createdAt;

    public Assessment() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Integer enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public double getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(double totalUnits) {
        this.totalUnits = totalUnits;
    }

    public BigDecimal getTuitionFee() {
        return tuitionFee;
    }

    public void setTuitionFee(BigDecimal tuitionFee) {
        this.tuitionFee = tuitionFee;
    }

    public BigDecimal getMiscFee() {
        return miscFee;
    }

    public void setMiscFee(BigDecimal miscFee) {
        this.miscFee = miscFee;
    }

    public BigDecimal getLabFee() {
        return labFee;
    }

    public void setLabFee(BigDecimal labFee) {
        this.labFee = labFee;
    }

    public BigDecimal getOtherFee() {
        return otherFee;
    }

    public void setOtherFee(BigDecimal otherFee) {
        this.otherFee = otherFee;
    }

    public BigDecimal getTotalDue() {
        return totalDue;
    }

    public void setTotalDue(BigDecimal totalDue) {
        this.totalDue = totalDue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
