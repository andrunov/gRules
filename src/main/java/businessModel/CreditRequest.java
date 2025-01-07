package businessModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CreditRequest {

    private Calendar applicDate;
    private String programCode;
    private double creditQty;
    private double duration;
    private double rate;
    private Borrower borrower;

    public CreditRequest(String programCode, int creditQty) {
        this.programCode = programCode;
        this.creditQty = creditQty;
    }

    public boolean isComplete() {
        return this.programCode != null && !this.programCode.isEmpty() && this.creditQty != 0;
    }

    public CreditRequest() {
    }

    public Calendar getApplicDate() {
        return applicDate;
    }

    public void setApplicDate(Calendar applicDate) {
        this.applicDate = applicDate;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
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

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = format.format(applicDate.getTime());
        final StringBuilder sb = new StringBuilder("CreditRequest{");
        sb.append("applicDate=").append(strDate);
        sb.append(", programCode='").append(programCode).append('\'');
        sb.append(", creditQty=").append(creditQty);
        sb.append(", duration=").append(duration);
        sb.append(", rate=").append(rate);
        sb.append(", borrower=").append(borrower);
        sb.append('}');
        return sb.toString();
    }
}
