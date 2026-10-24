package lesson04;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Task2 {
    private static final String INSERT_SQL =
            "INSERT INTO students (name, group_name, grade) VALUES (?, ?, ?)";

    public static void main(String[] args) throws SQLException {
        List<Student> students = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            students.add(new Student("Студент " + i, "ИС-24", 85));
        }

        DataSource normalDataSource = Database.createDataSource("task2_normal");
        DataSource batchDataSource = Database.createDataSource("task2_batch");
        Database.initialize(normalDataSource);
        Database.initialize(batchDataSource);

        long normalTime = insertNormally(normalDataSource, students);
        long batchTime = insertBatch(batchDataSource, students);

        System.out.printf("Обычный цикл: %d нс (%.3f мс)%n", normalTime, normalTime / 1_000_000.0);
        System.out.printf("Batch: %d нс (%.3f мс)%n", batchTime, batchTime / 1_000_000.0);
        System.out.printf("Отношение времени цикла к batch: %.2f%n", (double) normalTime / batchTime);
    }

    public static long insertNormally(DataSource dataSource, List<Student> students) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                long start = System.nanoTime();

                for (Student student : students) {
                    setStudentParameters(statement, student);
                    statement.executeUpdate();
                }

                connection.commit();
                return System.nanoTime() - start;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    public static long insertBatch(DataSource dataSource, List<Student> students) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
                long start = System.nanoTime();

                for (Student student : students) {
                    setStudentParameters(statement, student);
                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
                return System.nanoTime() - start;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }

    private static void setStudentParameters(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getName());
        statement.setString(2, student.getGroupName());
        statement.setDouble(3, student.getGrade());
    }
}
