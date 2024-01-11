package com.example.fitnessplaner.adapterNotes

data class NotesModel( //data class автоматически генерирует констуктор класса, setter и getter
    var id: String? = null,
    var title: String,
    var food: String,
    var content:String,
    var date: String
) {

    constructor() : this(null,"", "", "", "")

    fun saveFormatted(): Map<String, Any> {
        // Создаем карту для сохранения в базе данных
        val noteMap = hashMapOf<String, Any>()
        id?.let { noteMap["id"] = it }
        noteMap["title"] = title
        noteMap["content"] = content
        noteMap["food"] = food
        noteMap["date"] = date

        return noteMap
    }
}
