# Spring SDK

Kumpulan library untuk mempercepat pembuatan service Spring Boot 2.7 (Java 11):

| Modul | Isi | Butuh |
|---|---|---|
| `spring-common` | Pola MVC (`BaseRestController`, `BaseService`, `ServiceResolver`, `ResponseConverter`), **RestRepository** (klien HTTP deklaratif berbasis anotasi), utilitas (`ValueOf`, `BeanCopy`, `DTCommons`, `StopWatch`) | `spring-boot-starter-web` |
| `spring-common-jpa` | `BaseEntity` dengan kolom audit, `BaseJpaRepository`, `BaseSpec`, dan **history otomatis** (`@EntityHistory`) ke tabel `<nama>_his` | `spring-boot-starter-data-jpa` + `spring-common` |
| `spring-common-crypto` | AES (CBC/GCM) dan RSA dengan pemuat kunci dari string, Base64, file, atau MinIO | tidak bergantung Spring |

Semua konfigurasi dimuat lewat auto-configuration Spring Boot. Tidak ada anotasi `@EnableXxx` yang perlu ditambahkan.

## Daftar isi

1. [Setup](001_SETUP.MD)
2. [Model View Controller (MVC)](002_MVC.MD)
3. [RestRepository](003_REST_REPOSITORY.MD)
4. [JPA, audit, dan history](004_JPA.MD)
5. [Crypto](005_CRYPTO.MD)
6. [Utilitas](006_UTILITIES.MD)
7. [Rilis](007_RELEASE.MD)

## Aplikasi contoh di repo ini

| Modul | Menunjukkan |
|---|---|
| `spring-test` | MVC dan RestRepository tanpa database |
| `spring-jpa-postgres-test` | JPA, audit, history di PostgreSQL (SQL di `src/main/resources/sql`) |
| `spring-jpa-mariadb-test` | Hal yang sama di MariaDB/MySQL |
