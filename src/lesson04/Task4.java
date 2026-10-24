package lesson04;

import javax.sql.DataSource;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class Task4 {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.createDataSource("task4");
        Database.initialize(dataSource);
        StudentDao studentDao = new StudentDao(dataSource);
        studentDao.save(new Student("Алия", "ИС-24", 80));
        studentDao.save(new Student("Марат", "ИС-24", 90));
        studentDao.save(new Student("Дана", "ИС-24", 100));
        studentDao.save(new Student("Тимур", "ИС-25", 75));

        createFunction(dataSource);

        String groupName = "ИС-24";
        Double average = findGroupAverage(dataSource, groupName);
        System.out.printf("Средний балл группы %s: %.2f%n", groupName, average);
    }

    public static void createFunction(DataSource dataSource) throws SQLException {
        String sql = "CREATE ALIAS IF NOT EXISTS GROUP_AVERAGE FOR \"lesson04.Task4.averageGrade\"";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public static Double findGroupAverage(DataSource dataSource, String groupName) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             CallableStatement statement = connection.prepareCall("{? = call GROUP_AVERAGE(?)}")) {
            statement.registerOutParameter(1, Types.DOUBLE);
            statement.setString(2, groupName);
            statement.execute();

            double average = statement.getDouble(1);
            return statement.wasNull() ? null : average;
        }
    }

    public static Double averageGrade(Connection connection, String groupName) throws SQLException {
        String sql = "SELECT AVG(grade) FROM students WHERE group_name = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, groupName);

            try (ResultSet result = statement.executeQuery()) {
                result.next();
                double average = result.getDouble(1);
                return result.wasNull() ? null : average;
            }
        }
    }
}
