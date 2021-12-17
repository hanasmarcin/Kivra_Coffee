package com.hanasmarcin.kivracoffee.model

data class FilterItem(
    val name: String,
    val count: Int,
    var isSelected: Boolean = false
)

enum class FilterType {
    COUNTRY, VARIETY, NOTE, INTENSIFIER
}

enum class SortType {
    DEFAULT, BLEND_NAME, COUNTRY
}

data class CoffeeFilters(
    val countries: List<FilterItem>,
    val varieties: List<FilterItem>,
    val notes: List<FilterItem>,
    val intensifiers: List<FilterItem>,
    val sortType: SortType = SortType.DEFAULT
) {
    fun getCountriesCount() = getCountForList(countries)

    fun getVarietiesCount() = getCountForList(varieties)

    fun getNotesCount() = getCountForList(notes)

    fun getIntensifiersCount() = getCountForList(intensifiers)

    private fun getCountForList(filters: List<FilterItem>) = filters.filter { it.isSelected }.size to filters.size

    fun clearAllFilters() {
        countries.forEach { it.isSelected = false }
        varieties.forEach { it.isSelected = false }
        notes.forEach { it.isSelected = false }
        intensifiers.forEach { it.isSelected = false }
    }

    fun changeFilterItemState(item: FilterItem, filterType: FilterType, isSelected: Boolean) {
        when (filterType) {
            FilterType.COUNTRY -> countries.changeSelection(item.name, isSelected)
            FilterType.INTENSIFIER -> intensifiers.changeSelection(item.name, isSelected)
            FilterType.NOTE -> notes.changeSelection(item.name, isSelected)
            FilterType.VARIETY -> varieties.changeSelection(item.name, isSelected)
        }
    }

    private fun List<FilterItem>.changeSelection(filterName: String, isSelected: Boolean) {
        firstOrNull { it.name.equals(filterName, ignoreCase = true) }?.isSelected = isSelected
    }

    companion object {
        fun fromCoffeeList(coffees: List<CoffeeModel>): CoffeeFilters {
            val countries =
                coffees.groupBy { it.country.trim() }.map { FilterItem(it.key, it.value.size) }
                    .sortedWith(filterComparator)
            val varieties =
                coffees.groupBy { it.variety.trim() }.map { FilterItem(it.key, it.value.size) }
                    .sortedWith(filterComparator)
            val notes =
                coffees.flatMap { it.notes }.groupBy { it.trim() }
                    .map { FilterItem(it.key, it.value.size) }
                    .sortedWith(filterComparator)
            val intensifiers =
                coffees.groupBy { it.intensifier.trim() }.map { FilterItem(it.key, it.value.size) }
                    .sortedWith(filterComparator)
            return CoffeeFilters(countries, varieties, notes, intensifiers)
        }

        fun merge(old: CoffeeFilters, new: CoffeeFilters) = CoffeeFilters(
            merge(new.countries, old.countries),
            merge(new.varieties, old.varieties),
            merge(new.notes, old.notes),
            merge(new.intensifiers, old.intensifiers),
            old.sortType
        )

        private fun merge(listA: List<FilterItem>, listB: List<FilterItem>) = listA.map { item ->
            listB.firstOrNull { item.name.equals(it.name, ignoreCase = true) }
                ?.let { item.copy(isSelected = it.isSelected) } ?: item
        }

        private val filterComparator = Comparator<FilterItem> { filter1, filter2 ->
            if (filter1.count == filter2.count)
                filter1.name.compareTo(filter2.name, ignoreCase = true)
            else
                filter2.count.compareTo(filter1.count)
        }
    }
}

fun List<CoffeeModel>.filter(filters: CoffeeFilters): List<CoffeeModel> {
    val sortType = filters.sortType
    val countries = filters.countries.filter { it.isSelected }.map { it.name }
    val intensifiers = filters.intensifiers.filter { it.isSelected }.map { it.name }
    val varieties = filters.varieties.filter { it.isSelected }.map { it.name }
    return this.filter {
        (countries.isEmpty() || it.country in countries) &&
            (intensifiers.isEmpty() || it.intensifier in intensifiers) &&
            (varieties.isEmpty() || it.variety in varieties)
    }.run {
        if (sortType != SortType.DEFAULT)
            sortedBy { if (sortType == SortType.BLEND_NAME) it.blendName else it.country }
        else this
    }
}