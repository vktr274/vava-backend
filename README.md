# Vývoj aplikácii s viacvrstvovou architektúrou
## Semestrálny projekt - Backend Server

Aplikácia pre server využíva **Spring Boot** a **Spring Data JPA** s **Hibernate ORM** a **Flyway** pre migrácie
a **PostgreSQL** databázu.

### Setup
Na spustenie je potrebný JDK vo verzii 11 a taktiež je potrebné nastaviť systémovú premennú prostredia `JAVA_HOME` na
cestu k adresáru, v ktorom je JDK nainštalovaný, napr. `C:\Program Files\Java\jdk-11.0.14`. 

Tiež je potrebné nastaviť systémové premenné prostredia s údajmi k databáze - URL, meno databázy a heslo - nasledovne:

`SPRING_DATASOURCE_URL` je URL v tvare `jdbc:postgresql://localhost:<port>/<nazov_databazy>?currentSchema=<nazov_schemy>`.

`SPRING_DATASOURCE_USERNAME` je meno roly, ktorá vlastní databázu.

`SPRING_DATASOURCE_PASSWORD` je heslo roly, ktorá vlastní databázu.

Po pridaní systémových premenných prostredia je vhodné reštartovať počítač.

Pred spustením treba načítať v IntellijIDEA **Gradle závislosti** a následne sa aplikácia spúšťa triedou **Main**.
Alternatívou je spustenie v príkazovom riadku.

#### Windows CMD
```
gradlew bootRun
```

#### Mac OS alebo Windows PowerShell
```
./gradlew bootRun
```
