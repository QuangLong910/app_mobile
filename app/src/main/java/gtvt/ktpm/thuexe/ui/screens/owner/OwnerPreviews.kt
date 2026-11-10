package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import gtvt.ktpm.thuexe.data.model.Booking
import gtvt.ktpm.thuexe.data.model.BookingStatus
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarBrief
import gtvt.ktpm.thuexe.data.model.CarPhoto
import gtvt.ktpm.thuexe.data.model.CarStatus
import gtvt.ktpm.thuexe.data.model.Fuel
import gtvt.ktpm.thuexe.data.model.RenterBrief
import gtvt.ktpm.thuexe.data.model.Transmission
import gtvt.ktpm.thuexe.ui.theme.ColorEAECF3
import gtvt.ktpm.thuexe.ui.theme.UIEnglishEgaleTechTheme
import java.time.LocalDate

private fun donMau(status: String) = Booking(
    id = 1,
    code = "KNM4F2C9",
    carId = 1,
    car = CarBrief(1, "51A-123.45", "Toyota", "Vios"),
    renter = RenterBrief(101, "Nguyễn Văn An", "0901234567"),
    startDate = LocalDate.now().plusDays(2),
    endDate = LocalDate.now().plusDays(5),
    days = 3,
    pricePerDay = 620_000,
    rentTotal = 1_860_000,
    deposit = 15_000_000,
    commission = 279_000,
    status = status,
)

private fun xeMau(status: String) = Car(
    id = 1,
    plate = "51H-333.22",
    brand = "Mazda",
    model = "CX-5",
    year = 2023,
    seats = 5,
    transmission = Transmission.SO_TU_DONG,
    fuel = Fuel.XANG,
    odo = 18_000,
    district = "Quận 2",
    pricePerDay = 980_000,
    status = status,
    photos = listOf(CarPhoto(1, "/files/mau/xe-4.jpg", 0)),
)

@Preview(showBackground = true, name = "Thẻ đơn", widthDp = 400)
@Composable
private fun TheDonPreview() {
    UIEnglishEgaleTechTheme {
        Column(
            modifier = Modifier.background(ColorEAECF3).padding(12.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            BookingCard(donMau(BookingStatus.CHO_CHU_XE), false, {}, {}, {}, {})
            BookingCard(donMau(BookingStatus.DA_XAC_NHAN), false, {}, {}, {}, {})
            BookingCard(donMau(BookingStatus.DANG_THUE), false, {}, {}, {}, {})
        }
    }
}

@Preview(showBackground = true, name = "Thẻ xe", widthDp = 400)
@Composable
private fun TheXePreview() {
    UIEnglishEgaleTechTheme {
        Column(
            modifier = Modifier.background(ColorEAECF3).padding(12.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            CarCard(xeMau(CarStatus.DANG_BAN)) {}
            CarCard(xeMau(CarStatus.CHO_DUYET)) {}
        }
    }
}
