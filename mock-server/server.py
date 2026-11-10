#!/usr/bin/env python3
"""
Máy chủ giả cho app chủ xe BonBon. Chỉ dùng thư viện chuẩn Python.

    python3 server.py            # nghe 0.0.0.0:8080

Trả đúng 22 đường dẫn app đang gọi, theo đúng quy ước của kế hoạch:
tiền là số nguyên đồng, ngày là "2026-09-12", lỗi nghiệp vụ là {"code","message"}.
Dữ liệu nằm trong bộ nhớ — khởi động lại là về mốc ban đầu.
"""
import json
import os
import re
import sys
from datetime import date, timedelta
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer

import seed

CONG = int(os.environ.get("PORT", "8080"))
THU_MUC_ANH = os.path.join(os.path.dirname(os.path.abspath(__file__)), "uploads")
TOKEN = "gia-lap-jwt-chu-xe-1"

# ---- trạng thái trong bộ nhớ ----
DB = {}


def dung_lai_du_lieu():
    DB.clear()
    DB.update({
        "user": dict(seed.CHU_XE),
        "giay_to": dict(seed.GIAY_TO),
        "dieu_khoan": dict(seed.DIEU_KHOAN),
        "xe": seed.xe(),
        "don": seed.don(),
        "chi_tra": seed.chi_tra(),
        "tin_tuc": seed.tin_tuc(),
        "bien_ban": [],
        "lich_khoa": {},   # car_id -> set ngày bị khoá
        "id_tiep": {"xe": 100, "bien_ban": 1},
    })


dung_lai_du_lieu()


class LoiNghiepVu(Exception):
    def __init__(self, code, message, status=409):
        super().__init__(message)
        self.code = code
        self.message = message
        self.status = status


def tim_xe(car_id):
    for x in DB["xe"]:
        if x["id"] == car_id:
            return x
    raise LoiNghiepVu("CAR_UNAVAILABLE", "Không tìm thấy xe", 404)


def tim_don(booking_id):
    for d in DB["don"]:
        if d["id"] == booking_id:
            return d
    raise LoiNghiepVu("WRONG_STATE", "Không tìm thấy đơn", 404)


def doi_trang_thai(don, tu, den):
    """Mọi phương thức đổi trạng thái mở đầu bằng một câu kiểm tra trạng thái hiện tại."""
    if don["status"] != tu:
        raise LoiNghiepVu("WRONG_STATE",
                          "Đơn đang ở %s, không đổi sang %s được" % (don["status"], den))
    don["status"] = den
    return don


# ---- các điểm cuối ----

def post_register(body, **_):
    print("   [OTP] mã xác thực cho %s là 123456" % body.get("phone"))
    return {}


def post_verify_otp(body, **_):
    if body.get("otp") != "123456":
        raise LoiNghiepVu("OTP_SAI", "Mã OTP không đúng. Mã ở cửa sổ chạy máy chủ giả.", 400)
    return {"token": TOKEN, "user": DB["user"]}


TAI_KHOAN = ("0364184928", "test")


def post_login(body, **_):
    phone = (body.get("phone") or "").strip()
    mat_khau = body.get("password") or ""
    if (phone, mat_khau) != TAI_KHOAN:
        raise LoiNghiepVu("SAI_DANG_NHAP", "Số điện thoại hoặc mật khẩu không đúng", 401)
    return {"token": TOKEN, "user": DB["user"]}


def get_me(**_):
    return {
        "user": DB["user"],
        "idDocument": DB["giay_to"],
        "ownerAgreementSigned": DB["dieu_khoan"]["signed"],
    }


def post_documents(body, **_):
    DB["giay_to"].update({k: v for k, v in body.items() if v is not None})
    DB["giay_to"]["status"] = "CHO_DUYET"
    return DB["giay_to"]


def get_agreement(**_):
    return DB["dieu_khoan"]


def post_agreement_accept(body, **_):
    for truong in ("cccdNo", "bankAccount", "bankName", "signatureUrl"):
        if not body.get(truong):
            raise LoiNghiepVu("THIEU_TRUONG", "Thiếu %s" % truong, 422)
    DB["dieu_khoan"]["signed"] = True
    DB["dieu_khoan"]["signedAt"] = seed.now()
    return DB["dieu_khoan"]


def get_owner_cars(**_):
    return DB["xe"]


def post_owner_cars(body, **_):
    # A4: chưa ký cam kết thì không đăng được xe.
    if not DB["dieu_khoan"]["signed"]:
        raise LoiNghiepVu("OWNER_AGREEMENT_REQUIRED", "Phải ký cam kết chủ xe trước", 409)
    bien = (body.get("plate") or "").strip().upper()
    # A7: biển số trùng bị chặn ở ràng buộc duy nhất.
    if any(x["plate"].upper() == bien for x in DB["xe"]):
        raise LoiNghiepVu("PLATE_TAKEN", "Xe này đã có người đăng", 409)
    DB["id_tiep"]["xe"] += 1
    xe_moi = {
        "id": DB["id_tiep"]["xe"],
        "ownerId": 1,
        "plate": bien,
        "brand": body.get("brand"),
        "model": body.get("model"),
        "year": body.get("year"),
        "seats": body.get("seats"),
        "transmission": body.get("transmission"),
        "fuel": body.get("fuel"),
        "odo": body.get("odo", 0),
        "district": body.get("district"),
        "pickupAddress": body.get("pickupAddress"),
        "pricePerDay": body.get("pricePerDay"),
        "maxKmDay": body.get("maxKmDay", 300),
        "deposit": body.get("deposit", 3000000),
        "description": body.get("description"),
        "status": "CHO_DUYET",
        "rejectReason": None,
        "createdAt": seed.now(),
        "photos": [{"id": None, "url": u, "sortOrder": i}
                   for i, u in enumerate(body.get("photoUrls") or [])],
        "documents": body.get("documents") or [],
    }
    DB["xe"].append(xe_moi)
    return xe_moi


def put_owner_car(body, car_id=None, **_):
    xe = tim_xe(int(car_id))
    for truong in ("brand", "model", "year", "seats", "transmission", "fuel", "odo",
                   "district", "pickupAddress", "pricePerDay", "maxKmDay", "deposit", "description"):
        if truong in body and body[truong] is not None:
            xe[truong] = body[truong]
    if body.get("photoUrls"):
        xe["photos"] = [{"id": None, "url": u, "sortOrder": i}
                        for i, u in enumerate(body["photoUrls"])]
    # Sửa xe đang bán thì phải duyệt lại.
    if xe["status"] == "DANG_BAN":
        xe["status"] = "CHO_DUYET"
    return xe


def put_calendar(body, car_id=None, **_):
    tim_xe(int(car_id))
    khoa = DB["lich_khoa"].setdefault(int(car_id), set())
    for d in body.get("days") or []:
        if body.get("blocked"):
            khoa.add(d)
        else:
            khoa.discard(d)
    return {}


def get_car(car_id=None, **_):
    return tim_xe(int(car_id))


def get_availability(car_id=None, query=None, **_):
    cid = int(car_id)
    tu = date.fromisoformat(query.get("from", [date.today().isoformat()])[0])
    den = date.fromisoformat(query.get("to", [(date.today() + timedelta(days=30)).isoformat()])[0])
    ban = []
    for d in DB["don"]:
        if d["carId"] != cid or d["status"] in ("DA_HUY", "BI_TU_CHOI", "HET_HAN"):
            continue
        bd = date.fromisoformat(d["startDate"])
        kt = date.fromisoformat(d["endDate"])
        n = bd
        while n <= kt:            # KHOÁ CẢ NGÀY TRẢ
            if tu <= n <= den:
                ban.append(n.isoformat())
            n += timedelta(days=1)
    return {
        "carId": cid,
        "from": tu.isoformat(),
        "to": den.isoformat(),
        "busyDays": sorted(set(ban)),
        "blockedDays": sorted(DB["lich_khoa"].get(cid, set())),
    }


def get_owner_bookings(query=None, **_):
    trang_thai = (query or {}).get("status", [None])[0]
    if not trang_thai:
        return DB["don"]
    return [d for d in DB["don"] if d["status"] == trang_thai]


def get_booking(booking_id=None, **_):
    don = tim_don(int(booking_id))
    return {
        "booking": don,
        "payment": None,
        "handovers": [b for b in DB["bien_ban"] if b["bookingId"] == don["id"]],
        "charges": [],
    }


def post_confirm(booking_id=None, **_):
    return doi_trang_thai(tim_don(int(booking_id)), "CHO_CHU_XE", "CHO_THANH_TOAN")


def post_reject(body, booking_id=None, **_):
    don = doi_trang_thai(tim_don(int(booking_id)), "CHO_CHU_XE", "BI_TU_CHOI")
    don["cancelReason"] = body.get("reason")
    return don


def post_cancel(body, booking_id=None, **_):
    don = tim_don(int(booking_id))
    # G4: đã ký biên bản giao thì không cho huỷ nữa.
    if any(b["bookingId"] == don["id"] and b["kind"] == "GIAO" for b in DB["bien_ban"]):
        raise LoiNghiepVu("WRONG_STATE", "Đã có biên bản giao, phải đi qua đường trả xe", 409)
    if don["status"] in ("HOAN_TAT", "DA_HUY", "BI_TU_CHOI"):
        raise LoiNghiepVu("WRONG_STATE", "Đơn đã đóng", 409)
    don["status"] = "DA_HUY"
    don["cancelReason"] = body.get("reason")
    return don


KHUNG_BAT_BUOC = ["TRUOC", "SAU", "TRAI", "PHAI", "TAPLO", "ODO"]


def post_handover(body, booking_id=None, **_):
    don = tim_don(int(booking_id))
    kind = body.get("kind")
    if kind == "GIAO":
        doi_trang_thai(don, "DA_XAC_NHAN", "DA_XAC_NHAN")
    anh = body.get("photos") or []
    thieu = [k for k in KHUNG_BAT_BUOC if k not in {a.get("slot") for a in anh}]
    if thieu or not body.get("signature"):
        raise LoiNghiepVu("HANDOVER_INCOMPLETE",
                          "Còn thiếu: " + ", ".join(thieu or ["chữ ký"]), 422)
    if any(b["bookingId"] == don["id"] and b["kind"] == kind for b in DB["bien_ban"]):
        raise LoiNghiepVu("WRONG_STATE", "Biên bản %s của đơn này đã lập rồi" % kind, 409)
    DB["id_tiep"]["bien_ban"] += 1
    bb = {
        "id": DB["id_tiep"]["bien_ban"],
        "bookingId": don["id"],
        "kind": kind,
        "createdBy": "CHU_XE" if kind == "GIAO" else "KHACH",
        "odo": body.get("odo"),
        "fuelLevel": body.get("fuelLevel"),
        "note": body.get("note"),
        "status": "CHO_SOI",
        "signCreator": body.get("signature"),
        "signReviewer": None,
        "objection": None,
        "reviewedAt": None,
        "createdAt": seed.now(),
        "photos": [{"id": None, "slot": a.get("slot"), "url": a.get("url"),
                    "takenBy": "CHU_XE", "note": a.get("note")} for a in anh],
    }
    DB["bien_ban"].append(bb)
    return bb


def tim_bien_ban(handover_id):
    for b in DB["bien_ban"]:
        if b["id"] == handover_id:
            return b
    raise LoiNghiepVu("WRONG_STATE", "Không tìm thấy biên bản", 404)


def post_handover_photos(body, handover_id=None, **_):
    bb = tim_bien_ban(int(handover_id))
    if bb["status"] != "CHO_SOI":
        raise LoiNghiepVu("WRONG_STATE", "Biên bản đã ký, không bổ sung ảnh được", 409)
    for a in body.get("photos") or []:
        bb["photos"].append({"id": None, "slot": a.get("slot"), "url": a.get("url"),
                             "takenBy": "CHU_XE", "note": a.get("note")})
    return bb


def post_handover_review(body, handover_id=None, **_):
    bb = tim_bien_ban(int(handover_id))
    if bb["status"] != "CHO_SOI":
        raise LoiNghiepVu("WRONG_STATE", "Biên bản này đã xử lý rồi", 409)
    don = tim_don(bb["bookingId"])
    if body.get("agreed"):
        bb["status"] = "DA_KY"
        bb["signReviewer"] = body.get("signature")
        don["status"] = "DANG_THUE" if bb["kind"] == "GIAO" else "CHO_QUYET_TOAN"
    else:
        bb["status"] = "PHAN_DOI"
        bb["objection"] = body.get("objection")
        if bb["kind"] == "GIAO":
            don["status"] = "DA_HUY"
            don["cancelReason"] = body.get("objection")
    bb["reviewedAt"] = seed.now()
    return bb


def get_payouts(**_):
    return DB["chi_tra"]


def get_news(**_):
    """NGOÀI §04 — app chủ xe cần màn tin tức, N1 phải bổ sung đường dẫn này vào docs/api.md."""
    return DB["tin_tuc"]


DUONG_DAN = [
    ("POST", r"^/auth/register$", post_register),
    ("POST", r"^/auth/verify-otp$", post_verify_otp),
    ("POST", r"^/auth/login$", post_login),
    ("GET", r"^/me$", get_me),
    ("POST", r"^/me/documents$", post_documents),
    ("GET", r"^/owner/agreement$", get_agreement),
    ("POST", r"^/owner/agreement/accept$", post_agreement_accept),
    ("GET", r"^/owner/cars$", get_owner_cars),
    ("POST", r"^/owner/cars$", post_owner_cars),
    ("PUT", r"^/owner/cars/(?P<car_id>\d+)$", put_owner_car),
    ("PUT", r"^/owner/cars/(?P<car_id>\d+)/calendar$", put_calendar),
    ("GET", r"^/cars/(?P<car_id>\d+)$", get_car),
    ("GET", r"^/cars/(?P<car_id>\d+)/availability$", get_availability),
    ("GET", r"^/owner/bookings$", get_owner_bookings),
    ("GET", r"^/bookings/(?P<booking_id>\d+)$", get_booking),
    ("POST", r"^/owner/bookings/(?P<booking_id>\d+)/confirm$", post_confirm),
    ("POST", r"^/owner/bookings/(?P<booking_id>\d+)/reject$", post_reject),
    ("POST", r"^/bookings/(?P<booking_id>\d+)/cancel$", post_cancel),
    ("POST", r"^/bookings/(?P<booking_id>\d+)/handovers$", post_handover),
    ("POST", r"^/handovers/(?P<handover_id>\d+)/photos$", post_handover_photos),
    ("POST", r"^/handovers/(?P<handover_id>\d+)/review$", post_handover_review),
    ("GET", r"^/owner/payouts$", get_payouts),
    ("GET", r"^/news$", get_news),
]


def anh_mau_png(ten, rong=640, cao=480):
    """
    Ảnh mẫu cho những url chưa có tệp thật. Sinh PNG chứ không phải SVG:
    Coil không đọc SVG nếu không thêm coil-svg, mà ảnh thật rồi cũng là JPEG.
    Màu suy ra từ tên nên mỗi xe một màu, nhìn là biết ảnh có nạp đúng không.
    """
    import struct
    import zlib

    h = zlib.crc32(ten.encode("utf-8"))
    r, g, b = 120 + h % 90, 120 + (h >> 8) % 90, 140 + (h >> 16) % 90
    vien = (60, 62, 100)

    hang = []
    for y in range(cao):
        diem = bytearray([0])                       # byte lọc của mỗi dòng
        for x in range(rong):
            trong_vien = x < 6 or y < 6 or x >= rong - 6 or y >= cao - 6
            diem += bytes(vien if trong_vien else (r, g, b))
        hang.append(bytes(diem))
    tho = b"".join(hang)

    def khoi(kieu, du_lieu):
        return (struct.pack(">I", len(du_lieu)) + kieu + du_lieu
                + struct.pack(">I", zlib.crc32(kieu + du_lieu) & 0xFFFFFFFF))

    return (b"\x89PNG\r\n\x1a\n"
            + khoi(b"IHDR", struct.pack(">IIBBBBB", rong, cao, 8, 2, 0, 0, 0))
            + khoi(b"IDAT", zlib.compress(tho, 6))
            + khoi(b"IEND", b""))


def tach_multipart(body, content_type):
    """Lấy phần tệp đầu tiên: trả (tên tệp, dữ liệu)."""
    m = re.search(r'boundary=([^;]+)', content_type or "")
    if not m:
        return None, None
    ranh = ("--" + m.group(1).strip('"')).encode()
    for phan in body.split(ranh):
        if b"filename=" not in phan:
            continue
        dau = phan.find(b"\r\n\r\n")
        if dau < 0:
            continue
        ten = re.search(rb'filename="([^"]*)"', phan)
        du_lieu = phan[dau + 4:]
        if du_lieu.endswith(b"\r\n"):
            du_lieu = du_lieu[:-2]
        return (ten.group(1).decode() if ten else "anh.jpg"), du_lieu
    return None, None


class Handler(BaseHTTPRequestHandler):
    protocol_version = "HTTP/1.1"

    def log_message(self, fmt, *args):
        sys.stdout.write("   %s\n" % (fmt % args))

    # -- tiện ích --
    def tra(self, status, obj):
        raw = json.dumps(obj, ensure_ascii=False).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(raw)))
        self.end_headers()
        self.wfile.write(raw)

    def tra_loi(self, e):
        print("   ✗ %s — %s" % (e.code, e.message))
        self.tra(e.status, {"code": e.code, "message": e.message})

    def doc_body(self):
        n = int(self.headers.get("Content-Length") or 0)
        return self.rfile.read(n) if n else b""

    # -- định tuyến --
    def xu_ly(self, method):
        from urllib.parse import urlparse, parse_qs
        u = urlparse(self.path)
        duong = u.path.rstrip("/") or "/"

        if method == "GET" and duong.startswith("/files/"):
            return self.phuc_vu_anh(duong)

        if method == "POST" and duong == "/files":
            return self.nhan_anh()

        for m, mau, ham in DUONG_DAN:
            if m != method:
                continue
            khop = re.match(mau, duong)
            if not khop:
                continue
            body = {}
            raw = self.doc_body()
            if raw:
                try:
                    body = json.loads(raw.decode("utf-8"))
                except ValueError:
                    body = {}
            try:
                ket_qua = ham(body=body, query=parse_qs(u.query), **khop.groupdict())
            except LoiNghiepVu as e:
                return self.tra_loi(e)
            except Exception as e:                      # lỗi lập trình thì nói thẳng
                import traceback
                traceback.print_exc()
                return self.tra(500, {"code": "UNKNOWN", "message": str(e)})
            return self.tra(200, ket_qua)

        self.tra(404, {"code": "KHONG_CO_DUONG_DAN", "message": "%s %s" % (method, duong)})

    def nhan_anh(self):
        raw = self.doc_body()
        ten, du_lieu = tach_multipart(raw, self.headers.get("Content-Type"))
        if du_lieu is None:
            return self.tra(400, {"code": "THIEU_TEP", "message": "Không thấy phần tệp"})
        import hashlib
        ma = hashlib.sha1(du_lieu).hexdigest()[:12]
        duoi = os.path.splitext(ten)[1] or ".jpg"
        os.makedirs(THU_MUC_ANH, exist_ok=True)
        with open(os.path.join(THU_MUC_ANH, ma + duoi), "wb") as f:
            f.write(du_lieu)
        print("   ↑ nhận ảnh %s (%d KB) → /files/%s%s" % (ten, len(du_lieu) // 1024, ma, duoi))
        self.tra(200, {"url": "/files/%s%s" % (ma, duoi)})

    def phuc_vu_anh(self, duong):
        ten = duong[len("/files/"):]
        duong_tep = os.path.join(THU_MUC_ANH, os.path.basename(ten))
        if os.path.isfile(duong_tep):
            with open(duong_tep, "rb") as f:
                du_lieu = f.read()
            kieu = "image/png" if ten.endswith(".png") else "image/jpeg"
        else:
            du_lieu = anh_mau_png(os.path.basename(ten))
            kieu = "image/png"
        self.send_response(200)
        self.send_header("Content-Type", kieu)
        self.send_header("Content-Length", str(len(du_lieu)))
        self.end_headers()
        self.wfile.write(du_lieu)

    def do_GET(self):
        self.xu_ly("GET")

    def do_POST(self):
        self.xu_ly("POST")

    def do_PUT(self):
        self.xu_ly("PUT")


def ip_lan():
    import socket
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    try:
        s.connect(("8.8.8.8", 80))
        return s.getsockname()[0]
    except OSError:
        return "127.0.0.1"
    finally:
        s.close()


if __name__ == "__main__":
    ip = ip_lan()
    print("Máy chủ giả BonBon Owner")
    print("  máy thật  → http://%s:%d" % (ip, CONG))
    print("  giả lập   → http://10.0.2.2:%d" % CONG)
    print("  Tài khoản: %s / %s   ·   OTP luôn là 123456" % TAI_KHOAN)
    print("  %d xe, %d đơn. Ctrl+C để dừng.\n" % (len(DB["xe"]), len(DB["don"])))
    ThreadingHTTPServer(("0.0.0.0", CONG), Handler).serve_forever()
