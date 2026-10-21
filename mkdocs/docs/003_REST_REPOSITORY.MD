# 03. RestRepository

Klien HTTP deklaratif: tulis interface, beri anotasi, dan library yang membuat proxy-nya.
Mirip Feign, tetapi berbasis `RestTemplate` dan tanpa konfigurasi tambahan.

## Mendefinisikan repository

```java
public interface PokemonRestRepository extends RestRepository {

    @Get(url = "https://pokeapi.co/api/v2/pokemon/ditto")
    String ditto();

    @Get(url = "https://pokeapi.co/api/v2/pokemon/{name}")
    ResponseWrapper<PokemonResponse> byName(@PathVariable("name") String name);

    @Post(key = "${payment.base-url}", url = "/v1/charge", read = 30000)
    ResponseWrapper<ChargeResponse> charge(HttpHeaders headers, ChargeRequest body);
}
```

Syaratnya hanya dua: interface `extends RestRepository` dan berada di package aplikasi
(package kelas `@SpringBootApplication` atau di bawahnya). Interface terdaftar sebagai bean
dengan nama simple name berawalan huruf kecil (`pokemonRestRepository`) dan bisa di-`@Autowired`
seperti biasa.

## Anotasi method

`@Get`, `@Post`, `@Put`, `@Patch`, `@Delete`, semua dengan atribut yang sama:

| Atribut | Default | Keterangan |
|---|---|---|
| `key` | `""` | base URL, lihat placeholder di bawah |
| `url` | `""` | path atau URL lengkap, boleh berisi `{pathVariable}` |
| `connect` | -1 | timeout membuka koneksi (ms) |
| `request` | -1 | timeout menunggu koneksi dari pool (ms) |
| `read` | -1 | timeout membaca response (ms) |
| `contentType` | `application/json` | header `Content-Type` |
| `throwException` | `true` | perilaku saat server tidak bisa dihubungi, lihat *Menangani response* |

URL akhir = `key` + `url`. Keduanya boleh kosong selama gabungannya URL yang valid.

### Placeholder untuk `key` dan `url`

| Bentuk | Sumber nilai |
|---|---|
| `https://...` atau teks biasa di `url` | dipakai apa adanya |
| `${nama.properti}` | `Environment` (application.properties/yml, env var). Error saat dipanggil bila properti tidak ada. |
| `D{KUNCI}` atau `d{KUNCI}` | bean `RestConfigBaseKey`, biasanya membaca tabel konfigurasi |
| teks biasa di `key` yang bukan `http...` | diperlakukan sebagai `D{teks}` |

Nilai yang tidak ditemukan dilaporkan saat startup (lihat *Validasi saat startup* di bawah).

Implementasi `RestConfigBaseKey` cukup satu bean:

```java
@Configuration
public class RestConfig {

    @Autowired
    ConfigRepository configRepository;

    @Bean
    public RestConfigBaseKey restConfigBaseKey() {
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                return configRepository.findById(key).orElseThrow().getValue();
            }
        };
    }
}
```

### Validasi saat startup

Semua `key` dan `url` di semua `RestRepository` dicek saat aplikasi start, setelah semua bean
siap tetapi **sebelum web server menerima request**. Mirip Spring memvalidasi
`@ConfigurationProperties`: bila ada `${properti}` yang tidak ada, kunci database (`D{...}` atau
tanpa prefix) yang tidak ditemukan, bean `RestConfigBaseKey` tidak ada padahal dipakai, atau
hasil gabungannya bukan URL http(s) yang valid, aplikasi **tidak jadi up**. Semua masalah
dilaporkan sekaligus:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Konfigurasi RestRepository belum lengkap, 2 masalah:
  - com.acme.PaymentRestRepository#charge(): property ${payment.base-url} not found in Environment (...) (key='${payment.base-url}', url='/v1/charge')
  - com.acme.PaymentRestRepository#refund(): database key 'PAYMENT_REFUND' not found: RestConfigBaseKey returned null/empty (key='PAYMENT_REFUND', url='/v1/refund')

Action:

Lengkapi properti di application.properties/yml atau environment variable untuk ${...}, dan baris
di tabel konfigurasi (bean RestConfigBaseKey) untuk D{...} atau key tanpa prefix. Untuk
menonaktifkan sementara: spring-common.rest.validate-on-startup=false
```

Berhasil ditandai satu baris log:

```
RestRepositoryConfigValidator : RestRepository config validated: 7 method(s) in 2 repository(ies)
```

Menonaktifkan validasi (misalnya tabel konfigurasi diisi setelah aplikasi jalan):

```properties
spring-common.rest.validate-on-startup=false
```

Dengan validasi mati, nilai yang hilang baru dilaporkan saat method dipanggil, sebagai
`RestConfigNotFoundException`.

`${...}` juga mendukung nilai default dengan sintaks Spring: `${payment.base-url:http://localhost:8080}`.

### Timeout

- Semua timeout `<= 0` (default): memakai bean `restTemplate` (bawaan 12 detik, atau bean milik proyek).
- Ada yang `> 0`: dibuat `RestTemplate` khusus untuk kombinasi timeout itu, dipakai ulang (cache),
  dan nilai dipakai apa adanya. `read = 2000` benar-benar 2 detik.

## Parameter method

Parameter dikenali dari tipe dan anotasinya, urutannya bebas:

| Parameter | Diperlakukan sebagai |
|---|---|
| `HttpHeaders` | header request. Objek disalin dulu, jadi milik pemanggil tidak berubah. |
| `@PathVariable("name") T` | mengganti `{name}` di URL. Nilai di-URL-encode sebagai segmen path. |
| parameter lain (tanpa anotasi) | body request |

Hanya satu body per method. Untuk `multipart/form-data` dan `x-www-form-urlencoded`, body berupa
`MultiValueMap<String, Object>`.

### GET dengan objek sebagai query string

Pada `@Get`, parameter body diubah menjadi query string dari field objeknya:

```java
@Data
public class SearchRequest {
    String keyword;
    Integer page;
    Integer size;
}

@Get(url = "https://api.example.com/search")
ResponseWrapper<SearchResult> search(SearchRequest request);
// → GET https://api.example.com/search?keyword=a%20b&page=1&size=20
```

Field bernilai `null` dilewati, field `static` diabaikan, dan nilai di-URL-encode.
Query string yang sudah ada di `url` dipertahankan, parameter berikutnya disambung dengan `&`.
Untuk POST/PUT/PATCH/DELETE query string di `url` juga dipertahankan.

## Tipe kembalian dan penanganan response

Ada dua gaya:

### 1. Tipe langsung (`String`, `MyResponse`, `List<MyResponse>`)

Body di-deserialisasi ke tipe itu. Kegagalan dilempar sebagai exception:

| Kondisi | Exception |
|---|---|
| HTTP 4xx/5xx | `HttpStatusCodeException` (`HttpClientErrorException`/`HttpServerErrorException`), body error ada di `getResponseBodyAsString()` |
| timeout, DNS, connection refused, `throwException = true` | `BadGatewayException` dengan cause `ResourceAccessException` |
| sama, `throwException = false` | `ResourceAccessException` asli |

Tangani secara global dengan `@RestControllerAdvice`.

### 2. `ResponseWrapper<T>`

Tidak melempar exception untuk HTTP 4xx/5xx. Semua informasi ada di wrapper:

```java
ResponseWrapper<PokemonResponse> res = pokemonRestRepository.byName("ditto");

if (res.isSuccess()) {                       // code 2xx
    PokemonResponse body = res.getBody();
}
res.getCode();                               // HTTP status, 502 bila server tak terjangkau
res.getHeaders();
res.getExceptionBodyAsText();                // body error mentah dari server
res.getException();                          // HttpStatusCodeException, bila ada
res.isErrorResourceAccessException();        // true bila timeout/DNS/refused (butuh throwException = false)
```

Helper bergaya `Optional`:

```java
// body, atau lempar exception yang tercatat (HttpStatusCodeException / ResourceAccessException);
// bila body null tanpa exception (mis. 204) lempar IllegalStateException
PokemonResponse body = res.bodyOrElseThrow();

// body, atau exception milik Anda
PokemonResponse body = res.bodyOrElseThrow(() -> new NotFoundException("pokemon"));

// exception milik Anda hanya untuk kode tertentu
PokemonResponse body = res.bodyOrElseThrow(() -> new NotFoundException("pokemon"), 404);

res.isErrorAndThrow(() -> new UpstreamException());       // bila error apa pun
res.isErrorAndThrow(() -> new UpstreamException(), 401, 403);
res.isErrorResourceAccessExceptionThrow();                // lempar ResourceAccessException bila ada
```

Catatan: dengan `throwException = true` (default), kegagalan koneksi tetap dilempar sebagai
`BadGatewayException` meskipun tipe kembaliannya `ResponseWrapper`. Set `throwException = false`
bila ingin kegagalan koneksi masuk ke wrapper.

## Contoh content type lain

### multipart/form-data

```java
@Post(key = "${sample.url}", url = "/upload", contentType = MediaType.MULTIPART_FORM_DATA_VALUE, read = 60000)
ResponseWrapper<LivenessResponse> verify(HttpHeaders headers, MultiValueMap<String, Object> form);
```

```java
MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
form.set("trx_id", trxId);
for (MultipartFile file : files) {
    form.add("file", new ByteArrayResource(file.getBytes()) {
        @Override
        public String getFilename() { return file.getOriginalFilename(); }
    });
}
ResponseWrapper<UploadResponse> res = uploadRestRepository.upload(headers, form);
```

### x-www-form-urlencoded

```java
@Post(url = "https://httpbin.org/post", contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
String urlEncoded(MultiValueMap<String, Object> form);
```

## Cara kerja singkat

`RestBeanRegistry` memindai bytecode package aplikasi, mendaftarkan `RestRepositoryFactoryBean`
per interface, dan class baru dimuat saat bean dibuat lewat classloader context. Karena itu
repository tetap ditemukan saat Spring Boot Devtools aktif. Setiap pemanggilan method diproses
`RestExecutorMethodHandler` yang membangun `ApiRequest` lalu memanggil `RestTemplate`.

Dua interface dengan simple name sama di package berbeda tetap terdaftar: yang kedua memakai
nama lengkapnya sebagai nama bean, disertai peringatan di log.
