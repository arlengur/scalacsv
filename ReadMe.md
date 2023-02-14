# Задача
Используя данные с https://www.kaggle.com/datasets/elemento/nyc-yellow-taxi-trip-data
 
Реализовать эффективную структуру данных, реализующую следующий интерфейс

    interface TaxiTable {
	// Инициализация структуры данных из csv файла<br>
	void from_csv(Path data);

	// Найти среднюю длину поездки для каждого уникального количества пассажиров в промежутке времени от start до end
	select avg(trip_distance) where tpep_pickup_datetime >= start and  tpep_dropoff_datetime <= end group by passenger_count;
	Map<Integer, Double> getAverageDistances(LocalDateTime start, LocalDateTime end);
    }
 
В предположении, что:
- read_csv будет вызван один раз, а getAverageDistances будет вызываться множество раз.
- все данные с запасом помещаются в оперативную память
 
Требования к решению:
- Для чтения csv можно использовать любые библиотеки.
- Добавлено несколько unit тестов
- Написан бенчмарк