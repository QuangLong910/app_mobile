#!/bin/sh
# Bật máy chủ giả và mở đường USB cho điện thoại. Chạy từ thư mục mock-server:
#     ./chay.sh
# Ctrl+C để dừng.
set -e
cd "$(dirname "$0")"

PATH="$HOME/Library/Android/sdk/platform-tools:$PATH"
export PATH

# Tắt tiến trình cũ nếu còn, tránh "Address already in use".
pkill -f "server.py" 2>/dev/null || true
sleep 1

if [ -z "$(adb devices | sed -n 2p)" ]; then
    echo "⚠  Chưa thấy điện thoại nào. Cắm cáp, chọn Truyền tệp (MTP), bấm Cho phép gỡ lỗi USB."
    echo "   Máy chủ vẫn chạy; cắm xong mở cửa sổ khác gõ: adb reverse tcp:8080 tcp:8080"
else
    adb reverse tcp:8080 tcp:8080
    echo "✓ Đã mở đường cáp: điện thoại gọi 127.0.0.1:8080 là tới máy này."
fi

# -u để log hiện ngay, không bị Python giữ trong bộ đệm.
exec python3 -u server.py
