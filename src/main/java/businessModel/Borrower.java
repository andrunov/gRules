package businessModel;

public class Borrower {
    private int age;
    private boolean salaryClient;

    private BorrowerType borrowerType;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isSalaryClient() {
        return salaryClient;
    }

    public void setSalaryClient(boolean salaryClient) {
        this.salaryClient = salaryClient;
    }

    public BorrowerType getBorrowerType() {
        return borrowerType;
    }

    public void setBorrowerType(BorrowerType borrowerType) {
        this.borrowerType = borrowerType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Borrower{");
        sb.append("age=").append(age);
        sb.append(", salaryClient=").append(salaryClient);
        sb.append(", borrowerType=").append(borrowerType);
        sb.append('}');
        return sb.toString();
    }
}
