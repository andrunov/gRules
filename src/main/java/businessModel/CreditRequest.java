package businessModel;

public class CreditRequest {

    private String programCode;

    private int creditQty;

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

    public int getCreditQty() {
        return creditQty;
    }

    public void setCreditQty(int creditQty) {
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
}
