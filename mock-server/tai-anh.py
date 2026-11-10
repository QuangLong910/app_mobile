#!/usr/bin/env python3
"""
Tải ảnh thật cho dữ liệu mẫu về thư mục uploads/.

Nguồn: Wikimedia Commons (ảnh tự do, ghi nguồn trong anh-nguon.txt). Chạy một lần:

    python3 tai-anh.py

Không chạy cũng không sao — máy chủ tự vẽ ô màu thay thế cho url nào chưa có tệp.
"""
import json
import os
import ssl
import urllib.parse
import urllib.request

THU_MUC = os.path.join(os.path.dirname(os.path.abspath(__file__)), "uploads")
API = "https://commons.wikimedia.org/w/api.php"
UA = {"User-Agent": "BonBonOwnerMock/1.0 (do an sinh vien; lien he: tuan612203@gmail.com)"}

# tên tệp lưu -> từ khoá tìm trên Commons
CAN_TAI = [
    ("xe-1.jpg", "Toyota Vios 2021"),
    ("xe-2.jpg", "Hyundai Accent 2022 sedan"),
    ("xe-3.jpg", "Kia Morning 2020"),
    ("xe-4.jpg", "Mazda CX-5 2023"),
    ("xe-5.jpg", "Ford Ranger 2021"),
    ("xe-6.jpg", "Toyota Innova 2019"),
    ("xe-7.jpg", "Mitsubishi Xpander 2022"),
    ("xe-8.jpg", "VinFast VF e34"),
    ("xe-9.jpg", "Honda City 2021"),
    ("xe-10.jpg", "Suzuki XL7 2022"),
    ("xe-11.jpg", "Toyota Fortuner 2020"),
    ("xe-12.jpg", "Hyundai Santa Fe 2023"),
    ("tin-1.jpg", "car rental cars parked"),
    ("tin-2.jpg", "car scratch bumper damage"),
    ("tin-3.jpg", "vehicle inspection sticker windshield"),
    ("tin-4.jpg", "smartphone mounted car dashboard"),
    ("tin-5.jpg", "car keys hand"),
]


def tim_anh(tu_khoa):
    tham_so = {
        "action": "query",
        "format": "json",
        "generator": "search",
        "gsrsearch": "filetype:bitmap " + tu_khoa,
        "gsrnamespace": "6",
        "gsrlimit": "3",
        "prop": "imageinfo",
        "iiprop": "url|extmetadata",
        "iiurlwidth": "900",
    }
    req = urllib.request.Request(API + "?" + urllib.parse.urlencode(tham_so), headers=UA)
    with urllib.request.urlopen(req, timeout=25, context=ssl.create_default_context()) as r:
        du_lieu = json.load(r)
    trang = (du_lieu.get("query") or {}).get("pages") or {}
    for t in trang.values():
        thong_tin = (t.get("imageinfo") or [{}])[0]
        url = thong_tin.get("thumburl") or thong_tin.get("url")
        # thumburl kèm tham số ?utm_source=... nên phải cắt trước khi xét đuôi tệp
        duong = urllib.parse.urlparse(url or "").path.lower()
        if url and duong.endswith((".jpg", ".jpeg", ".png")):
            meta = thong_tin.get("extmetadata") or {}
            giay_phep = (meta.get("LicenseShortName") or {}).get("value", "?")
            return url, t.get("title", ""), giay_phep
    return None, None, None


def main():
    os.makedirs(THU_MUC, exist_ok=True)
    nguon = []
    for ten, tu_khoa in CAN_TAI:
        dich = os.path.join(THU_MUC, ten)
        if os.path.exists(dich):
            print("  bỏ qua %s (đã có)" % ten)
            continue
        try:
            url, tieu_de, giay_phep = tim_anh(tu_khoa)
            if not url:
                print("  ✗ %s — không tìm thấy ảnh cho '%s'" % (ten, tu_khoa))
                continue
            req = urllib.request.Request(url, headers=UA)
            with urllib.request.urlopen(req, timeout=40) as r:
                du_lieu = r.read()
            with open(dich, "wb") as f:
                f.write(du_lieu)
            nguon.append("%s  ←  %s  [%s]" % (ten, tieu_de, giay_phep))
            print("  ✓ %s (%d KB) — %s" % (ten, len(du_lieu) // 1024, tieu_de))
        except Exception as e:
            print("  ✗ %s — %s" % (ten, e))

    if nguon:
        with open(os.path.join(THU_MUC, "anh-nguon.txt"), "a") as f:
            f.write("\n".join(nguon) + "\n")
        print("\nGhi nguồn vào uploads/anh-nguon.txt")


if __name__ == "__main__":
    main()
