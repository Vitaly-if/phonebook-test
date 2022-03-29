package com.example.phonebooktestapp.models

import android.os.Parcel
import android.os.Parcelable

data class ContactsGroupModel(var id: Long = 0, var name: String = "") : Parcelable {

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        name = parcel.readString().toString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as ContactModel
        if (id != other.groupID) return false
        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    companion object CREATOR : Parcelable.Creator<ContactsGroupModel> {
        override fun createFromParcel(parcel: Parcel): ContactsGroupModel {
            return ContactsGroupModel(parcel)
        }

        override fun newArray(size: Int): Array<ContactsGroupModel?> {
            return arrayOfNulls(size)
        }
    }
}