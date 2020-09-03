package com.aar.app.wsp.model

enum class GameMode(val id: Int) {
    Normal(1), Hidden(2), CountDown(3), Marathon(4);

    companion object {
        @JvmStatic
        fun getById(id: Int): GameMode {
            for (gameMode in values()) {
                if (gameMode.id == id) return gameMode
            }
            return Normal
        }
    }

}