package gtvt.ktpm.thuexe.ui.screens.owner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import gtvt.ktpm.thuexe.data.model.AgreementInfo
import gtvt.ktpm.thuexe.data.model.Car
import gtvt.ktpm.thuexe.data.model.CarUpsertRequest
import gtvt.ktpm.thuexe.data.net.ApiResult
import gtvt.ktpm.thuexe.data.net.ErrorCode
import gtvt.ktpm.thuexe.data.repo.FileRepository
import gtvt.ktpm.thuexe.data.repo.OwnerCarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class OwnerCarsState(
    val loading: Boolean = false,
    val cars: List<Car> = emptyList(),
    val agreement: AgreementInfo? = null,
    /** Bật lên khi backend trả OWNER_AGREEMENT_REQUIRED — mở màn điều khoản, giữ bản nháp xe (A4). */
    val mustSignAgreement: Boolean = false,
    val errorCode: String? = null,
    val errorMessage: String? = null,
)

@HiltViewModel
class OwnerCarsViewModel @Inject constructor(
    private val cars: OwnerCarRepository,
    private val files: FileRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(OwnerCarsState())
    val state: StateFlow<OwnerCarsState> = _state.asStateFlow()

    fun load() = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = cars.myCars()) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, cars = r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    fun loadAgreement() = viewModelScope.launch {
        when (val r = cars.agreement()) {
            is ApiResult.Ok -> _state.update { it.copy(agreement = r.data, mustSignAgreement = !r.data.signed) }
            is ApiResult.Err -> fail(r)
        }
    }

    fun acceptAgreement(version: String, cccdNo: String, bankAccount: String, bankName: String, signatureUrl: String) =
        viewModelScope.launch {
            when (val r = cars.acceptAgreement(version, cccdNo, bankAccount, bankName, signatureUrl)) {
                is ApiResult.Ok -> _state.update { it.copy(agreement = r.data, mustSignAgreement = false) }
                is ApiResult.Err -> fail(r)
            }
        }

    fun createCar(body: CarUpsertRequest) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = cars.createCar(body)) {
            is ApiResult.Ok -> _state.update { it.copy(loading = false, cars = it.cars + r.data) }
            is ApiResult.Err -> fail(r)
        }
    }

    fun updateCar(carId: Long, body: CarUpsertRequest) = viewModelScope.launch {
        _state.update { it.copy(loading = true, errorCode = null, errorMessage = null) }
        when (val r = cars.updateCar(carId, body)) {
            is ApiResult.Ok -> _state.update { s ->
                s.copy(loading = false, cars = s.cars.map { if (it.id == carId) r.data else it })
            }
            is ApiResult.Err -> fail(r)
        }
    }

    fun blockDays(carId: Long, days: List<LocalDate>) = viewModelScope.launch {
        (cars.blockDays(carId, days) as? ApiResult.Err)?.let(::fail)
    }

    fun openDays(carId: Long, days: List<LocalDate>) = viewModelScope.launch {
        (cars.openDays(carId, days) as? ApiResult.Err)?.let(::fail)
    }

    /** Ảnh đã nén trước khi vào đây; trả về url để nhét vào CarUpsertRequest.photoUrls. */
    suspend fun uploadPhoto(bytes: ByteArray, fileName: String): String? =
        when (val r = files.upload(bytes, fileName)) {
            is ApiResult.Ok -> r.data
            is ApiResult.Err -> { fail(r); null }
        }

    private fun fail(err: ApiResult.Err) = _state.update {
        it.copy(
            loading = false,
            mustSignAgreement = it.mustSignAgreement || err.code == ErrorCode.OWNER_AGREEMENT_REQUIRED,
            errorCode = err.code,
            errorMessage = err.message,
        )
    }
}
