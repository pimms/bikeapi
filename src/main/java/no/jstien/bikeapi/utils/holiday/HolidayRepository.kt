package no.jstien.bikeapi.utils.holiday

interface HolidayRepository {
    val holidaysForCurrentYear: List<Holiday>
}
