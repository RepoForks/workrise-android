package io.reyurnible.android.workrise.infrastructure.realm.dto

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass

@RealmDto
data class CheckItemRealmDto(
        @PrimaryKey
        var id: Long = 0,
        @Index
        var content: String = "",
        @Index
        var checked: Boolean = false
) : RealmObject()
