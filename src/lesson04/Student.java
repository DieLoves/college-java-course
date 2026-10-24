package lesson04;

public class Student {
    private long id;
    private String name;
    private String groupName;
    private double grade;

    public Student(String name, String groupName, double grade) {
        this(0, name, groupName, grade);
    }

    public Student(long id, String name, String groupName, double grade) {
        this.id = id;
        this.name = name;
        this.groupName = groupName;
        this.grade = grade;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return String.format("%d | %s | %s | %.2f", id, name, groupName, grade);
    }
}
