package businessModel;

public class CreditRequest {

    private String programCode;

    private double creditQty;

    private boolean salaryClient;

    private double rate;

    public CreditRequest(String programCode, int creditQty, boolean salaryClient) {
        this.programCode = programCode;
        this.creditQty = creditQty;
        this.salaryClient = salaryClient;
    }

    public String getProgramCode() {
        return programCode;
    }

    public void setProgramCode(String programCode) {
        this.programCode = programCode;
    }

    public double getCreditQty() {
        return creditQty;
    }

    public void setCreditQty(double creditQty) {
        this.creditQty = creditQty;
    }

    public boolean isSalaryClient() {
        return salaryClient;
    }

    public void setSalaryClient(boolean salaryClient) {
        this.salaryClient = salaryClient;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CreditRequest{");
        sb.append("programCode='").append(programCode).append('\'');
        sb.append(", creditQty=").append(creditQty);
        sb.append(", salaryClient=").append(salaryClient);
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }
}
