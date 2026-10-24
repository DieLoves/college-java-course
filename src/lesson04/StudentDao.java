package lesson04;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class StudentDao {
    private final DataSource dataSource;

    public StudentDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Student student) throws SQLException {
        String sql = "INSERT INTO students (name, group_name, grade) VALUES (?, ?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setStudentParameters(statement, student);
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    student.setId(keys.getLong(1));
                }
            }
        }
    }

    public Student findById(long id) throws SQLException {
        String sql = "SELECT id, name, group_name, grade FROM students WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? readStudent(result) : null;
            }
        }
    }

    public List<Student> findAll() throws SQLException {
        String sql = "SELECT id, name, group_name, grade FROM students ORDER BY id";
        List<Student> students = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                students.add(readStudent(result));
            }
        }

        return students;
    }

    public boolean update(Student student) throws SQLException {
        String sql = "UPDATE students SET name = ?, group_name = ?, grade = ? WHERE id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setStudentParameters(statement, student);
            statement.setLong(4, student.getId());
            return statement.executeUpdate() == 1;
        }
    }

    public boolean delete(long id) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM students WHERE id = ?")) {
            statement.setLong(1, id);
            return statement.executeUpdate() == 1;
        }
    }

    private void setStudentParameters(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getName());
        statement.setString(2, student.getGroupName());
        statement.setDouble(3, student.getGrade());
    }

    private Student readStudent(ResultSet result) throws SQLException {
        return new Student(
                result.getLong("id"),
                result.getString("name"),
                result.getString("group_name"),
                result.getDouble("grade")
        );
    }
}
