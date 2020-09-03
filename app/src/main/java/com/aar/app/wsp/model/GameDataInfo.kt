package com.aar.app.wsp.model

class GameDataInfo {
    var id = 0
    var name: String? = null
    var duration = 0
    var gridRowCount = 0
    var gridColCount = 0
    var usedWordsCount = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameDataInfo) return false
        return id == other.id &&
            name == other.name &&
            duration == other.duration &&
            gridColCount == other.gridColCount &&
            gridRowCount == other.gridRowCount &&
            usedWordsCount == other.usedWordsCount
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + duration
        result = 31 * result + gridRowCount
        result = 31 * result + gridColCount
        result = 31 * result + usedWordsCount
        return result
    }
}