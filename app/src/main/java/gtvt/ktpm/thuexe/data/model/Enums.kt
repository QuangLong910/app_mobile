package gtvt.ktpm.thuexe.data.model

/** Các chuỗi hằng backend dùng. Giữ String thay vì enum để tin lạ không làm Gson ném lỗi. */
object BookingStatus {
    const val CHO_CHU_XE = "CHO_CHU_XE"
    const val CHO_THANH_TOAN = "CHO_THANH_TOAN"
    const val DA_XAC_NHAN = "DA_XAC_NHAN"
    const val DANG_THUE = "DANG_THUE"
    const val CHO_QUYET_TOAN = "CHO_QUYET_TOAN"
    const val CHO_CHI_TRA = "CHO_CHI_TRA"
    const val HOAN_TAT = "HOAN_TAT"
    const val BI_TU_CHOI = "BI_TU_CHOI"
    const val HET_HAN = "HET_HAN"
    const val DA_HUY = "DA_HUY"
    /** Biên bản trả bị phản đối, hoặc quá hạn không ai xử lý — đơn đứng chờ sàn phân xử. */
    const val TRANH_CHAP = "TRANH_CHAP"
}

object CarStatus {
    const val NHAP = "NHAP"
    const val CHO_DUYET = "CHO_DUYET"
    const val DANG_BAN = "DANG_BAN"
    const val AN = "AN"
}

object DocStatus {
    /** Backend trả chuỗi này khi người dùng chưa nộp giấy tờ nào. */
    const val CHUA_NOP = "CHUA_NOP"
    const val CHO_DUYET = "CHO_DUYET"
    const val DAT = "DAT"
    const val TU_CHOI = "TU_CHOI"
}

object CarDocType {
    const val DANG_KY = "DANG_KY"
    const val DANG_KIEM = "DANG_KIEM"
    const val BAO_HIEM = "BAO_HIEM"
    const val UY_QUYEN = "UY_QUYEN"
}

object HandoverKind {
    const val GIAO = "GIAO"
    const val TRA = "TRA"
}

object HandoverStatus {
    const val CHO_SOI = "CHO_SOI"
    const val DA_KY = "DA_KY"
    const val PHAN_DOI = "PHAN_DOI"
}

/** Sáu khung ảnh bắt buộc, theo đúng thứ tự màn chụp. */
object PhotoSlot {
    const val TRUOC = "TRUOC"
    const val SAU = "SAU"
    const val TRAI = "TRAI"
    const val PHAI = "PHAI"
    const val TAPLO = "TAPLO"
    const val ODO = "ODO"
    const val KHAC = "KHAC"

    val BAT_BUOC = listOf(TRUOC, SAU, TRAI, PHAI, TAPLO, ODO)
}

object PartySide {
    const val CHU_XE = "CHU_XE"
    const val KHACH = "KHACH"
}

/** Người báo tự chọn khi báo sự cố giữa chuyến — VA_CHAM thì sàn được nhắc xem bảo hiểm xe. */
object LoaiSuCo {
    const val VA_CHAM = "VA_CHAM"
    const val KHAC = "KHAC"
}

object PayoutStatus {
    const val CHO = "CHO"
    const val DA_CHI = "DA_CHI"
    const val LOI = "LOI"
}

object ChargeType {
    const val QUA_GIO = "QUA_GIO"
    const val QUA_KM = "QUA_KM"
    const val NHIEN_LIEU = "NHIEN_LIEU"
    const val HU_HONG = "HU_HONG"
    const val VE_SINH = "VE_SINH"
    const val MAT_PHU_KIEN = "MAT_PHU_KIEN"
    const val PHI_CAU_DUONG = "PHI_CAU_DUONG"
    const val PHI_DO_XE = "PHI_DO_XE"
    const val NGAY_NGHI_SUA = "NGAY_NGHI_SUA"
    /** Phạt nguội phát hiện sau khi đơn đã hoàn tất — cộng thẳng vào khoản nợ. */
    const val PHAT_NGUOI = "PHAT_NGUOI"
}

object Transmission {
    const val SO_SAN = "SO_SAN"
    const val SO_TU_DONG = "SO_TU_DONG"
}

object Fuel {
    const val XANG = "XANG"
    const val DAU = "DAU"
    const val DIEN = "DIEN"
}
