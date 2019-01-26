package com.dev.pushkar.consochrome

/**
 * Created by pushkar on 1/3/18.
 */

class Highscore {
    var score: Int = 0
    var uid: String

    constructor(score: Int, uid: String) {
        this.score = score
        this.uid = uid
    }

    constructor() {

    }
}
