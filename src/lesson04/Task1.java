package lesson04;

import javax.sql.DataSource;
import java.sql.SQLException;

public class Task1 {
    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.createDataSource("task1");
        Database.initialize(dataSource);
        StudentDao studentDao = new StudentDao(dataSource);

        Student student = new Student("Алия", "ИС-24", 85);
        studentDao.save(student);
        studentDao.save(new Student("Марат", "ИС-24", 90));

        System.out.println("Студент по id: " + studentDao.findById(student.getId()));

        student.setGrade(95);
        studentDao.update(student);
        System.out.println("После обновления: " + studentDao.findById(student.getId()));

        System.out.println("Все студенты:");
        studentDao.findAll().forEach(System.out::println);

        studentDao.delete(student.getId());
        System.out.println("После удаления:");
        studentDao.findAll().forEach(System.out::println);
    }
}
