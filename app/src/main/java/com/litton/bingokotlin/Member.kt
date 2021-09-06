package com.litton.bingokotlin

data class Member(
    var uid: String,
    var displayname: String,
    var nickname: String?,
    var avatarId: Int
) {
    constructor() : this("", "", null, 0)

}

















