# Vývoj aplikácii s viacvrstvovou architektúrou
## Semestrálny projekt - Backend Server

Aplikácia pre server využíva **Spring Boot** a **Spring Data JPA** s **Hibernate ORM** a **Flyway** pre migrácie
a **PostgreSQL** databázu.

### Setup
Na spustenie je potrebný JDK vo verzii 11 a taktiež je potrebné nastaviť systémovú premennú `JAVA_HOME` na cestu
k adresáru, v ktorom je JDK nainštalované, napr. `C:\Program Files\Java\jdk-11.0.14`.

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
