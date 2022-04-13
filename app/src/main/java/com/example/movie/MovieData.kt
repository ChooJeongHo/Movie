package com.example.movie

class MovieData {
    lateinit var movieNm : String
    lateinit var audiCnt : String
    lateinit var movieCd : String
    var director = ""

    constructor(movieNm: String, audiCnt: String, movieCd: String) {
        this.movieNm = movieNm
        this.audiCnt = audiCnt
        this.movieCd = movieCd
    }
}