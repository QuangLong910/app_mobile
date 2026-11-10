"""Dữ liệu mẫu: 1 chủ xe, 12 xe, 6 đơn ở các trạng thái khác nhau."""
from datetime import date, datetime, timedelta, timezone

VN = timezone(timedelta(hours=7))


def now():
    return datetime.now(VN).isoformat(timespec="seconds")


def ngay(cach_hom_nay):
    return (date.today() + timedelta(days=cach_hom_nay)).isoformat()


CHU_XE = {
    "id": 1,
    "phone": "0364184928",
    "fullName": "Bùi Thanh Long",
    "email": "long@thuexe.vn",
    "isOwner": True,
    "status": "HOAT_DONG",
    "createdAt": now(),
}

GIAY_TO = {
    "id": 1,
    "cccdNo": "079201000123",
    "gplxNo": "790123456789",
    "gplxClass": "B2",
    "gplxExpiry": "2031-04-18",
    "frontUrl": "/files/mau/cccd-truoc.jpg",
    "backUrl": "/files/mau/cccd-sau.jpg",
    "selfieUrl": None,
    "status": "DAT",
    "rejectReason": None,
    "reviewedAt": now(),
}

DIEU_KHOAN = {
    "version": "1.0",
    "content": (
        "CAM KẾT CHỦ XE — bản 1.0\n\n"
        "1. Xe đăng lên sàn phải thuộc quyền sở hữu của bạn, hoặc có giấy uỷ quyền hợp lệ. "
        "Tên trên đăng ký xe khác CCCD mà không có uỷ quyền thì sàn từ chối duyệt.\n\n"
        "2. Đăng kiểm và bảo hiểm phải còn hiệu lực. Còn 7 ngày là sàn tự ẩn tin xe.\n\n"
        "3. Nhận đơn trong 30 phút. Quá hạn im lặng, đơn tự huỷ và điểm phản hồi của bạn bị hạ.\n\n"
        "4. Huỷ đơn sau khi khách đã chuyển tiền sẽ bị ghi vi phạm; tái phạm thì bị khoá đăng tin.\n\n"
        "5. Sàn giữ toàn bộ tiền thuê và tiền cọc cho tới khi trả xe xong, thu hoa hồng 15% "
        "tiền thuê, chuyển phần còn lại về tài khoản bạn khai dưới đây trong 24 giờ sau khi chốt đơn.\n\n"
        "6. Bạn phải lập biên bản giao xe có đủ 6 ảnh và số ODO trước khi giao chìa khoá. "
        "Không có biên bản thì mọi tranh chấp về sau sàn không bảo vệ được bạn."
    ),
    "signed": False,
    "signedAt": None,
}

_XE_MAU = [
    ("51A-123.45", "Toyota", "Vios", 2021, 5, "SO_TU_DONG", "XANG", 42000, "Quận 1", 620000),
    ("51F-678.90", "Hyundai", "Accent", 2022, 5, "SO_TU_DONG", "XANG", 31000, "Quận 3", 650000),
    ("51G-222.11", "Kia", "Morning", 2020, 5, "SO_SAN", "XANG", 68000, "Quận 7", 480000),
    ("51H-333.22", "Mazda", "CX-5", 2023, 5, "SO_TU_DONG", "XANG", 18000, "Quận 2", 980000),
    ("51K-444.33", "Ford", "Ranger", 2021, 5, "SO_SAN", "DAU", 75000, "Thủ Đức", 1050000),
    ("51L-555.44", "Toyota", "Innova", 2019, 7, "SO_SAN", "XANG", 96000, "Bình Thạnh", 780000),
    ("51M-666.55", "Mitsubishi", "Xpander", 2022, 7, "SO_TU_DONG", "XANG", 27000, "Gò Vấp", 820000),
    ("51N-777.66", "VinFast", "VF e34", 2023, 5, "SO_TU_DONG", "DIEN", 12000, "Quận 1", 900000),
    ("51P-888.77", "Honda", "City", 2021, 5, "SO_TU_DONG", "XANG", 39000, "Tân Bình", 640000),
    ("51R-999.88", "Suzuki", "XL7", 2022, 7, "SO_TU_DONG", "XANG", 33000, "Quận 10", 750000),
    ("51S-101.12", "Toyota", "Fortuner", 2020, 7, "SO_SAN", "DAU", 88000, "Quận 12", 1200000),
    ("51T-121.31", "Hyundai", "Santa Fe", 2023, 7, "SO_TU_DONG", "DAU", 15000, "Phú Nhuận", 1350000),
]

_TRANG_THAI_XE = [
    "DANG_BAN", "DANG_BAN", "DANG_BAN", "DANG_BAN", "DANG_BAN", "DANG_BAN",
    "DANG_BAN", "DANG_BAN", "CHO_DUYET", "CHO_DUYET", "NHAP", "AN",
]


def xe():
    ra = []
    for i, (bien, hang, dong, nam, cho, hop_so, nhien_lieu, odo, quan, gia) in enumerate(_XE_MAU):
        ra.append({
            "id": i + 1,
            "ownerId": 1,
            "plate": bien,
            "brand": hang,
            "model": dong,
            "year": nam,
            "seats": cho,
            "transmission": hop_so,
            "fuel": nhien_lieu,
            "odo": odo,
            "district": quan,
            "pickupAddress": "12 Nguyễn Huệ, %s" % quan,
            "pricePerDay": gia,
            "maxKmDay": 300,
            "deposit": 3000000,
            "description": "%s %s %d, %d chỗ. Xe gia đình, mới bảo dưỡng." % (hang, dong, nam, cho),
            "status": _TRANG_THAI_XE[i],
            "rejectReason": "Ảnh đăng kiểm mờ, chụp lại giúp sàn" if _TRANG_THAI_XE[i] == "AN" else None,
            "createdAt": now(),
            "photos": [
                {"id": i * 10 + 1, "url": "/files/mau/xe-%d.jpg" % (i + 1), "sortOrder": 0},
            ],
            "documents": [
                {"id": i * 10 + 1, "type": "DANG_KY", "url": "/files/mau/dang-ky-%d.jpg" % (i + 1),
                 "expiryDate": None},
                {"id": i * 10 + 2, "type": "DANG_KIEM", "url": "/files/mau/dang-kiem-%d.jpg" % (i + 1),
                 "expiryDate": "2027-03-01"},
            ],
        })
    return ra


def _don(id_, ma, xe_id, bien, hang, dong, ten_khach, sdt, bat_dau, so_ngay, gia, trang_thai):
    tien_thue = gia * so_ngay
    return {
        "id": id_,
        "code": ma,
        "carId": xe_id,
        "car": {"id": xe_id, "plate": bien, "brand": hang, "model": dong,
                "photoUrl": "/files/mau/xe-%d.jpg" % xe_id},
        "renter": {"id": 100 + id_, "fullName": ten_khach,
                   # Số điện thoại chỉ mở sau khi đơn đã xác nhận.
                   "phone": sdt if trang_thai not in ("CHO_CHU_XE", "CHO_THANH_TOAN") else None,
                   "idDocumentStatus": "DAT"},
        "startDate": ngay(bat_dau),
        "endDate": ngay(bat_dau + so_ngay),
        "days": so_ngay,
        "pricePerDay": gia,
        "rentTotal": tien_thue,
        "deposit": 3000000,
        "commission": int(tien_thue * 0.15),
        "status": trang_thai,
        "holdExpiresAt": now() if trang_thai in ("CHO_CHU_XE", "CHO_THANH_TOAN") else None,
        "cancelReason": None,
        "createdAt": now(),
    }


def don():
    return [
        _don(1, "KNM4F2C9", 1, "51A-123.45", "Toyota", "Vios", "Nguyễn Văn An", "0901234567", 2, 3, 620000, "CHO_CHU_XE"),
        _don(2, "KNM7H3K2", 4, "51H-333.22", "Mazda", "CX-5", "Trần Thị Bình", "0912345678", 5, 2, 980000, "CHO_CHU_XE"),
        _don(3, "KNM9P4M5", 2, "51F-678.90", "Hyundai", "Accent", "Lê Hoàng Cường", "0923456789", 1, 4, 650000, "CHO_THANH_TOAN"),
        _don(4, "KNM2R5N8", 6, "51L-555.44", "Toyota", "Innova", "Phạm Minh Dũng", "0934567890", 0, 3, 780000, "DA_XAC_NHAN"),
        _don(5, "KNM5T6Q3", 3, "51G-222.11", "Kia", "Morning", "Vũ Thu Hà", "0945678901", -2, 5, 480000, "DANG_THUE"),
        _don(6, "KNM8W7Z4", 5, "51K-444.33", "Ford", "Ranger", "Đỗ Quang Huy", "0956789012", -9, 4, 1050000, "HOAN_TAT"),
    ]


def chi_tra():
    return [
        {"id": 1, "bookingId": 6, "bookingCode": "KNM8W7Z4", "payeeType": "CHU_XE",
         "bankAccount": "0071000123456", "bankName": "Vietcombank", "amount": 3570000,
         "status": "DA_CHI", "transferRef": "FT2609210012345", "paidAt": now(), "createdAt": now()},
        {"id": 2, "bookingId": 5, "bookingCode": "KNM5T6Q3", "payeeType": "CHU_XE",
         "bankAccount": "0071000123456", "bankName": "Vietcombank", "amount": 2040000,
         "status": "CHO", "transferRef": None, "paidAt": None, "createdAt": now()},
    ]


def tin_tuc():
    """NGOÀI §04 — đường dẫn GET /news chưa có trong bảng API của kế hoạch."""
    return [
        {"id": 1, "tag": "CHÍNH SÁCH",
         "title": "Từ 01/10: chi trả cho chủ xe rút xuống trong 12 giờ",
         "summary": "Sàn rút hạn chuyển tiền từ 24 giờ còn 12 giờ kể từ lúc chốt đơn. "
                    "Kiểm tra lại số tài khoản trong phần Hồ sơ.",
         "imageUrl": "/files/mau/tin-1.jpg", "publishedAt": ngay(-1)},
        {"id": 2, "tag": "HƯỚNG DẪN",
         "title": "Sáu ảnh biên bản giao xe: chụp thế nào cho đủ bằng chứng",
         "summary": "Tranh chấp hay gặp nhất không phải xe hỏng mà là vết này có từ trước. "
                    "Chụp đúng sáu khung là hết đường cãi.",
         "imageUrl": "/files/mau/tin-2.jpg", "publishedAt": ngay(-3)},
        {"id": 3, "tag": "NHẮC VIỆC",
         "title": "Đăng kiểm còn 30 ngày là sàn bắt đầu nhắc",
         "summary": "Còn 7 ngày mà chưa cập nhật thì tin xe bị ẩn khỏi kết quả tìm kiếm. "
                    "Nộp lại ảnh đăng kiểm ngay trong màn sửa xe.",
         "imageUrl": "/files/mau/tin-3.jpg", "publishedAt": ngay(-6)},
        {"id": 4, "tag": "MẸO",
         "title": "Nhận đơn trong 30 phút để không tụt điểm phản hồi",
         "summary": "Quá 30 phút đơn tự hết hạn, lịch nhả ra và điểm phản hồi bị hạ. "
                    "Bật thông báo để không lỡ đơn.",
         "imageUrl": "/files/mau/tin-4.jpg", "publishedAt": ngay(-9)},
        {"id": 5, "tag": "CHÍNH SÁCH",
         "title": "Khách huỷ dưới 24 giờ: bạn vẫn nhận đủ tiền thuê",
         "summary": "Bảng chính sách hoàn tiền mới: trước 7 ngày hoàn 100%, "
                    "3–7 ngày 70%, 24–72 giờ 50%, dưới 24 giờ 0% tiền thuê.",
         "imageUrl": "/files/mau/tin-5.jpg", "publishedAt": ngay(-14)},
    ]
