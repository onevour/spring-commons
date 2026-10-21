# 07. Rilis

Panduan untuk maintainer: dari perubahan kode sampai artefak bisa dipakai proyek lain.
Ada dua jalur publikasi. JitPack adalah jalur utama, GitHub Packages jalur cadangan.

| Jalur | Koordinat untuk pemakai | Versi diambil dari | Butuh |
|---|---|---|---|
| JitPack | `com.github.onevour.spring-commons:<modul>:<tag>` | **tag Git**, sama untuk semua modul | repo publik, tag di GitHub |
| GitHub Packages | `com.onevour.core:<modul>:<versi>` | `version` di `build.gradle` tiap modul | token GitHub scope `write:packages` |

## 0. Sekali saja: amankan token

`gradle.properties` di root berisi `user` dan `token` GitHub dan **tidak boleh di-commit**
(sudah ada di `.gitignore`). Kalau token pernah masuk history, rotasi di GitHub:
*Settings → Developer settings → Personal access tokens*. Lebih aman lagi, pindahkan ke
`~/.gradle/gradle.properties` supaya tidak ada di folder repo sama sekali.

## 1. Siapkan perubahan

1. Pastikan semua test hijau (lihat *Menjalankan test* di README):
   ```bash
   ./gradlew test -x :spring-test:test
   ```
2. Naikkan `version` di `build.gradle` modul yang berubah (`spring-common`, `spring-common-jpa`,
   `spring-common-crypto`). Versi ini dipakai GitHub Packages dan tercantum di POM.
3. Cek artefak yang akan terbit secara lokal:
   ```bash
   ./gradlew clean publishToMavenLocal -x test
   ```
   Hasilnya ada di `~/.m2/repository/com/onevour/core/<modul>/<versi>/`. POM-nya tidak boleh
   berisi `<dependencyManagement>`; itu tanda library tidak mengunci versi untuk pemakai.
4. Commit dan push ke branch rilis:
   ```bash
   git push origin spring-2
   ```

## 2. Rilis lewat JitPack

JitPack membangun dari GitHub memakai `jitpack.yml` di root
(JDK 11, `./gradlew clean publishToMavenLocal -x test`). Tidak ada yang perlu diunggah manual.

1. Buat tag dari commit yang sudah di-push, lalu push tag-nya:
   ```bash
   git tag v2.0.3
   ```
   ```bash
   git push origin v2.0.3
   ```
   Pakai format `vMAJOR.MINOR.PATCH`. Satu tag berlaku untuk ketiga modul.
2. Picu build pertama. JitPack membangun saat artefak pertama kali diminta, atau lewat halaman
   `https://jitpack.io/#onevour/spring-commons` → pilih tag → *Get it*. Dari terminal:
   ```bash
   curl -s https://jitpack.io/api/builds/com.github.onevour/spring-commons/v2.0.3
   ```
   Status `ok` berarti selesai. Build pertama memakan beberapa menit.
3. Bila gagal, buka log build di `https://jitpack.io/com/github/onevour/spring-commons/v2.0.3/build.log`.
   Penyebab umum: JDK salah di `jitpack.yml`, test ikut jalan dan butuh jaringan, atau modul
   gagal resolve dependency. Perbaiki, lalu **buat tag baru**; JitPack tidak membangun ulang tag
   yang sama kecuali build sebelumnya gagal.
4. Verifikasi dari proyek pemakai:
   ```groovy
   repositories { maven { url 'https://jitpack.io' } }
   dependencies { implementation 'com.github.onevour.spring-commons:spring-common:v2.0.3' }
   ```

### Versi tanpa tag

Untuk mencoba sebelum rilis resmi, JitPack juga menerima:

| Versi | Artinya |
|---|---|
| `spring-2-SNAPSHOT` | commit terbaru di branch `spring-2`, dibangun ulang tiap kali berubah |
| `b07d91e` (hash pendek) | commit tertentu |

Jangan pakai keduanya di produksi, karena isinya bisa berubah.

## 3. Rilis lewat GitHub Packages

Jalur ini memakai blok `publishing` di tiap modul (repository `GitHubPackages`). Versi diambil
dari `version` di `build.gradle`, jadi naikkan dulu bila perlu.

```bash
./gradlew publish -x test
```

Kredensial dibaca dari properti Gradle `user` dan `token` (`~/.gradle/gradle.properties`) atau
environment variable `USERNAME` dan `TOKEN`. Token butuh scope `write:packages`. Versi yang sudah
ada tidak bisa ditimpa; naikkan versinya.

Pemakai perlu repository dan token dengan scope `read:packages`:

```groovy
repositories {
    maven {
        url 'https://maven.pkg.github.com/onevour/spring-commons'
        credentials {
            username = project.findProperty('gpr.user') ?: System.getenv('USERNAME')
            password = project.findProperty('gpr.token') ?: System.getenv('TOKEN')
        }
    }
}
dependencies { implementation 'com.onevour.core:spring-common:2.0.3' }
```

## 4. Setelah rilis

- Catat perubahan di GitHub Release untuk tag tersebut, atau di pesan tag.
- Beri tahu proyek pemakai tentang perubahan yang memutus kompatibilitas, misalnya method yang
  dihapus atau perubahan perilaku `ResponseWrapper`.
- Perubahan yang sama biasanya perlu di-port ke repo source `asliri/spring-sdk` (paket `com.asliri`),
  yang dirilis ke Nexus Asliri lewat `./gradlew publish` di repo itu.

## Ringkasan perintah

```bash
./gradlew test -x :spring-test:test && ./gradlew clean publishToMavenLocal -x test && git push origin spring-2 && git tag v2.0.3 && git push origin v2.0.3
```
