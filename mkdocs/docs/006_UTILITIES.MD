# 06. Utilitas

Kelas bantu di `spring-common`, paket `com.onevour.core.applications.commons`.

## ValueOf

Pengecekan nilai yang memperlakukan `null` dan string kosong/spasi sama:

```java
ValueOf.isNull(null)                    // true
ValueOf.isNull("  ")                    // true
ValueOf.isNull(a, b, c)                 // true bila salah satu null/kosong
ValueOf.nonNull(value)

ValueOf.init(value, "default")          // value, atau default bila null/kosong (juga Integer, Long, Double, Float, Boolean)
ValueOf.ofNullable(value)               // Optional kosong bila null/kosong

ValueOf.equalText("abc", "ABC", "x")    // true, ignore case
ValueOf.containsTextIgnoreCase(text, "err", "fail")
ValueOf.startWithText(text, "08", "+62")
ValueOf.equalInt(code, 200, 201)

ValueOf.isNumber("0812")                // hanya digit
ValueOf.isDouble("1.5")
ValueOf.minMaxLength(6, 20, password)
ValueOf.lengthEquals(nik, 16)
ValueOf.isValidRegex("^[A-Z]{3}$", code)
```

## BeanCopy

Salin properti antar objek berdasarkan nama (dibungkus `BeanUtils`):

```java
UserResponse res = BeanCopy.copy(user, UserResponse.class);
UserResponse res = BeanCopy.copy(user, UserResponse.class, "password", "token"); // kecualikan field
List<UserResponse> list = BeanCopy.copyCollection(users, UserResponse.class);

BeanUtils.copyProperties(source, target, BeanCopy.getNullPropertyNames(source)); // hanya field non-null
Order clone = BeanCopy.copyDeep(order);  // deep copy lewat serialisasi, objek harus Serializable
```

## DTCommons

Batas waktu berbasis Joda-Time, berguna untuk filter laporan:

```java
DateTime from = DTCommons.startOfDay();          // hari ini 00:00:00.000
DateTime to   = DTCommons.endOfDay();            // hari ini 23:59:59.999
DTCommons.startOfDay(date); DTCommons.endOfDay(date);
DTCommons.startOfDayYesterday(); DTCommons.endOfDayYesterday();
DTCommons.startOfThisMonth(); DTCommons.endOfThisMonth();
DTCommons.startOfMonth(dateTime); DTCommons.endOfMonth(dateTime);
DTCommons.startOfThisYear(); DTCommons.endOfThisYear();
```

## StopWatch

Turunan `org.springframework.util.StopWatch` yang otomatis menghentikan task sebelumnya dan
bisa membungkus lambda:

```java
StopWatch watch = new StopWatch("checkout");

Cart cart = watch.start("load cart", () -> cartService.load(id));     // Callable, hasil dikembalikan
watch.start("calculate", () -> pricing.calculate(cart));               // Runnable
Order order = watch.start("save", () -> orderService.save(cart));

log.info("\n{}", watch.prettyPrintMillis());
```

Keluaran:

```
StopWatch 'checkout': running time = 184 ms
---------------------------------------------
ms         %     Task name
---------------------------------------------
000120  065%  load cart
000004  002%  calculate
000060  033%  save
```

Tersedia juga `prettyPrintSecond()`, `shortSummaryMillis()`, `shortSummarySecond()`.

## Lainnya

| Kelas | Kegunaan |
|---|---|
| `Base64Commons` | encode file ke Base64, decode, dan buang prefix MIME (`data:image/...;base64,`) |
| `FileUploadCommons` | simpan gambar dari URL, konversi path/byte/Base64 |
| `ImageCommons` | resize, crop tengah, crop pas foto, konversi ke PNG |
| `ReflectionCommons` | utilitas tipe generik, dipakai internal RestRepository |
| `DTFormat` | deteksi dan konversi format tanggal (termasuk format e-KTP) |
| `ApiRequest` | builder request HTTP manual, dipakai `BaseService.requestData(...)` bila tidak ingin memakai RestRepository |

Contoh `ApiRequest` manual dari `BaseService`:

```java
ApiRequest request = new ApiRequest.Builder(PokemonResponse.class)
        .setUrl("https://pokeapi.co/api/v2/pokemon/ditto")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .buildGet();
PokemonResponse res = requestData(request);
```
