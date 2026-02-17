package com.dailymenu.data.model

enum class MemberLevel(
    val displayName: String,
    val level: Int
) {
    FREE("免费会员", 0),
    VIP("VIP会员", 1),
    SVIP("SVIP会员", 2)
}
