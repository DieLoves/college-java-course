package lesson04;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EnrollmentService {
    private final DataSource dataSource;

    public EnrollmentService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean enroll(long studentId, long courseId) throws SQLException {
        String updateSql = "UPDATE courses SET free_seats = free_seats - 1 WHERE id = ? AND free_seats > 0";
        String insertSql = "INSERT INTO enrollments (student_id, course_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection()) {
            boolean autoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement update = connection.prepareStatement(updateSql);
                 PreparedStatement insert = connection.prepareStatement(insertSql)) {
                update.setLong(1, courseId);

                if (update.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                insert.setLong(1, studentId);
                insert.setLong(2, courseId);
                insert.executeUpdate();

                connection.commit();
                return true;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(autoCommit);
            }
        }
    }
}
