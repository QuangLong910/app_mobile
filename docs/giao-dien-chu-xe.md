# Giao diện app chủ xe

Mọi màn dựng trên đúng các model ở `data/model` — không có lớp UI-model song song, không
có chỗ nào tự định dạng tiền hay ngày ngoài `ui/widget/form/Format.kt`.

## Widget dùng chung — `ui/widget/form/`

| Widget | Việc |
|---|---|
| `FormField` | Nhãn + ô nhập + dòng lỗi. Mọi ô khác dựng trên nó nên cùng một dáng. |
| `FormTextField` | Ô chữ, một dòng hoặc nhiều dòng. |
| `FormMoneyField` | Tiền. Nhận `Long`, hiện `600.000`, lọc sạch ký tự không phải số — không có đường nào để số thập phân lọt vào. |
| `FormIntField` | Số nguyên có hậu tố (`km`, `chỗ`). |
| `FormDropdownField` | Chọn từ danh sách `(giá trị backend, chữ hiện ra)` — hộp số, nhiên liệu, loại giấy tờ. |
| `FormDateField` | `LocalDate` + DatePicker. Đổi millis bằng `ZoneOffset.UTC`, đổi bằng múi giờ máy là lệch một hôm. |
| `FormSection` | Nhóm các ô thành thẻ có tiêu đề. |
| `InfoRow` | Dòng "nhãn — giá trị" cho màn chỉ đọc. |
| `PrimaryButton` / `SecondaryButton` | Nút gradient của app và nút viền. |
| `LyDoDialog` | Hỏi lý do, rỗng thì không cho gửi. Dùng cho từ chối đơn, huỷ đơn, phản đối biên bản. |
| `LoadingBox` / `EmptyBox` / `ErrorBox` / `ErrorBanner` | Ba trạng thái của mọi danh sách. `ErrorBox` nhận **mã lỗi**, câu chữ do `nhanLoi` quyết định. |
| `BookingStatusChip` / `CarStatusChip` / `PayoutStatusChip` | Trạng thái → chữ tiếng Việt + màu. |
| `PhotoSlotBox` / `SixSlotPhotoGrid` | Chọn ảnh, **nén ngay tại chỗ** (1600px, JPEG 80%) rồi mới trả `ByteArray` ra ngoài. |
| `StatCard` / `ViecCanLamRow` | Ô số liệu và dòng việc cần làm cho màn Tổng quan. |
| `LichThang` | Lịch một tháng của một xe: ngày trống / có đơn / đã khoá / quá khứ, chạm để chọn nhiều ngày. |
| `ChuKyField` | **Ký tay bằng ngón** trên canvas, bấm Xong là kết xuất PNG nền trắng nét đen rồi tải lên qua `POST /files`. Có Xoá và Ký lại. |
| `OwnerScaffold` | Khung chung: tiêu đề, nút lùi, chỗ cho thanh nút dưới đáy. |

`Format.kt` giữ toàn bộ chữ tiếng Việt: `tien()`, `ngay()`, `gioNgay()`, `nhanTrangThaiDon`,
`nhanTrangThaiXe`, `nhanHopSo`, `nhanNhienLieu`, `nhanKhungAnh`, `nhanLoi`. Đổi cách gọi một
trạng thái thì sửa đúng một chỗ.

Mở `FormPreviews.kt` trong Android Studio để xem cả bộ mà không cần chạy app.

## Màn hình — `ui/screens/owner/`

| Màn | Gọi API nào |
|---|---|
| `OwnerBookingsScreen` | `GET /owner/bookings` (3 tab: chờ duyệt / đang chạy / tất cả), `confirm`, `reject`, `cancel` |
| `OwnerCarsScreen` | `GET /owner/cars`, `GET /owner/agreement` |
| `CarFormScreen` | `POST /owner/cars` hoặc `PUT /owner/cars/{id}`, `POST /files` cho từng ảnh |
| `AgreementScreen` | `GET /owner/agreement`, `POST /owner/agreement/accept` |
| `HandoverGiaoScreen` | `POST /bookings/{id}/handovers` — 6 khung ảnh, ODO, 8 nấc nhiên liệu, chữ ký |
| `DashboardScreen` | `GET /me` + `/owner/cars` + `/owner/bookings` + `/owner/payouts` — số liệu tính ở app, không thêm đường dẫn nào |
| `ProfileScreen` | `GET /me`, `/owner/agreement`, `/owner/payouts` — giấy tờ, cam kết, tiền đã nhận và đang chờ, đăng xuất |
| `CarCalendarScreen` | `GET /cars/{id}`, `/cars/{id}/availability`, `PUT /owner/cars/{id}/calendar` — theo dõi một xe và mở/khoá ngày |
| `NewsScreen` | `GET /news` — **ngoài §04**, xem cảnh báo dưới |

Thanh dưới có năm thẻ: Tổng quan · Đơn · Xe · Tin tức · Hồ sơ.
Điều hướng gom trong `OwnerNav.kt`; `MainActivity` chỉ thêm một dòng `ownerGraph(navController)`.
Đường dẫn khai ở `OwnerRoute`, đừng gõ chuỗi tay ở chỗ khác.

## Ba chỗ giao diện tự chặn trước khi gọi mạng

- **Chưa ký cam kết** → nút đăng xe dẫn thẳng sang màn điều khoản, không để backend trả
  `OWNER_AGREEMENT_REQUIRED` rồi mới quay lại.
- **Biên bản thiếu ảnh** → nút nộp tắt, và hiện đúng những khung còn thiếu.
- **Biểu mẫu xe** → biển số, năm trong khoảng 1980–năm nay, giá dưới 100.000 đ bị coi là gõ
  nhầm. Lỗi chỉ hiện sau lần bấm đầu tiên, không mắng người dùng khi họ còn đang gõ.

## Đã chạy thật trên máy

21/09/2026, Xiaomi 24069RA21C (Android 16), app nói chuyện với máy chủ giả ở `mock-server/`:
đăng nhập → `GET /me` → danh sách đơn → **Nhận đơn** (`POST .../confirm`, đơn sang
`CHO_THANH_TOAN`, thẻ tự đổi nút sang "Huỷ đơn") → hộp hỏi lý do khi **Từ chối** →
tab Xe của tôi → màn cam kết → **ký tay** và ảnh chữ ký PNG 1052×421 lên tới máy chủ.

Một lỗi sẵn có đã sửa trong lúc chạy: `ButtonItem` cục bộ ở `LoginScreen.kt` đặt `clickable`
lên Box ngoài rồi phủ một `Button(onClick = {})` lên trên — Button nuốt hết chạm nên **mọi
nút của màn đăng nhập đều chết**. Đã trả `onClick` về cho Button.

## ⚠ Một đường dẫn ngoài hợp đồng

`GET /news` **không có** trong bảng 36 đường dẫn ở §04. Màn Tin tức đang chạy nhờ máy chủ giả.
N1 phải bổ sung đường dẫn này vào `docs/api.md` của kho chung, hoặc nhóm quyết định bỏ màn
Tin tức đi. Trả về: `[{ id, tag, title, summary, imageUrl, publishedAt }]`.

## Còn thiếu

- Màn soi biên bản **TRA** (chủ xe xem ảnh khách chụp, bổ sung hư hỏng mới, ký hoặc phản đối).
  `HandoverViewModel` đã có `boSungAnh`, `ky`, `phanDoi` — còn thiếu phần vẽ.
- Màn chi trả (`GET /owner/payouts`) và màn hồ sơ/giấy tờ.
- Chụp ảnh đang qua thư viện ảnh, chưa nối CameraX.
