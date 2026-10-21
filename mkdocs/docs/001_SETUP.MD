# 01. Setup

## Prasyarat

- Java 11 atau lebih baru
- Spring Boot 2.7.x (javax.*). Library ini belum untuk Spring Boot 3.
- Proyek pemakai mengelola versi Spring sendiri, lewat `spring-boot-starter-parent` atau plugin `io.spring.dependency-management`.

## Menambahkan dependency

### Lewat JitPack (disarankan)

Ganti `<tag>` dengan tag rilis di GitHub, misalnya `v2.0.3`. Untuk mencoba commit terbaru tanpa tag bisa memakai `spring-2-SNAPSHOT`.

```groovy
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'

    implementation 'com.github.onevour.spring-commons:spring-common:<tag>'

    // hanya jika memakai JPA
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.github.onevour.spring-commons:spring-common-jpa:<tag>'

    // hanya jika memakai crypto
    implementation 'com.github.onevour.spring-commons:spring-common-crypto:<tag>'
}
```

Contoh Maven ada di [README repo](https://github.com/onevour/spring-commons#pakai-lewat-jitpack).

### Lewat GitHub Packages

Artefak yang sama diterbitkan sebagai `com.onevour.core:spring-common:<versi>` di
`https://maven.pkg.github.com/onevour/spring-commons`. Repository ini butuh token GitHub
dengan scope `read:packages`, jadi JitPack lebih praktis untuk sebagian besar kasus.

## Versi dependency transitif

Library ini **tidak mengunci** versi Spring, Jackson, Hibernate, dan sebagainya. POM yang
terbit hanya memuat versi hasil resolusi saat library di-build, bukan versi strict. Versi
yang benar-benar dipakai mengikuti BOM proyek Anda.

Dua dependency di luar BOM Spring Boot, `joda-time` dan `commons-io`, ikut terbawa dari
`spring-common` dengan versi yang tertulis di POM. Timpa di proyek Anda bila perlu.

## Yang otomatis aktif

Begitu `spring-common` ada di classpath, auto-configuration mendaftarkan:

| Bean | Keterangan |
|---|---|
| `ObjectMapper` | `FAIL_ON_UNKNOWN_PROPERTIES = false`. Dilewati bila proyek sudah punya bean sendiri. |
| `RestTemplate` bernama `restTemplate` | timeout 12 detik. Dilewati bila proyek sudah punya bean dengan nama itu. |
| `RestBeanRegistry` | memindai interface turunan `RestRepository` di package aplikasi, lihat bab 03 |
| `RestRepositoryConfigValidator` | saat startup memastikan semua `${properti}` dan kunci database yang dipakai `RestRepository` ada; matikan dengan `spring-common.rest.validate-on-startup=false` |
| `MethodHandlerConverter` | aspek yang mengisi parameter `@ConverterResolver` di controller |
| `ClientManifestResolver` | argument resolver untuk `ClientManifest` dari atribut request `CLIENT_MANIFEST` |

Dengan `spring-common-jpa` ikut aktif `AuditUpdateListener` dan `AuditTableCreator` (bab 04).

Package yang dipindai adalah package kelas `@SpringBootApplication` beserta sub-package-nya,
sama seperti Spring Data.

## Verifikasi

Jalankan aplikasi dan cari baris log berikut:

```
RestBeanRegistry              : RestRepository found 2
RestRepositoryConfigValidator : RestRepository config validated: 5 method(s) in 2 repository(ies)
```

Baris pertama jumlah interface `RestRepository` yang terdaftar sebagai bean, baris kedua tanda
semua `key`/`url`-nya bisa di-resolve. Bila ada yang kurang, aplikasi berhenti dengan blok
"APPLICATION FAILED TO START" yang merinci method dan placeholder yang bermasalah.
