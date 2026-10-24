package lesson04;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class Lesson04Test {
    private DataSource dataSource;
    private StudentDao studentDao;

    @BeforeEach
    void setUp() throws SQLException {
        dataSource = Database.createDataSource(UUID.randomUUID().toString());
        Database.initialize(dataSource);
        studentDao = new StudentDao(dataSource);
    }

    @Test
    void savesAndFindsStudentWithApostrophes() throws SQLException {
        Student student = new Student("О'Коннор", "ИС'24", 87.5);
        studentDao.save(student);

        assertTrue(student.getId() > 0);
        Student found = studentDao.findById(student.getId());
        assertNotNull(found);
        assertEquals("О'Коннор", found.getName());
        assertEquals("ИС'24", found.getGroupName());
        assertEquals(87.5, found.getGrade());
    }

    @Test
    void findsAllStudentsAndHandlesMissingId() throws SQLException {
        assertTrue(studentDao.findAll().isEmpty());
        assertNull(studentDao.findById(-1));
        studentDao.save(new Student("Алия", "ИС-24", 90));
        studentDao.save(new Student("Марат", "ИС-25", 75));

        List<Student> students = studentDao.findAll();
        assertEquals(2, students.size());
        assertEquals("Алия", students.get(0).getName());
        assertEquals("Марат", students.get(1).getName());
    }

    @Test
    void updatesAndDeletesOnlySelectedStudent() throws SQLException {
        Student first = new Student("Алия", "ИС-24", 90);
        Student second = new Student("Марат", "ИС-25", 75);
        studentDao.save(first);
        studentDao.save(second);
        first.setName("Айгуль");
        first.setGroupName("ИС-26");
        first.setGrade(95);

        assertTrue(studentDao.update(first));
        Student updated = studentDao.findById(first.getId());
        assertEquals("Айгуль", updated.getName());
        assertEquals("ИС-26", updated.getGroupName());
        assertEquals(95, updated.getGrade());
        assertEquals("Марат", studentDao.findById(second.getId()).getName());
        assertTrue(studentDao.delete(first.getId()));
        assertNull(studentDao.findById(first.getId()));
        assertEquals(1, studentDao.findAll().size());
        assertFalse(studentDao.delete(first.getId()));
        assertFalse(studentDao.update(first));
    }

    @Test
    void insertsThousandStudentsNormally() throws SQLException {
        assertTrue(Task2.insertNormally(dataSource, createStudents()) > 0);
        assertInsertedStudents();
    }

    @Test
    void insertsThousandStudentsInBatch() throws SQLException {
        assertTrue(Task2.insertBatch(dataSource, createStudents()) > 0);
        assertInsertedStudents();
    }

    @Test
    void rollsBackNormalInsertOnInvalidGrade() throws SQLException {
        List<Student> students = List.of(
                new Student("Алия", "ИС-24", 90),
                new Student("Марат", "ИС-24", 101)
        );

        assertThrows(SQLException.class, () -> Task2.insertNormally(dataSource, students));
        assertEquals(0, queryInt("SELECT COUNT(*) FROM students"));
    }

    @Test
    void rollsBackBatchOnInvalidGrade() throws SQLException {
        List<Student> students = List.of(
                new Student("Алия", "ИС-24", 90),
                new Student("Марат", "ИС-24", 101)
        );

        assertThrows(SQLException.class, () -> Task2.insertBatch(dataSource, students));
        assertEquals(0, queryInt("SELECT COUNT(*) FROM students"));
    }

    @Test
    void enrollsStudentAndDecreasesFreeSeats() throws SQLException {
        Student student = saveStudent();
        addCourse(1, 2);

        assertTrue(new EnrollmentService(dataSource).enroll(student.getId(), 1));
        assertEquals(1, queryInt("SELECT free_seats FROM courses WHERE id = 1"));
        assertEquals(1, queryInt("SELECT COUNT(*) FROM enrollments"));
    }

    @Test
    void doesNotEnrollWhenCourseIsFull() throws SQLException {
        Student student = saveStudent();
        addCourse(1, 0);

        assertFalse(new EnrollmentService(dataSource).enroll(student.getId(), 1));
        assertEquals(0, queryInt("SELECT free_seats FROM courses WHERE id = 1"));
        assertEquals(0, queryInt("SELECT COUNT(*) FROM enrollments"));
    }

    @Test
    void rollsBackSeatWhenStudentDoesNotExist() throws SQLException {
        addCourse(1, 2);

        assertThrows(SQLException.class, () -> new EnrollmentService(dataSource).enroll(-1, 1));
        assertEquals(2, queryInt("SELECT free_seats FROM courses WHERE id = 1"));
        assertEquals(0, queryInt("SELECT COUNT(*) FROM enrollments"));
    }

    @Test
    void rollsBackSeatOnDuplicateEnrollment() throws SQLException {
        Student student = saveStudent();
        addCourse(1, 2);
        EnrollmentService service = new EnrollmentService(dataSource);
        assertTrue(service.enroll(student.getId(), 1));

        assertThrows(SQLException.class, () -> service.enroll(student.getId(), 1));
        assertEquals(1, queryInt("SELECT free_seats FROM courses WHERE id = 1"));
        assertEquals(1, queryInt("SELECT COUNT(*) FROM enrollments"));
    }

    @Test
    void doesNotEnrollOnMissingCourse() throws SQLException {
        Student student = saveStudent();

        assertFalse(new EnrollmentService(dataSource).enroll(student.getId(), -1));
        assertEquals(0, queryInt("SELECT COUNT(*) FROM enrollments"));
    }

    @Test
    void onlyOneStudentCanTakeLastSeat() throws Exception {
        Student first = saveStudent();
        Student second = saveStudent();
        addCourse(1, 1);
        EnrollmentService service = new EnrollmentService(dataSource);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Future<Boolean> firstResult = executor.submit(() -> {
                start.await();
                return service.enroll(first.getId(), 1);
            });
            Future<Boolean> secondResult = executor.submit(() -> {
                start.await();
                return service.enroll(second.getId(), 1);
            });
            start.countDown();

            assertNotEquals(firstResult.get(10, TimeUnit.SECONDS), secondResult.get(10, TimeUnit.SECONDS));
            assertEquals(0, queryInt("SELECT free_seats FROM courses WHERE id = 1"));
            assertEquals(1, queryInt("SELECT COUNT(*) FROM enrollments"));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    void callsAverageFunctionForSelectedGroup() throws SQLException {
        studentDao.save(new Student("Алия", "ИС'24", 80));
        studentDao.save(new Student("Марат", "ИС'24", 95));
        studentDao.save(new Student("Дана", "ИС-25", 10));
        Task4.createFunction(dataSource);

        assertEquals(87.5, Task4.findGroupAverage(dataSource, "ИС'24"), 0.0001);
        assertEquals(10, Task4.findGroupAverage(dataSource, "ИС-25"), 0.0001);
    }

    @Test
    void returnsNullForEmptyGroup() throws SQLException {
        Task4.createFunction(dataSource);

        assertNull(Task4.findGroupAverage(dataSource, "Пустая группа"));
        studentDao.save(new Student("Алия", "ИС-24", 0));
        assertEquals(0, Task4.findGroupAverage(dataSource, "ИС-24"), 0.0001);
    }

    private Student saveStudent() throws SQLException {
        Student student = new Student("Алия", "ИС-24", 90);
        studentDao.save(student);
        return student;
    }

    private List<Student> createStudents() {
        List<Student> students = new ArrayList<>();

        for (int i = 1; i <= 1000; i++) {
            students.add(new Student("Студент " + i, "ИС-24", 85));
        }

        return students;
    }

    private void assertInsertedStudents() throws SQLException {
        List<Student> students = studentDao.findAll();
        assertEquals(1000, students.size());
        assertEquals("Студент 1", students.get(0).getName());
        assertEquals("Студент 1000", students.get(999).getName());
        assertEquals("ИС-24", students.get(999).getGroupName());
        assertEquals(85, students.get(999).getGrade());
    }

    private void addCourse(long id, int freeSeats) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO courses (id, name, free_seats) VALUES (?, ?, ?)")) {
            statement.setLong(1, id);
            statement.setString(2, "Java");
            statement.setInt(3, freeSeats);
            statement.executeUpdate();
        }
    }

    private int queryInt(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        }
    }
}
