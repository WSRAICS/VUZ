# Запуск программы:

1. Скачать для своего компьютера (выбрать подходящую операционную систему и разрядность) jre по ссылке https://www.azul.com/downloads/zulu-community/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jre-fx 
2. Распаковать в папку "jre"
3. запустить скрипт bin/Worldskills_2020.bat



# Отчет по базе данных:

### Старт проверки:

· Время до конца конкурса 30.00

· Время трансляции 1.58.55

### Конец проверки:

· Время до конца конкурса 24.00

· Время трансляции 2.05.00

## Доступ к БД.

1. Используемая в проекте БД - "H2". Скачать ее можно по ссылке (https://www.h2database.com/html/main.html)

2. Для ее запуска потребуется установленная версия jre. Можно скачать то ссылке для своей платформы. Пакет msi (https://www.azul.com/downloads/zulu-community/?version=java-8-lts&os=windows&architecture=x86-64-bit&package=jdk-fx)

3. После установки необходимо запустить приложение "H2 Console". 
   
4. В поле "JDBC URL:" ввести абсолютный путь к базе данных в формате "jdbc:h2:C:\Users\User\Documents\Worldskills\VUZ\source\database". 
В вводимом имени бд не должно быть расширения ".mv.db"! Логин = "". Пароль = "".
   
5. БД автоматически создается в папке запуска приложения. ./bin/database.mv.db