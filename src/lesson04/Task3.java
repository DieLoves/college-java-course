package lesson04;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Task3 {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.createDataSource("task3");
        Database.initialize(dataSource);
        StudentDao studentDao = new StudentDao(dataSource);
        Student first = new Student("Алия", "ИС-24", 85);
        Student second = new Student("Марат", "ИС-24", 90);
        studentDao.save(first);
        studentDao.save(second);

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO courses (id, name, free_seats) VALUES (?, ?, ?)")) {
            statement.setLong(1, 1);
            statement.setString(2, "Java");
            statement.setInt(3, 1);
            statement.executeUpdate();
        }

        EnrollmentService service = new EnrollmentService(dataSource);
        printEnrollment(first, service.enroll(first.getId(), 1));
        printEnrollment(second, service.enroll(second.getId(), 1));

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT free_seats FROM courses WHERE id = ?")) {
            statement.setLong(1, 1);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    System.out.println("Свободных мест: " + result.getInt("free_seats"));
                }
            }
        }
    }

    private static void printEnrollment(Student student, boolean enrolled) {
        if (enrolled) {
            System.out.println(student.getName() + ": запись на курс выполнена");
        } else {
            System.out.println(student.getName() + ": свободных мест нет, транзакция отменена");
        }
    }
}
