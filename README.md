# Java

Задания по урокам находятся в `src/lesson01`–`src/lesson04`.

Для запуска нужны JDK 17 или новее и Maven. В IntelliJ IDEA нужно открыть
`pom.xml` как Maven-проект, затем запускать нужный класс `TaskN`.

## Урок 4. JDBC

- `Task1` — сохранение, поиск, изменение и удаление студентов через `StudentDao`.
- `Task2` — сравнение вставки 1000 студентов обычным циклом и batch.
- `Task3` — запись на курс с транзакцией и откатом при отсутствии мест.
- `Task4` — средний балл группы через функцию H2 и `CallableStatement`.

```bash
mvn compile exec:java -Dexec.mainClass=lesson04.Task1
mvn compile exec:java -Dexec.mainClass=lesson04.Task2
mvn compile exec:java -Dexec.mainClass=lesson04.Task3
mvn compile exec:java -Dexec.mainClass=lesson04.Task4
```

Каждое задание создаёт H2 в памяти. После завершения программы данные исчезают.
В `Task2` оба способа используют одну транзакцию на 1000 студентов, одинаковый
набор данных и отдельные базы. Замер включает вставку и `commit()`, без создания
схемы и подключения. Результат зависит от прогрева JVM и нагрузки; один запуск
даёт приблизительное сравнение.

Проверки:

```bash
mvn test
```
