package businessModel;

public class CreditProgram {
    private String name;
    private String code;
    private double rate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CreditProgram{");
        sb.append("name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", rate=").append(rate);
        sb.append('}');
        return sb.toString();
    }
}
