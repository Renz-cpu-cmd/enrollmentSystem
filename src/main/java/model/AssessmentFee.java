package model;

import java.math.BigDecimal;

public class AssessmentFee {
    private Integer id;
    private Integer assessmentId;
    private String feeCode;
    private String description;
    private BigDecimal amount;
    private int sortOrder;

    public AssessmentFee() {}

    public AssessmentFee(Integer assessmentId, String feeCode, String description, BigDecimal amount, int sortOrder) {
        this.assessmentId = assessmentId;
        this.feeCode = feeCode;
        this.description = description;
        this.amount = amount;
        this.sortOrder = sortOrder;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Integer assessmentId) { this.assessmentId = assessmentId; }

    public String getFeeCode() { return feeCode; }
    public void setFeeCode(String feeCode) { this.feeCode = feeCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}
