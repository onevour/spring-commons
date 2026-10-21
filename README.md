# Spring SDK

Kumpulan library untuk mempercepat pembuatan service Spring Boot 2.7 (Java 11).

## Library
- `spring-common`: pola MVC, RestRepository (klien HTTP deklaratif), utilitas
- `spring-common-jpa`: entity dasar dengan kolom audit, history otomatis ke tabel `_his`
- `spring-common-crypto`: AES dan RSA dengan pemuat kunci dari string, Base64, file, atau MinIO

## Tutorial
1. [Setup](mkdocs/docs/001_SETUP.MD)
2. [Model View Controller (MVC)](mkdocs/docs/002_MVC.MD)
3. [RestRepository](mkdocs/docs/003_REST_REPOSITORY.MD)
4. [JPA, audit, dan history](mkdocs/docs/004_JPA.MD)
5. [Crypto](mkdocs/docs/005_CRYPTO.MD)
6. [Utilitas](mkdocs/docs/006_UTILITIES.MD)

Dokumentasi juga bisa dijalankan sebagai situs: `cd mkdocs && mkdocs serve` (butuh `mkdocs-material`).

## Pakai lewat JitPack

Library ini dipublikasikan lewat [JitPack](https://jitpack.io) dari repo
`onevour/spring-commons`. Ganti `<tag>` dengan tag rilis, misalnya `v2.0.3`.

### Gradle

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.onevour.spring-commons:spring-common:<tag>'
    implementation 'com.github.onevour.spring-commons:spring-common-jpa:<tag>'
    implementation 'com.github.onevour.spring-commons:spring-common-crypto:<tag>'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.onevour.spring-commons</groupId>
        <artifactId>spring-common</artifactId>
        <version>&lt;tag&gt;</version>
    </dependency>
    <dependency>
        <groupId>com.github.onevour.spring-commons</groupId>
        <artifactId>spring-common-jpa</artifactId>
        <version>&lt;tag&gt;</version>
    </dependency>
    <dependency>
        <groupId>com.github.onevour.spring-commons</groupId>
        <artifactId>spring-common-crypto</artifactId>
        <version>&lt;tag&gt;</version>
    </dependency>
</dependencies>
```

### Catatan versi dependency transitif

Library ini **tidak mengunci** versi dependency transitif (Spring, Jackson, Hibernate, dan
sebagainya). POM yang diterbitkan hanya mencantumkan versi hasil resolusi saat build, bukan
versi strict, dan BOM `spring-boot-dependencies` tidak ikut diterbitkan. Versi yang benar-benar
dipakai mengikuti BOM proyek pemakai (misalnya plugin `io.spring.dependency-management` atau
`spring-boot-starter-parent` di proyek Anda).
