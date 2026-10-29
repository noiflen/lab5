package org.example;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Создаём парсер JSON
        Gson gson = new Gson();

        try (Reader reader = new FileReader("books.json")) {
            // Читаем файл и превращаем в список посетителей
            List<Visitor> visitors = gson.fromJson(reader, new TypeToken<List<Visitor>>(){}.getType());

            // Выполняем все задания
            task1(visitors);
            task2(visitors);
            task3(visitors);
            task4(visitors);
            task5(visitors);
            task6(visitors);

        } catch (Exception e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        }
    }

    // Задание 1: Вывести список посетителей и их количество
    private static void task1(List<Visitor> visitors) {
        System.out.println("=== Задание 1: Посетители библиотеки ===");

        long count = visitors.stream().count();
        System.out.println("Количество посетителей: " + count);

        System.out.println("Список посетителей:");
        visitors.stream().forEach(v -> {
            System.out.println("  " + v.getName() + " " + v.getSurname());
        });
    }

    // Задание 2: Вывести все книги без повторений
    private static void task2(List<Visitor> visitors) {
        System.out.println("\n=== Задание 2: Книги без повторений ===");

        // Собираем все книги от всех посетителей в один список
        List<Book> allBooks = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .distinct()  // Убираем дубликаты
                .collect(Collectors.toList());

        System.out.println("Количество уникальных книг: " + allBooks.size());
        System.out.println("Список книг:");
        allBooks.forEach(b -> {
            System.out.println("  " + b.getName() + " - " + b.getAuthor());
        });
    }

    // Задание 3: Отсортировать книги по году издания
    private static void task3(List<Visitor> visitors) {
        System.out.println("\n=== Задание 3: Сортировка книг по году ===");

        visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .sorted((b1, b2) -> Integer.compare(b1.getPublishingYear(), b2.getPublishingYear()))
                .forEach(b -> {
                    System.out.println("  " + b.getPublishingYear() + " - " + b.getName());
                });
    }

    // Задание 4: Проверить, есть ли книги Jane Austen
    private static void task4(List<Visitor> visitors) {
        System.out.println("\n=== Задание 4: Поиск автора Jane Austen ===");

        boolean hasJaneAusten = visitors.stream()
                .flatMap(v -> v.getFavoriteBooks().stream())
                .anyMatch(b -> "Jane Austen".equals(b.getAuthor()));

        if (hasJaneAusten) {
            System.out.println("Да, у посетителей есть книги Jane Austen");
        } else {
            System.out.println("Нет, книг Jane Austen не найдено");
        }
    }

    // Задание 5: Найти максимальное число книг у одного посетителя
    private static void task5(List<Visitor> visitors) {
        System.out.println("\n=== Задание 5: Максимальное число книг ===");

        int maxBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .max()
                .orElse(0);

        System.out.println("Максимальное число книг у одного посетителя: " + maxBooks);
    }

    // Задание 6: Создать SMS для подписчиков
    private static void task6(List<Visitor> visitors) {
        System.out.println("\n=== Задание 6: SMS подписчикам ===");

        // Считаем среднее число книг по всем посетителям
        double averageBooks = visitors.stream()
                .mapToInt(v -> v.getFavoriteBooks().size())
                .average()
                .orElse(0.0);

        System.out.println("Среднее число книг на посетителя: " + averageBooks);

        // Создаём сообщения только для подписчиков
        Map<String, List<SmsMessage>> groupedMessages = visitors.stream()
                .filter(v -> v.isSubscribed())  // Только подписчики
                .map(v -> {
                    int bookCount = v.getFavoriteBooks().size();
                    String messageText;

                    // Определяем текст сообщения
                    if (bookCount > averageBooks) {
                        messageText = "you are a bookworm";
                    } else if (bookCount < averageBooks) {
                        messageText = "read more";
                    } else {
                        messageText = "fine";
                    }

                    // Создаём объект сообщения
                    SmsMessage sms = new SmsMessage();
                    sms.setPhoneNumber(v.getPhone());
                    sms.setMessage(messageText);
                    return sms;
                })
                .collect(Collectors.groupingBy(SmsMessage::getMessage));

        // Выводим результаты
        groupedMessages.forEach((category, messages) -> {
            System.out.println("\nКатегория: " + category);
            messages.forEach(sms -> {
                System.out.println("  Телефон: " + sms.getPhoneNumber() + " -> " + sms.getMessage());
            });
        });
    }
}
