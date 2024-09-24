package com.example.nicardmanagementapp.models

data class EntriesItemModel(
    val id : SampleAppFormEntryEnum,
    var label: String,
    var value: String,
    var placeHolder: String = ""
)

enum class SampleAppFormEntryEnum {
    CARD_ID,
    CARD_TYPE,
    ROOT_URL,
    TOKEN,
    BANK_CODE,
    PIN_LENGTH
}
