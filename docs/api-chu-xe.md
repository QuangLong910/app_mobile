# API app chủ xe (người cho thuê)

Kế hoạch 20 ngày liệt kê 36 đường dẫn. App chủ xe dùng **22** trong số đó.
14 đường dẫn còn lại là của khách thuê (`GET /cars` có lọc, `POST /bookings/quote`,
`POST /bookings`, `GET /bookings`) và của quản trị (`/admin/**`) — app này không gọi.

Base URL đặt ở `app/build.gradle.kts` → `BuildConfig.BASE_URL`. Đổi địa chỉ thì sửa kèm
`res/xml/network_security_config.xml`, nếu không sẽ dính `CLEARTEXT not permitted`.

## Chạy trên máy thật

Mặc định `BASE_URL = http://127.0.0.1:5160/` và đi qua **cáp USB**, không qua WiFi:

```sh
cd mock-server && python3 -u server.py      # cửa sổ 1
adb reverse tcp:5160 tcp:5160               # cửa sổ 2, chạy LẠI sau mỗi lần cắm lại máy
./gradlew :app:installDebug
```

Chọn cách này vì IP WiFi của máy tính đổi liên tục — đang test thì máy tính nhảy mạng là
app mất kết nối, mà lỗi nhìn y hệt lỗi backend. Qua cáp thì không phụ thuộc mạng nào.

Muốn chạy qua WiFi hoặc giả lập thì đổi `BASE_URL` sang IP LAN (máy thật) hoặc `10.0.2.2`
(giả lập), và thêm địa chỉ đó vào `network_security_config.xml`.

**Tài khoản của máy chủ giả:** `0364184928` / `test`, OTP `123456`. Sai thì trả 401
`SAI_DANG_NHAP` để test được cả nhánh lỗi. Chủ xe mẫu có 12 xe và 6 đơn ở các trạng thái
khác nhau.

**Ảnh mẫu:** chạy `python3 tai-anh.py` một lần để tải 17 ảnh xe và ảnh tin thật từ
Wikimedia Commons về `uploads/` (nguồn và giấy phép ghi trong `uploads/anh-nguon.txt`).
Không chạy cũng được — url nào chưa có tệp thì máy chủ tự vẽ một ô màu thay thế.

## Dùng chung một backend cho cả nhóm (Tailscale)

Chỉ một người chạy `mock-server` (sau này là backend thật của N1), cả nhóm trỏ vào đó —
không cần chung WiFi, không cần mở cổng ra Internet.

**Địa chỉ backend giờ đọc từ `local.properties`**, tệp này không lên kho mã nên mỗi người
đặt một kiểu mà không ai phải sửa `build.gradle.kts` của người khác:

```properties
# Cáp USB (chạy adb reverse tcp:5160 tcp:5160 trước)
baseUrl=http://127.0.0.1:5160/

# Qua Tailscale, máy của Long chạy backend
# baseUrl=http://100.86.242.10:5160/
```

Không khai `baseUrl` thì mặc định là `http://127.0.0.1:5160/`.

### Các bước

1. **Máy chạy backend** cài Tailscale, đăng nhập vào tailnet của nhóm, chạy `./chay.sh`.
   Máy chủ đã nghe `0.0.0.0` nên tự động lộ trên địa chỉ Tailscale.
   Lấy IP bằng `tailscale ip -4`.
2. **Điện thoại test** cài app Tailscale, đăng nhập cùng tài khoản nhóm, bật VPN.
3. **Máy của người viết app** sửa `baseUrl` trong `local.properties` thành IP của máy chạy
   backend, rồi `./gradlew installDebug`.

### Dùng IP, đừng dùng tên máy

Tailnet của nhóm là **`tail82d659.ts.net`**, tài khoản `quanglongtv910@gmail.com` làm chủ.
Trước đó nhóm dùng nhờ tailnet của trường (`tail4376a.ts.net`, 167 tài khoản) — đã bỏ vì
backend lộ với cả 167 người đó.

MagicDNS đang tắt nên dùng IP, đừng dùng tên máy. Đã đo:

```
http://100.86.242.10:5160  → HTTP 200 (4 ms)
```

Muốn dùng tên cho dễ nhớ thì chủ tailnet bật MagicDNS trong admin console; `ts.net` đã được
khai sẵn trong `network_security_config.xml` để lúc đó không phải sửa gì thêm.

### Cleartext

Bản **debug** dùng `app/src/debug/res/xml/network_security_config.xml` mở cleartext cho mọi
địa chỉ, nên trỏ vào IP nào cũng chạy mà không phải sửa XML rồi build lại. Bản **release**
vẫn dùng tệp ở `src/main`, chỉ mở đúng những địa chỉ đã khai.

## Chế độ demo — chạy rời máy tính

Gọi máy chủ không được thì app **tự rơi về backend giả nhúng trong APK**
(`data/demo/`): 12 xe, 6 đơn, 5 tin, 17 ảnh nằm trong `assets/demo/`. Bấm nút vẫn đổi
trạng thái thật — nhận đơn, từ chối, ký biên bản đều chạy — chỉ là trạng thái sống trong
tiến trình app và mất khi tắt app.

Nghĩa là **cầm điện thoại đi demo ở đâu cũng được**: không cáp, không WiFi, không máy tính.
Tài khoản vẫn `0364184928` / `test`.

Đã rơi về demo thì ở luôn đó cho tới khi mở lại app, không thử lại mạng giữa chừng — nửa
màn lấy từ máy chủ, nửa màn lấy từ demo là cách nhanh nhất để có dữ liệu mâu thuẫn.

Muốn dùng máy chủ thật thì bật `mock-server` **trước khi** mở app; app gọi được là dùng luôn
máy chủ, không đụng tới dữ liệu demo.

Luật nghiệp vụ trong `DemoBackend.kt` phải khớp với backend thật. Lệch là demo qua mà chạy
thật thì hỏng.

## Đã nối dây

| Đường dẫn | Dùng ở màn | Khai báo | Gọi qua |
|---|---|---|---|
| `POST /auth/register` | Đăng ký | `AuthApi.register` | `AuthRepository.register` |
| `POST /auth/verify-otp` | Nhập OTP | `AuthApi.verifyOtp` | `AuthRepository.verifyOtp` |
| `POST /auth/login` | Đăng nhập | `AuthApi.login` | `AuthRepository.login` |
| `GET /me` | Hồ sơ | `AuthApi.me` | `AuthRepository.me` |
| `POST /me/documents` | Nộp CCCD + GPLX | `AuthApi.submitDocuments` | `AuthRepository.submitDocuments` |
| `POST /files` | Mọi chỗ có ảnh | `FileApi.upload` | `FileRepository.upload` |
| `GET /owner/agreement` | Màn điều khoản | `OwnerApi.agreement` | `OwnerCarRepository.agreement` |
| `POST /owner/agreement/accept` | Ký cam kết | `OwnerApi.acceptAgreement` | `OwnerCarRepository.acceptAgreement` |
| `GET /owner/cars` | Xe của tôi | `OwnerApi.cars` | `OwnerCarRepository.myCars` |
| `POST /owner/cars` | Đăng xe | `OwnerApi.createCar` | `OwnerCarRepository.createCar` |
| `PUT /owner/cars/{id}` | Sửa giá, mô tả, ảnh | `OwnerApi.updateCar` | `OwnerCarRepository.updateCar` |
| `PUT /owner/cars/{id}/calendar` | Lịch mở/khoá ngày | `OwnerApi.updateCalendar` | `OwnerCarRepository.blockDays` / `openDays` |
| `GET /cars/{id}` | Xem lại tin xe | `BookingApi.car` | `OwnerCarRepository.car` |
| `GET /cars/{id}/availability` | Tô lịch xe | `BookingApi.availability` | `OwnerCarRepository.availability` |
| `GET /owner/bookings` | Đơn của xe tôi | `OwnerApi.bookings` | `OwnerBookingRepository.myBookings` |
| `GET /bookings/{id}` | Chi tiết đơn | `BookingApi.booking` | `OwnerBookingRepository.detail` |
| `POST /owner/bookings/{id}/confirm` | Nhận đơn | `OwnerApi.confirmBooking` | `OwnerBookingRepository.confirm` |
| `POST /owner/bookings/{id}/reject` | Từ chối đơn | `OwnerApi.rejectBooking` | `OwnerBookingRepository.reject` |
| `POST /bookings/{id}/cancel` | Huỷ đơn | `BookingApi.cancel` | `OwnerBookingRepository.cancel` |
| `POST /bookings/{id}/handovers` | Lập biên bản GIAO | `BookingApi.createHandover` | `HandoverRepository.createGiao` |
| `POST /handovers/{id}/photos` | Soi biên bản TRA, chụp bổ sung | `HandoverApi.addPhotos` | `HandoverRepository.addPhotos` |
| `POST /handovers/{id}/review` | Ký hoặc phản đối | `HandoverApi.review` | `HandoverRepository.sign` / `object_` |

## Ngoài hợp đồng

| Đường dẫn | Dùng ở màn | Tình trạng |
|---|---|---|
| `GET /news` | Tin tức | **Chưa có trong §04.** App đã nối, máy chủ giả đã trả. N1 cần bổ sung vào `docs/api.md` hoặc nhóm bỏ màn này. |

## Quy ước đã cài sẵn

- **Tiền** là `Long` đồng ở mọi model. Không có `Double`/`Float` nào trong tầng dữ liệu.
- **Ngày thuê** là `LocalDate`, đi trên dây dưới dạng `"2026-09-12"` (`LocalDateAdapter`).
  Mốc hệ thống là `OffsetDateTime` ISO có múi giờ (`OffsetDateTimeAdapter`).
- **Lỗi nghiệp vụ** về một dạng `ApiResult.Err(code, message)`; bắt theo `ErrorCode`, không
  bắt theo câu chữ. Mã đã có: `SLOT_TAKEN`, `CAR_UNAVAILABLE`, `KYC_REQUIRED`,
  `OWNER_AGREEMENT_REQUIRED`, `WRONG_STATE`, `HANDOVER_INCOMPLETE`, cộng `NETWORK`,
  `UNAUTHORIZED`, `UNKNOWN` sinh ở phía app.
- **JWT** tự gắn vào mọi yêu cầu qua `AuthInterceptor`, lưu ở `TokenStore`.
- `HandoverRepository.createGiao` chặn sớm khi thiếu một trong 6 khung ảnh bắt buộc
  (`TRUOC, SAU, TRAI, PHAI, TAPLO, ODO`) — đỡ một vòng mạng, backend vẫn kiểm lại.

## Lắp ghép

Hilt lo toàn bộ. `NetworkModule` cấp Gson, TokenStore, OkHttp, Retrofit và 5 interface API;
các repository là `@Singleton` nhận phụ thuộc qua hàm dựng. Màn hình chỉ cần
`hiltViewModel()`, không tự dựng gì.

```kotlin
@HiltViewModel
class OwnerBookingsViewModel @Inject constructor(
    private val repo: OwnerBookingRepository,
) : ViewModel()
```

```kotlin
when (val r = repo.confirm(bookingId)) {
    is ApiResult.Ok  -> hienTrangThai(r.data.status)   // CHO_THANH_TOAN
    is ApiResult.Err -> when (r.code) {
        ErrorCode.WRONG_STATE -> baoDonDaDoiTrangThai()
        ErrorCode.NETWORK     -> baoMatMang()
        else                  -> baoLoiChung(r.message)
    }
}
```

Bốn ViewModel đã có sẵn ở `ui/screens/owner/`: `SessionViewModel` (đăng ký, OTP, đăng nhập,
hồ sơ), `OwnerCarsViewModel` (cam kết, xe của tôi, lịch, tải ảnh), `OwnerBookingsViewModel`
(đơn, nhận, từ chối, huỷ), `HandoverViewModel` (6 khung ảnh, lập biên bản giao, soi và ký
biên bản trả).

### Hai chỗ phải lưu ý khi sửa build

- Hilt phải từ **2.60.1** trở lên; bản 2.57.x đổ về dùng `BaseExtension` mà AGP 9 đã bỏ,
  áp plugin là hỏng ngay.
- `gradle.properties` có `android.disallowKotlinSourceSets=false`. AGP 9 dùng Kotlin dựng
  sẵn và cấm plugin khác thêm source set, trong khi KSP vẫn khai theo kiểu cũ. Bỏ dòng này
  thì build đứng ở khâu cấu hình.

## Phần còn lại

- Nén ảnh 1600px/JPEG 80% đã có ở `data/AnhNen.kt`, `PhotoSlotBox` gọi sẵn trước khi upload.
- Màn hình: xem `docs/giao-dien-chu-xe.md`.
- `GET /owner/bookings` đang trả `List`; nếu backend phân trang thì đổi sang kiểu `Paged`.
